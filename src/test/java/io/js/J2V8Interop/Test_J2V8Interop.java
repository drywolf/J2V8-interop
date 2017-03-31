package io.js.J2V8Interop;

import com.eclipsesource.v8.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

import org.junit.*;
import org.junit.rules.*;

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

    static String readFile(String path, Charset encoding)
    {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    @Test
    public void java_lang_Object_basicInterop() {
        NodeJS njs = NodeJS.createNodeJS();

        J2V8Interop.injectInteropRuntime(njs);

        File script = new File("C:/code/J2V8-interop/src/test/resources/js/J2V8Interop/Test_J2V8Interop.js");
        njs.exec(script, new NodeJsExecCallback() {
            public void receiveResult(Object result) {
                System.out.println("Node.js Result: " + result);

                V8 v8 = njs.getRuntime();
                String debug = v8.executeStringScript("JSON.stringify(global.JObject, null, 2)");
                System.out.println("DEBUG " + debug);

                Assert.assertEquals(true, v8.executeBooleanScript("global.equals"));
                Assert.assertEquals(123456, v8.executeScript("global.hashCode"));
                Assert.assertEquals("hello world!", v8.executeStringScript("global.toString"));

                J2V8Interop.releaseInterop(njs);
            }

            public void receiveError(Throwable error) {
                System.out.println("Node.js Error: " + error);
            }
        });

        // String script = readFile("C:/code/J2V8-interop/src/test/resources/js/J2V8Interop/Test_J2V8Interop.js", StandardCharsets.UTF_8);

        // V8 v8 = njs.getRuntime();
        // v8.executeVoidScript(script);

        // v8.execute("StaticAnimals.SomeFuncVarargs([myBear, myBear2])");
        
        //v8.executeVoidScript(Utils.getScriptSource(this.getClass().getClassLoader(), "./js/construction/TestJsClassConstructors.js"));

        while (njs.isRunning()) {
            njs.handleMessage();
        }

        //v8.executeVoidScript(ScriptUtils.getScriptSource(this));

        // Assert.assertEquals(v8.executeStringScript("person.name"), "joe");
        // Assert.assertEquals(v8.executeStringScript("jackie.name"), "jackie");
        // Assert.assertEquals(v8.executeBooleanScript("jackie.isAwesome()"), true);


    }
}
