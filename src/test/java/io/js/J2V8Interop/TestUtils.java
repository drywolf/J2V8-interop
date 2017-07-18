package io.js.J2V8Interop;

import java.io.*;
import java.nio.file.*;

import com.eclipsesource.v8.*;

import org.apache.commons.io.Charsets;
import org.junit.Assert;

class TestUtils {
    private static String readFile(String path)
    {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, Charsets.UTF_8);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public static void runTestScript(V8 v8, String script_path)
    {
        try {
            String script = readFile(script_path);
            v8.executeVoidScript(script);
        }
        catch (V8ScriptExecutionException e)
        {
            e.printStackTrace();
            String st = e.getJSStackTrace();
            System.out.println("JS stacktrace: " + st);
            Assert.fail(e.getJSMessage());
        }
    }

    public static void injectDebugUtils(V8 v8)
    {
        v8.registerJavaMethod(new JavaVoidCallback()
        {
            public void invoke(final V8Object receiver, final V8Array parameters) {
                String msg = parameters.getString(0);
                System.out.println(msg);
            }
        }, "printDebug");
    }
}
