package io.js.J2V8Interop.mixins;

import io.js.J2V8Interop.*;

import com.eclipsesource.v8.*;

import java.util.*;
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

                int __ptr = parameters.getInteger(0);
                int methodHash = parameters.getInteger(1);
                V8Array jsArgs = f.getArray(parameters, 2);

                int jsArgsLength = jsArgs.length();

                Object[] javaArgs = new Object[jsArgsLength];

                for (int i=0; i<jsArgsLength; ++i)
                {
                    V8Object ptrBox = f.getObject(jsArgs, i);
                    javaArgs[i] = javaHeap.getJObjfromJSBox(ptrBox);
                }

                Object thiz = javaHeap.getJObjfromPtr(__ptr);
                Class<?> clazz = thiz.getClass();

                Method m = Arrays.asList(clazz.getDeclaredMethods())
                    .stream()
                    .filter(x -> x.hashCode() == methodHash)
                    .findFirst()
                    .get();

                try {
                    Object javaReturn = m.invoke(thiz, javaArgs);
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
