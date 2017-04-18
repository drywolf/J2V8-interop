package io.js.J2V8Interop.mixins;

import io.js.J2V8Interop.*;

import com.eclipsesource.v8.*;

import java.util.*;
import java.lang.reflect.*;

public class JavaCreateInstanceMixin
{
    public static V8Object inject(InteropRuntime runtime)
    {
        V8 v8rt = runtime.getV8Runtime();
        JavaObjectHeap javaHeap = runtime.getHeap();

        JavaCallback __javaCreateInstance = new JavaCallback() {
            public V8Value invoke(final V8Object receiver, final V8Array parameters) {
                V8Stackframe f = new V8Stackframe();

                V8Object jsClass = f.getObject(parameters, 0);
                V8Object jsInstance = f.getObject(parameters, 1);
                
                String __javaPackage = jsClass.getString("__javaPackage");
                String __javaClassName = jsClass.getString("__javaClassName");
                //int __javaClassHash = jsClass.getInteger("__javaClassHash");
                
                // Class<?> javaClass = JavaTypeInfoGenerator._types.get(__javaClassHash);
                Class<?> javaClass;
                Constructor<?> javaCtor;

                try {
                    System.out.println("creating Java instance of: " + __javaPackage + "." + __javaClassName);
                    javaClass = Class.forName(__javaPackage + "." + __javaClassName);
                    // TODO: parameter mapping
                    javaCtor = javaClass.getConstructor();
                    Object javaObj = javaCtor.newInstance(new Object[] {});

                    JavaHeapEntry heapEntry = javaHeap.put(javaObj);
                    jsInstance.add("__ptr", heapEntry.javaPtr);
                    return heapEntry.jsPtr;
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
        return v8rt.registerJavaMethod(__javaCreateInstance, "__javaCreateInstance");
    }
}
