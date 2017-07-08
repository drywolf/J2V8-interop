package io.js.J2V8Interop;

import com.eclipsesource.v8.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

import org.apache.commons.io.Charsets;

import org.junit.*;
import org.junit.rules.*;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class Test_JsClassGenerator2 {

    @Test
    public void basic_ClassGen() {
        // NodeJS njs = NodeJS.createNodeJS();
        V8 v8 = V8.createV8Runtime();

        J2V8Interop.injectInteropRuntime(v8);

        //V8 v8 = njs.getRuntime();

        // v8.registerJavaMethod(new JavaVoidCallback()
        // {
        //     public void invoke(final V8Object receiver, final V8Array parameters) {
        //         String expected = parameters.getString(0);
        //         String actual = parameters.getString(1);
        //         Assert.assertEquals(expected, actual);
        //     }
        // }, "assert");

        v8.registerJavaMethod(new JavaVoidCallback()
        {
            public void invoke(final V8Object receiver, final V8Array parameters) {
                String msg = parameters.getString(0);
                System.out.println(msg);
            }
        }, "print");

        // ClassLoader cl = J2V8Interop.class.getClassLoader();

        // String boot_script = ScriptUtils.getScriptSource(cl, "J2V8Interop.js");
        // v8.executeVoidScript(boot_script);

        TestUtils.runTestScript(v8, "./src/test/resources/js/J2V8Interop/Test_JsClassGenerator2.js");

        J2V8Interop.releaseInterop(v8);
        v8.release();
    }
}
