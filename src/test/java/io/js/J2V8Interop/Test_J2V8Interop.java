package io.js.J2V8Interop;

import com.eclipsesource.v8.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import org.junit.*;
import org.junit.rules.*;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class Test_J2V8Interop {

    @Rule
    public ExpectedException injectEx = ExpectedException.none();

    @Test
    public void injectRuntime() {

        NodeJS njs = NodeJS.createNodeJS();

        try {
            J2V8Interop.injectInteropRuntime(njs);
        }
        finally {
            J2V8Interop.releaseInterop(njs);
            njs.release();
        }
    }

    @Test
    public void injectRuntimeTwice() {
        injectEx.expect(RuntimeException.class);
        injectEx.expectMessage("Interop runtime already injected");

        NodeJS njs = NodeJS.createNodeJS();

        try {
            J2V8Interop.injectInteropRuntime(njs);
            J2V8Interop.injectInteropRuntime(njs);
        }
        finally {
            J2V8Interop.releaseInterop(njs);
            njs.release();
        }
    }

    @Test
    public void releaseNonExsitingRuntime()
    {
        injectEx.expect(RuntimeException.class);
        injectEx.expectMessage("Interop runtime was not found");

        NodeJS njs = NodeJS.createNodeJS();

        try {
            J2V8Interop.releaseInterop(njs);
        }
        finally {
            njs.release();
        }
    }

    @Test
    public void releaseRuntimeTwice()
    {
        injectEx.expect(RuntimeException.class);
        injectEx.expectMessage("Interop runtime was not found");

        NodeJS njs = NodeJS.createNodeJS();

        try {
            J2V8Interop.injectInteropRuntime(njs);
            J2V8Interop.releaseInterop(njs);
            J2V8Interop.releaseInterop(njs);
        }
        finally {
            njs.release();
        }
    }

    boolean cont = false;

    @Test // NOTE: temporarily disabled
    public void java_lang_Object_basicInterop() {
        NodeJS njs = NodeJS.createNodeJS();
        V8 v8 = njs.getRuntime();

        J2V8Interop.injectInteropRuntime(njs);

        TestUtils.injectDebugUtils(v8);

        TestUtils.runTestScript(v8, "./src/test/resources/js/J2V8Interop/Test_J2V8Interop.js");

        /*
        // TODO: remove absolute path strings from all files
        File script = new File("./src/test/resources/js/J2V8Interop/Test_J2V8Interop.js");

        System.out.println("BEFORE exec");
        njs.exec(script, new NodeJsExecCallback() {
            public void receiveResult(Object result) {
                System.out.println("Node.js Result: " + result);

                // J2V8Interop.releaseInterop(njs);
                // njs.release();
                System.out.println("EXEC OK");
                cont = true;
            }

            public void receiveError(Throwable error) {
                System.out.println("Node.js Error: " + error);
                System.out.println("EXEC ERROR");
                cont = true;
            }
        });

        while (njs.isRunning()) {
            njs.handleMessage();
        }

        System.out.println("Waiting for EXEC");
        while (!cont) ;

        System.out.println("EXEC arrived");
        */

        //String debug = v8.executeStringScript("JSON.prune(global.JObject, global.prune_options)");
        //System.out.println("DEBUG " + debug);

        Assert.assertEquals(true, v8.executeBooleanScript("global.equals"));
        Assert.assertTrue("hashCode is int", v8.executeScript("global.hashCode") instanceof Integer);
        Assert.assertTrue("Object.toString()", ((String)v8.executeStringScript("global.toString")).startsWith("java.lang.Object@"));

        try {
            J2V8Interop.releaseInterop(njs);
            njs.release();
        }
        catch (Exception e) {
            throw e;
        }
    }
}
