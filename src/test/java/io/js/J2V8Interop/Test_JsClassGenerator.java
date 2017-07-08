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
public class Test_JsClassGenerator {

    @Test
    public void basic_ClassGen() {
        // NodeJS njs = NodeJS.createNodeJS();
        V8 v8 = V8.createV8Runtime();
        //V8 v8 = njs.getRuntime();

        J2V8Interop.injectInteropRuntime(v8);

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

        TestUtils.runTestScript(v8, "./src/test/resources/js/J2V8Interop/assert-utils.js");
        TestUtils.runTestScript(v8, "./src/test/resources/js/J2V8Interop/Test_JsClassGenerator.js");

        // System.out.println("class A desc: " + v8.executeStringScript("JSON.stringify(Object.getOwnPropertyDescriptors(global.A))"));
        // System.out.println("class B desc: " + v8.executeStringScript("JSON.stringify(Object.getOwnPropertyDescriptors(global.B))"));

        // System.out.println("class A: " + v8.executeObjectScript("global.A"));
        // System.out.println("class B: " + v8.executeObjectScript("global.B"));

        // System.out.println("class A constr: " + v8.executeStringScript("JSON.stringify(Object.getOwnPropertyDescriptors(global.A.constructor))"));
        // System.out.println("class B constr: " + v8.executeStringScript("JSON.stringify(Object.getOwnPropertyDescriptors(global.B.constructor))"));

        //njs.release();
        J2V8Interop.releaseInterop(v8);
        v8.release();

        // File script = new File("./src/test/resources/js/J2V8Interop/Test_JsClassGenerator.js");
        // njs.exec(script, new NodeJsExecCallback() {
        //     public void receiveResult(Object result) {

        //         String debug = v8.executeStringScript("global.A");
        //         System.out.println("DEBUG " + debug);

        //         njs.release();
        //     }

        //     public void receiveError(Throwable error) {

        //         System.out.println("Node.js Error: " + error);
        //         this.notifyAll();

        //         njs.release();
        //     }
        // });

        // while (njs.isRunning()) {
        //     njs.handleMessage();
        // }
    }
}
