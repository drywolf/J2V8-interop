package io.js.J2V8Interop;

import io.js.J2V8Interop.mixins.*;

import com.eclipsesource.v8.*;

import javassist.compiler.ast.NewExpr;

import java.util.*;
import java.lang.reflect.*;

public class J2V8Interop {

    private static HashMap<V8, InteropRuntime> runtimes = new HashMap<>();

    public static void injectInteropRuntime(NodeJS runtime) {
        injectInteropRuntime(runtime.getRuntime());
    }

    public static InteropRuntime injectInteropRuntime(V8 runtime) {
        
        Object resultObj = runtime.executeScript("typeof __runtimeHash === 'undefined' ? null : __runtimeHash");

        final String alreadyInjectedMsg = "Interop runtime already injected";

        if (resultObj != null) {
            String interopHash = (String)resultObj;

            if (interopHash != null)
                throw new RuntimeException(alreadyInjectedMsg);
        }

        boolean rtExists = runtimes.containsKey(runtime);

        if (rtExists)
            throw new RuntimeException(alreadyInjectedMsg);

        runtime.executeVoidScript("__runtimeHash = '" + runtime.hashCode() + "';");

        InteropRuntime rt =new InteropRuntime(runtime);
        runtimes.put(runtime, rt);

        ClassLoader cl = J2V8Interop.class.getClassLoader();

        String script = ScriptUtils.getScriptSource(cl, "J2V8Interop.js");
        runtime.executeVoidScript(script);

        JavaGetTypeInfoMixin.inject(rt);
        JavaCreateInstanceMixin.inject(rt);
        JavaCallMethodMixin.inject(rt);

        return rt;
    }

    public static void releaseInterop(NodeJS runtime)
    {
        releaseInterop(runtime.getRuntime());
    }

    public static void releaseInterop(InteropRuntime runtime)
    {
        releaseInterop(runtime.getV8Runtime());
    }

    public static void releaseInterop(V8 runtime)
    {
        String interopHash = null;

        try {
            interopHash = runtime.executeStringScript("__runtimeHash");
        }
        catch (V8ScriptExecutionException e)
        {
        }

        final String notFoundMsg = "Interop runtime was not found";

        if (interopHash == null)
            throw new RuntimeException(notFoundMsg);

        InteropRuntime rt = runtimes.get(runtime);

        if (rt == null)
            throw new RuntimeException(notFoundMsg);

        runtime.executeVoidScript("delete __runtimeHash");

        JavaTypeInfoGenerator.release();

        JavaObjectHeap javaHeap = rt.getHeap();
        javaHeap.release();

        runtimes.remove(runtime);
    }
}
