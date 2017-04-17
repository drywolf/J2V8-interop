package io.js.J2V8Interop.mixins;

import com.eclipsesource.v8.*;
import io.js.J2V8Interop.*;

public class JavaGetTypeInfoMixin
{
    public static V8Object inject(InteropRuntime runtime)
    {
        V8 v8rt = runtime.getV8Runtime();

        JavaCallback __javaGetTypeInfo = new JavaCallback()
        {
            public V8Value invoke(final V8Object receiver, final V8Array parameters) {
                String className = parameters.getString(0);
                try {
                    Class<?> clazz = Class.forName(className);
                    V8Object typeInfo = JavaTypeInfoGenerator.getJavaTypeInfo(v8rt, clazz);
                    return typeInfo;
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                return V8.getUndefined();
            }
        };
        return v8rt.registerJavaMethod(__javaGetTypeInfo, "__javaGetTypeInfo");
    }
}
