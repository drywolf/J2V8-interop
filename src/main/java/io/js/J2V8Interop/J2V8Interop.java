package io.js.J2V8Interop;

import com.eclipsesource.v8.*;
import java.util.*;
import java.lang.reflect.*;

public class J2V8Interop {

    public static void injectInteropRuntime(NodeJS runtime) {
        injectInteropRuntime(runtime.getRuntime());
    }

    static V8Object __javaGetTypeInfo;
    static V8Object __javaCreateInstance;
    static V8Object __javaCallMethod;

    public static void injectInteropRuntime(V8 runtime) {
        
        Object resultObj = runtime.executeScript("typeof __runtimeHash === 'undefined' ? null : __runtimeHash");

        if (resultObj != null) {
            String interopHash = (String)resultObj;

            if (interopHash != null)
                throw new RuntimeException("Interop runtime already injected");
        }

        runtime.executeVoidScript("__runtimeHash = '" + runtime.hashCode() + "';");

        ClassLoader cl = J2V8Interop.class.getClassLoader();

        String script = ScriptUtils.getScriptSource(cl, "J2V8Interop.js");
        runtime.executeVoidScript(script);

        // TODO: move each of those to separate Mixins classes
        __javaGetTypeInfo = inject__javaGetTypeInfo(runtime);
        __javaCreateInstance = inject__javaCreateInstance(runtime);
        __javaCallMethod = inject__javaCallMethod(runtime);
    }

