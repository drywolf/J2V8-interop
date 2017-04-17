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
public class Test_java_lang_Object {

    static String readFile(String path)
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

    @Test
    public void basic_ClassGen()
    {
        V8 v8 = V8.createV8Runtime();
        J2V8Interop.injectInteropRuntime(v8);

        v8.registerJavaMethod(new JavaVoidCallback()
        {
            public void invoke(final V8Object receiver, final V8Array parameters) {
                String msg = parameters.getString(0);
                System.out.println(msg);
            }
        }, "print");

        String assert_script = readFile("C:/code/J2V8-interop/src/test/resources/js/J2V8Interop/assert-utils.js");
        v8.executeVoidScript(assert_script);

        try {
            String script = readFile("C:/code/J2V8-interop/src/test/resources/js/J2V8Interop/Test_java_lang_Object.js");
            v8.executeVoidScript(script);
        }
        catch (V8ScriptExecutionException e)
        {
            e.printStackTrace();
            String st = e.getJSStackTrace();
            System.out.println(st);
            //Assert.fail(e.getJSMessage());
            throw new RuntimeException(e.getJSMessage(), e);
        }
        finally
        {
            J2V8Interop.releaseInterop(v8);
            v8.release();
        }
    }
}
