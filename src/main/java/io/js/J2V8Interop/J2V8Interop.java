package io.js.J2V8Interop;

import com.eclipsesource.v8.*;

public class J2V8Interop {

    public static void injectInteropRuntime(NodeJS runtime) {
        injectInteropRuntime(runtime.getRuntime());
    }

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

        inject__javaGetTypeInfo(runtime);
    }

    private static void inject__javaGetTypeInfo(V8 runtime) {
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
                    System.out.println("after typeinfo for " + clazz);
                    return typeInfo;
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                return V8.getUndefined();
            }
        };
        runtime.registerJavaMethod(__javaGetTypeInfo, "__javaGetTypeInfo");
    }

    public static void releaseInterop(NodeJS runtime) {
        releaseInterop(runtime.getRuntime());
    }

    public static void releaseInterop(V8 runtime) {

        String interopHash = runtime.executeStringScript("__runtimeHash");

        if (interopHash == null)
            throw new RuntimeException("Interop runtime was not found");
    }
}