    private static V8Object inject__javaGetTypeInfo(V8 runtime) {
        JavaCallback __javaGetTypeInfo = new JavaCallback() {
            public V8Value invoke(final V8Object receiver, final V8Array parameters) {
                String className = parameters.getString(0);
                int type = parameters.getType(0);
                String typeS = V8.getStringRepresentaion(type);
                try {
                    System.out.println("getting java class-len: " + parameters.length());
                    System.out.println("getting java class-type: " + typeS);
                    System.out.println("getting java class: >" + className + "<");

                    Class<?> clazz = Class.forName(className);

                    V8Object typeInfo = JavaTypeInfoGenerator.getJavaTypeInfo(runtime, clazz);
                    return typeInfo;
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                return V8.getUndefined();
            }
        };
        return runtime.registerJavaMethod(__javaGetTypeInfo, "__javaGetTypeInfo");
    }

    public static class JavaHeapEntry {
        private Object javaObject;
        private V8Object jsPtr;
        public JavaHeapEntry(V8 runtime, Object javaObject) {
            this.javaObject = javaObject;
            this.jsPtr = new V8Object(runtime);
            this.jsPtr.add("__javaPtr", javaObject.hashCode());
        }
    }

    private static HashMap<Integer, JavaHeapEntry> javaHeap = new HashMap<>();

    private static JavaHeapEntry get(int hash) {
        return javaHeap.get(hash);
    }

    private static V8Object inject__javaCreateInstance(V8 runtime) {
        JavaCallback __javaCreateInstance = new JavaCallback() {
            public V8Value invoke(final V8Object receiver, final V8Array parameters) {
                V8Stackframe f = new V8Stackframe();

                V8Object jsClass = f.getObject(parameters, 0);
                V8Object jsInstance = f.getObject(parameters, 1);

                // V8Object jsClass = parameters.getObject(0);
                // V8Object jsInstance = parameters.getObject(1);
                // runtime.registerResource(jsClass);
                // runtime.registerResource(jsInstance);
                
                String __javaPackage = jsClass.getString("__javaPackage");
                String __javaClassName = jsClass.getString("__javaClassName");
                int __javaClassHash = jsClass.getInteger("__javaClassHash");
                System.out.println("-------> __javaCreateInstance " + __javaClassName);
                
                // Class<?> javaClass = JavaTypeInfoGenerator._types.get(__javaClassHash);
                Class<?> javaClass;
                Constructor<?> javaCtor;

                try {
                    javaClass = Class.forName(__javaPackage + "." + __javaClassName);
                    // TODO: parameter mapping
                    javaCtor = javaClass.getConstructor();
                    Object javaInstance = javaCtor.newInstance(new Object[] {});

                    int javaInstanceHash = javaInstance.hashCode();
                    JavaHeapEntry entry = new JavaHeapEntry(runtime, javaInstance);
                    javaHeap.put(javaInstanceHash, entry);
                    System.out.println("CREATED JAVA INSTANCE ON HEAP " + javaInstanceHash);
                    jsInstance.add("__javaPtr", javaInstanceHash);
                    return entry.jsPtr;
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
        return runtime.registerJavaMethod(__javaCreateInstance, "__javaCreateInstance");
    }

    private static V8Object inject__javaCallMethod(V8 runtime) {
        JavaCallback __javaCallMethod = new JavaCallback() {
            public V8Value invoke(final V8Object receiver, final V8Array parameters) {
                int __javaPtr = parameters.getInteger(0);
                int methodHash = parameters.getInteger(1);

                V8Stackframe f = new V8Stackframe();

                V8Array jsArgs = f.getArray(parameters, 2);
                // V8Array jsArgs = parameters.getArray(2);

                //runtime.registerResource(jsArgs);
                int jsArgsLength = jsArgs.length();

                // System.out.println("calling java method:");
                // System.out.println("javaPtr: " + __javaPtr);
                // System.out.println("methodHash: " + methodHash);
                // System.out.println("numArgs: " + jsArgsLength);

                Object[] javaArgs = new Object[jsArgsLength];

                for (int i=0; i<jsArgsLength; ++i)
                {
                    V8Object javaPtrBox = f.getObject(jsArgs, i);
                    // runtime.registerResource(javaPtrBox);
                    int javaPtr = javaPtrBox.getInteger("__javaPtr");
                    JavaHeapEntry javaArg = javaHeap.get(javaPtr);
                    javaArgs[i] = javaArg.javaObject;
                    // System.out.println("Arg: " + javaArg.javaObject.hashCode());
                }

                // jsArgs.release();

                Object thiz = javaHeap.get(__javaPtr).javaObject;
                // System.out.println("Thiz: " + thiz.hashCode());
                
                Class<?> clazz = thiz.getClass();

                // Arrays.asList(clazz.getDeclaredMethods())
                //     .stream()
                //     .forEach(x -> System.out.println("m-hash " + x.getName() + " -> " + x.hashCode()));

                Method m = Arrays.asList(clazz.getDeclaredMethods())
                    .stream()
                    .filter(x -> x.hashCode() == methodHash)
                    .findFirst()
                    .get();

                try {
                    Object javaReturn = m.invoke(thiz, javaArgs);
                    // TODO: box return to JS
                    V8Object jsReturn = new V8Object(runtime);
                    // System.out.println("Return: " + javaReturn);
                    jsReturn.add("value", (boolean)javaReturn);
                    // runtime.registerResource(jsReturn);
                    return jsReturn;
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
        return runtime.registerJavaMethod(__javaCallMethod, "__javaCallMethod");
    }

    public static void releaseInterop(NodeJS runtime) {
        releaseInterop(runtime.getRuntime());
    }

    public static void releaseInterop(V8 runtime) {

        // safeRelease(__javaGetTypeInfo);
        // safeRelease(__javaCreateInstance);
        // safeRelease(__javaCallMethod);

        String interopHash = runtime.executeStringScript("__runtimeHash");

        if (interopHash == null)
            throw new RuntimeException("Interop runtime was not found");

        // V8Object global = runtime.getObject("global");

        //runtime.executeScript("global.J2V8.release();");

        // V8Object j2v8_interop = global.getObject("J2V8Interop");
        // global.addUndefined("J2V8Interop");
        // if (!j2v8_interop.isReleased())
        //     j2v8_interop.release();

        JavaTypeInfoGenerator.release();

        // System.out.println("Releasing Heap " + javaHeap.size());
        for (JavaHeapEntry entry : javaHeap.values()) {
            entry.jsPtr.release();
        }

        // if (!global.isReleased())
        //     global.release();
    }
}
