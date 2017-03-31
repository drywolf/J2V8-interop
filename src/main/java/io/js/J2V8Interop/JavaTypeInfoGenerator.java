package io.js.J2V8Interop;

import com.eclipsesource.v8.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class JavaTypeInfoGenerator {

    private static HashMap<Class<?>, V8Object> _types = new HashMap<>();

    public static V8Object getJavaTypeInfo(V8 runtime, Class<?> clazzType) {

        V8Object typeInfo = _types.get(clazzType);

        if (typeInfo != null)
            return typeInfo;

        typeInfo = new V8Object(runtime);
        _types.put(clazzType, typeInfo);

        String javaTypeName = clazzType.getName();

        typeInfo.add("name", javaTypeName);
        V8Object jsMethods = new V8Object(runtime);
        typeInfo.add("methods", jsMethods);

        Method[] javaMethods = clazzType.getDeclaredMethods();

        // DEBUGGING
        Map<String, Long> counting =
            Arrays.asList(javaMethods).stream()
            .collect(Collectors.groupingBy(Method::getName, Collectors.counting()))
            .entrySet().stream()
            .filter(x -> x.getValue() > 1)
            .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
            .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));

        if (counting.size() > 0)
            System.out.println("OVERLOADED: " + counting);

        for (Method javaMethod : javaMethods) {

            // skip private methods
            if (Modifier.isPrivate(javaMethod.getModifiers()))
                continue;

            getJavaMethodInfo(runtime, javaMethod, jsMethods);
        }

        return typeInfo;
    }

    public static V8Object getJavaMethodInfo(V8 runtime, Method javaMethod, V8Object jsMethods) {

        // TODO: is this a feasible way to support overloads ??!
        String javaMethodName = javaMethod.getName() + "_" + javaMethod.hashCode();

        V8Object jsMethod = new V8Object(runtime);
        jsMethods.add(javaMethodName, jsMethod);
        jsMethod.add("name", javaMethodName);

        getJavaMethodReturnInfo(runtime, javaMethod, jsMethod);

        V8Array jsMethodArgs = new V8Array(runtime);
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
        return jsReturnType;
    }

    public static V8Object getJavaMethodArgInfo(V8 runtime, Parameter javaArg, V8Array jsMethodArgs) {

        String javaArgName = javaArg.getName();

        V8Object jsArg = new V8Object(runtime);
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
