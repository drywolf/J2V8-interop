package io.js.J2V8Interop;

import com.eclipsesource.v8.*;
import java.lang.reflect.*;

public class JavaTypeInfo {

    private Class<?> clazz;
    private V8Object typeInfo;

    // TODO: refactor into static API + Java class registry
    public JavaTypeInfo(V8 runtime, Class clazz) {
        this.clazz = clazz;

        String javaTypeName = clazz.getName();

        System.out.println("JAVA type " + javaTypeName);

        V8Object jsType = new V8Object(runtime);
        V8Object jsTypeName = jsType.add("name", javaTypeName);
        V8Object jsMethods = new V8Object(runtime);
        jsType.add("methods", jsMethods);

        Method[] javaMethods = this.clazz.getDeclaredMethods();

        // TODO: encapsulate in JavaMethodInfo class
        for (Method javaMethod : javaMethods) {

            // TODO: support overloads
            String javaMethodName = javaMethod.getName() + "_" + javaMethod.hashCode();

            System.out.println("JAVA method " + javaMethodName);

            V8Object jsMethod = new V8Object(runtime);
            jsMethods.add(javaMethodName, jsMethod);
            V8Object jsMethodName = jsMethod.add("name", javaMethodName);
            V8Object jsMethodArgs = new V8Object(runtime);
            jsMethod.add("args", jsMethodArgs);

            Parameter[] javaArgs = javaMethod.getParameters();

            // TODO: encapsulate in JavaMethodArgInfo class
            for (Parameter javaArg : javaArgs) {
                String javaArgName = javaArg.getName();

                System.out.println("JAVA arg " + javaArgName);

                V8Object jsArg = new V8Object(runtime);
                jsMethodArgs.add(javaArgName, jsArg);
                V8Object jsArgName = jsArg.add("name", javaArgName);

                Class<?> javaArgType = javaArg.getType();
                String argTypeName = javaArgType.getName();
                
                // TODO: this type should also be available in the JS-side type registry immediately
                // V8Object jsArgType = new JavaTypeInfo(runtime, javaArgType).getTypeInfo();
                V8Object jsArgType = new V8Object(runtime);
                jsArg.add("type", jsArgType);
                jsArgType.add("tempDebugTypeName", argTypeName);
            }
        }

        this.typeInfo = jsType;
    }

    public V8Object getTypeInfo() {
        return this.typeInfo;
    }
}
