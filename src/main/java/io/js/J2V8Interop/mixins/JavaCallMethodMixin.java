package io.js.J2V8Interop.mixins;

import io.js.J2V8Interop.*;

import com.eclipsesource.v8.*;

import java.util.*;
import java.util.stream.Collectors;

import java.lang.reflect.*;

public class JavaCallMethodMixin
{
    public static V8Object inject(InteropRuntime runtime)
    {
        V8 v8rt = runtime.getV8Runtime();
        JavaObjectHeap javaHeap = runtime.getHeap();

        JavaCallback __javaCallMethod = new JavaCallback()
        {
            public V8Value invoke(final V8Object receiver, final V8Array parameters) {
                V8Stackframe f = new V8Stackframe();

                int __thisPtr = parameters.getInteger(0);
                String jsMethodName = parameters.getString(1);
                int jsArgsHash = parameters.getInteger(2);
                V8Array jsArgs = f.getArray(parameters, 3);

                System.out.println("Looking for method -> " + jsMethodName + " -> " + jsArgsHash);

                int jsArgsLength = jsArgs.length();

                // Object[] javaArgs = new Object[jsArgsLength];
                JavaObjectHeap.JObj[] args = new JavaObjectHeap.JObj[jsArgsLength];

                for (int i=0; i<jsArgsLength; ++i)
                {
                    V8Object ptrBox = f.getObject(jsArgs, i);
                    JavaObjectHeap.JObj jobj = javaHeap.getJObjfromJSBox(ptrBox);
                    args[i] = jobj;
                    // System.out.println("arg" + i + ": " + javaArgs[i].getClass());
                }

                Object thiz = javaHeap.getJObjfromPtr(__thisPtr);
                Class<?> clazz = thiz.getClass();

                try {

                    Method method = null;

                    System.out.println("jsArgsHash -> " + jsArgsHash);

                    // fast path
                    // TODO: fixed hash value if method has no parameters (-1 or int.minval ?!)
                    // TODO: this will actually be != 0, but for prototyping we skip that for now
                    // if (jsArgsHash != 0) {
                    //     method = JavaTypeRegistry.Instance.getClassMethodByHash(clazz, jsArgsHash);

                    //     if (method != null)
                    //         System.out.println("Reused method overload from cache");
                    // }

                    // slow path (method overload lookup)
                    if (method == null) {
                        List<Method> baseCandidates = Arrays.asList(clazz.getMethods())
                            .stream()
                            // TODO: filter for name / length mismatch separately, then try to match actual arguments in two steps
                            .filter(x ->
                            {
                                if (!x.getName().equals(jsMethodName))
                                {
                                    //System.out.println("Skipping method(n): " + x.getName() + " != " + jsMethodName);
                                    return false;
                                }

                                Parameter[] params = x.getParameters();

                                // parameter-list length mismatch
                                if (params.length != args.length)
                                {
                                    //System.out.println("Skipping method(l): " + params.length + " != " + javaArgs.length);
                                    return false;
                                }

                                return true;
                            })
                            .collect(Collectors.toList());

                        // TODO: think if this is the way to do it or is there a better way?
                        // TODO: if this is kept, then introduce a cache for methods indexed by their hash-codes
                        System.out.println("Looking for match -> " + jsMethodName + " -> " + jsArgsHash);
                        List<Method> candidates = baseCandidates
                            .stream()
                            .filter(x ->
                            {
                                Parameter[] params = x.getParameters();

                                for (int i=0; i<params.length; ++i) {
                                    Parameter p = params[i];
                                    JavaObjectHeap.JObj argx = args[i];
                                    Class<?> valType = JavaTypeInfoGenerator.getJavaClass(argx.__typehash);

                                    Class<?> paramType = p.getType();

                                    HashMap<Class<?>, Class<?>> hm = new HashMap<>();
                                    hm.put(Boolean.class, boolean.class);
                                    hm.put(Byte.class, byte.class);
                                    hm.put(Character.class, char.class);
                                    hm.put(Double.class, double.class);
                                    hm.put(Float.class, float.class);
                                    hm.put(Integer.class, int.class);
                                    hm.put(Long.class, long.class);
                                    hm.put(Short.class, short.class);
                                    hm.put(Void.class, void.class);

                                    Class<?> bt = hm.get(valType);

                                    if (!paramType.equals(valType) && (bt == null || !paramType.equals(bt)))
                                    {
                                        System.out.println("Skipping method(na): " + paramType + " <!=> " + valType + "/" + bt);
                                        return false;
                                    }
                                }

                                return true;
                            })
                            .collect(Collectors.toList());

                            if (candidates.size() == 0) {
                                candidates = baseCandidates
                                    .stream()
                                    .filter(x ->
                                    {
                                        Parameter[] params = x.getParameters();

                                        for (int i=0; i<params.length; ++i) {
                                            Parameter p = params[i];
                                            // Object arg = javaArgs[i];
                                            JavaObjectHeap.JObj argx = args[i];
                                            Class<?> valType = JavaTypeInfoGenerator.getJavaClass(argx.__typehash);

                                            Class<?> paramType = p.getType();
                                            // TODO: what about "null" arg value ?
                                            // TODO: need utility method to check if "null" is assignable to builtin & class types
                                            if (!paramType.isAssignableFrom(valType))
                                            {
                                                //System.out.println("Skipping method(na): " + paramType + " <!=> " + arg.getClass());
                                                return false;
                                            }
                                        }

                                        return true;
                                    })
                                    .collect(Collectors.toList());
                            }
                            // .filter(x -> MethodHashing.calculateHash(x) == jsMethodHash)
                            // .collect(CollectorEx.singletonCollector());

                        if (candidates.size() == 0) {
                            throw new Exception("Unable to match JS method call to any Java method. TODO print details");
                        }

                        if (candidates.size() != 1) {
                            throw new Exception("Multiple matching overloads for JS method call. TODO print details");
                        }

                        method = candidates.get(0);
                        System.out.println("Found method candidate: " + method);

                        // TODO: fast-path register method for reuse
                        // if (jsArgsHash != 0) {
                        //     JavaTypeRegistry.Instance.addClassMethodByHash(clazz, jsArgsHash, method);
                        //     System.out.println("Put method overload into cache");
                        // }

                        // TODO: finish method caching for fast-path
                        // jsMethodHash = 0;
                        // for (int i=0; i<jsArgsLength; ++i)
                        // {
                        //     V8Object ptrBox = f.getObject(jsArgs, i);
                        //     int argTypehash = javaHeap.getJTypehashfromJSBox(ptrBox);
                        //     jsMethodHash ^= argTypehash;
                        // }
                    }

                    Object[] javaArgs = Arrays.asList(args)
                        .stream()
                        .map(x -> x.__val)
                        .toArray();

                    System.out.println("Invoking ... " + method + " [" + javaArgs.length + "]");
                    Object javaReturn = method.invoke(thiz, javaArgs);
                    V8Object returnBox = InteropBoxUtils.boxJavaObject(javaHeap, v8rt, javaReturn);
                    return returnBox;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                finally
                {
                    f.close();
                }
            }
        };
        return v8rt.registerJavaMethod(__javaCallMethod, "__javaCallMethod");
    }
}
