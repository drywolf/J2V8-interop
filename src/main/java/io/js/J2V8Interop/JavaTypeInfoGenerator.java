package io.js.J2V8Interop;

import com.eclipsesource.v8.*;
import com.eclipsesource.v8.utils.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class JavaTypeInfoGenerator {

    // TODO: what was the thought for a separate _returnTypes set here, just debugging --> remove !?
    private static HashSet<V8Object> _returnTypes = new HashSet<>();
    private static HashMap<Integer, V8Object> _js_types = new HashMap<>();
    private static HashMap<Integer, Class<?>> _java_types = new HashMap<>();

    public static void release()
    {
        for (V8Object typeInfo : _js_types.values()) {
            if (!typeInfo.isReleased())
                typeInfo.release();
        }
    }

    static
    {
        // TODO: this is a BIG band-aid here, need to figure out the right way to initialize the
        // type hashmap to make sure the hashes are available once they are needed
        _java_types.put(System.identityHashCode(Boolean.class), Boolean.class);
    }

    public static Class<?> getJavaClass(int classHash) {
        Class<?> clazz = _java_types.get(classHash);

        if (clazz == null)
        {
            // debug print known types
            for (Class<?> x : _java_types.values())
                System.out.println(x);

            throw new RuntimeException("Tried to get unavailable java class by hashcode");
        }

        return clazz;
    }

    public static V8Object getJavaTypeInfo(V8 runtime, Class<?> classType) {

        int classHash = System.identityHashCode(classType);
        // String classHashStr = Long.toString(classHash & 0xFFFFFFFFL);

        V8Object typeInfo = _js_types.get(classHash);

        if (typeInfo != null)
            return typeInfo;

        typeInfo = new V8Object(runtime);
        _js_types.put(classHash, typeInfo);
        _java_types.put(classHash, classType);

        //String packageName = classType.getPackage().getName();
        String packageName = "";
        String javaTypeName = classType.getName();
        int classNameStartIdx = javaTypeName.lastIndexOf('.');

        if (classNameStartIdx >= 0)
        {
            packageName = javaTypeName.substring(0, classNameStartIdx);
            javaTypeName = javaTypeName.substring(classNameStartIdx + 1);
        }

        typeInfo.add("package", packageName);
        typeInfo.add("name", javaTypeName);
        typeInfo.add("hash", classHash);
        typeInfo.add("__typehash", classHash); // TODO: consolidate usages of hash vs. __typehash vs. javaClassHash
        // typeInfo.add("hashstr", classHashStr);

        Class<?> superClass = classType.getSuperclass();

        if (superClass == null)
            typeInfo.addNull("extends");
        else
        {
            String superName = superClass.getName();
            // V8Object jsSuper = getJavaTypeInfo(runtime, superClass);
            // typeInfo.add("extends", jsSuper);
            typeInfo.add("extends", superName);
        }

        V8Object jsCtors = new V8Object(runtime);
        runtime.registerResource(jsCtors);
        typeInfo.add("constructors", jsCtors);

        V8Object jsMethods = new V8Object(runtime);
        runtime.registerResource(jsMethods);
        typeInfo.add("methods", jsMethods);

        Constructor[] javaCtors = classType.getDeclaredConstructors();

        for (Constructor javaCtor : javaCtors) {

            // skip private methods
            if (Modifier.isPrivate(javaCtor.getModifiers()))
                continue;

            getJavaCtorInfo(runtime, javaCtor, jsCtors);
        }

        Method[] javaMethods = classType.getDeclaredMethods();

        // DEBUGGING
        // Map<String, Long> counting =
        //     Arrays.asList(javaMethods).stream()
        //     .collect(Collectors.groupingBy(Method::getName, Collectors.counting()))
        //     .entrySet().stream()
        //     .filter(x -> x.getValue() > 1)
        //     .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
        //     .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));

        // if (counting.size() > 0)
        // {
        //     System.out.println("OVERLOADs: " + javaTypeName);
        //     System.out.println("OVERLOADED: " + counting);
        // }

        for (Method javaMethod : javaMethods) {

            // skip private methods
            if (Modifier.isPrivate(javaMethod.getModifiers()))
                continue;

            getJavaMethodInfo(runtime, javaMethod, jsMethods);
        }

        return typeInfo;
    }

    public static V8Object getJavaCtorInfo(V8 runtime, Constructor javaCtor, V8Object jsCtors) {

        // TODO: is this a feasible way to support overloads ??!
        String javaCtorName = "constructor";
        int javaCtorHash = MethodHashing.calculateHash(javaCtor);
        // String javaCtorHashStr = Integer.toString(javaCtorHash);

        V8Object jsCtor = new V8Object(runtime);
        runtime.registerResource(jsCtor);
        jsCtors.add(javaCtorName, jsCtor);
        jsCtor.add("hash", javaCtorHash);
        // jsCtor.add("hashstr", javaCtorHashStr);

        V8Array jsCtorArgs = new V8Array(runtime);
        runtime.registerResource(jsCtorArgs);
        jsCtor.add("args", jsCtorArgs);

        Parameter[] javaArgs = javaCtor.getParameters();

        for (Parameter javaArg : javaArgs)
            getJavaMethodArgInfo(runtime, javaArg, jsCtorArgs);

        return jsCtor;
    }

    public static V8Object getJavaMethodInfo(V8 runtime, Method javaMethod, V8Object jsMethods) {

        // TODO: is this a feasible way to support overloads ??!
        String javaMethodName = javaMethod.getName();
        // System.out.println("Get hash for " + javaMethod);
        int javaMethodHash = MethodHashing.calculateHash(javaMethod);
        // System.out.println("Done for " + javaMethod);
        // String javaMethodHashStr = Integer.toString(javaMethodHash);

        V8Object jsMethod = new V8Object(runtime);
        runtime.registerResource(jsMethod);
        jsMethods.add(javaMethodName, jsMethod);
        jsMethod.add("name", javaMethodName);
        jsMethod.add("hash", javaMethodHash);
        // jsMethod.add("hashstr", javaMethodHashStr);

        getJavaMethodReturnInfo(runtime, javaMethod, jsMethod);

        V8Array jsMethodArgs = new V8Array(runtime);
        runtime.registerResource(jsMethodArgs);
        jsMethod.add("args", jsMethodArgs);

        Parameter[] javaArgs = javaMethod.getParameters();

        for (Parameter javaArg : javaArgs)
            getJavaMethodArgInfo(runtime, javaArg, jsMethodArgs);

        return jsMethod;
    }

    public static V8Object getJavaMethodReturnInfo(V8 runtime, Method javaMethod, V8Object jsMethod) {

        Class<?> javaReturnType = javaMethod.getReturnType();
        V8Object jsReturnType = getJavaTypeInfo(runtime, javaReturnType);
        jsMethod.add("return", jsReturnType);
        _returnTypes.add(jsReturnType);
        return jsReturnType;
    }

    public static V8Object getJavaMethodArgInfo(V8 runtime, Parameter javaArg, V8Array jsMethodArgs) {

        String javaArgName = javaArg.getName();

        V8Object jsArg = new V8Object(runtime);
        runtime.registerResource(jsArg);
        jsMethodArgs.push(jsArg);
        jsArg.add("name", javaArgName);

        Class<?> javaArgType = javaArg.getType();
        //String argTypeName = javaArgType.getName();

        // TODO: make sure this type is also available in the JS-side type registry immediately
        V8Object jsArgType = getJavaTypeInfo(runtime, javaArgType);
        jsArg.add("type", jsArgType);

        return jsArg;
    }
}
