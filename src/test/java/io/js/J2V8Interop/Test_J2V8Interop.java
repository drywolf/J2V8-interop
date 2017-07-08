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
public class Test_J2V8Interop implements ReferenceHandler {

    private HashSet<V8Value> hm = new HashSet<>();
    private List<String> hm2 = new ArrayList<>();

    public void v8HandleCreated(V8Value object)
    {
        hm.add(object);
    }

    public void v8HandleDisposed(V8Value object)
    {
        hm.remove(object);
    }

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

        v8.addReferenceHandler(this);

        J2V8Interop.injectInteropRuntime(njs);

        v8.registerJavaMethod(new JavaVoidCallback()
        {
            public void invoke(final V8Object receiver, final V8Array parameters) {
                String msg = parameters.getString(0);
                System.out.println(msg);
            }
        }, "print");

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

        //String debug = v8.executeStringScript("JSON.prune(global.JObject, global.prune_options)");
        //System.out.println("DEBUG " + debug);

        Assert.assertEquals(true, v8.executeBooleanScript("global.equals"));
        Assert.assertTrue("hashCode is int", v8.executeScript("global.hashCode") instanceof Integer);
        Assert.assertTrue("Object.toString()", ((String)v8.executeStringScript("global.toString")).startsWith("java.lang.Object@"));

        // TODO: not needed / useful ?!?!
        // v8.executeScript("delete global.JObject");
        // v8.executeScript("global.JObject = undefined");

        // v8.executeScript("delete global.obj");
        // v8.executeScript("global.obj = undefined");

        // v8.executeScript("delete global.JSON.prune");
        // v8.executeScript("global.JSON.prune = undefined");

        // v8.executeScript("global.J2V8.release();");

        // v8.executeScript("delete global.J2V8");
        // v8.executeScript("global.J2V8 = undefined");

        // String script = readFile("./src/test/resources/js/J2V8Interop/Test_J2V8Interop.js", StandardCharsets.UTF_8);

        // V8 v8 = njs.getRuntime();
        // v8.executeVoidScript(script);

        // v8.execute("StaticAnimals.SomeFuncVarargs([myBear, myBear2])");

        //v8.executeVoidScript(Utils.getScriptSource(this.getClass().getClassLoader(), "./js/construction/TestJsClassConstructors.js"));

        //v8.executeVoidScript(ScriptUtils.getScriptSource(this));

        // Assert.assertEquals(v8.executeStringScript("person.name"), "joe");
        // Assert.assertEquals(v8.executeStringScript("jackie.name"), "jackie");
        // Assert.assertEquals(v8.executeBooleanScript("jackie.isAwesome()"), true);

        try {
            J2V8Interop.releaseInterop(njs);

            // v8.executeObjectScript("global.J2V8").release();
            // v8.executeObjectScript("global.J2V8Interop");
            // v8.executeObjectScript("global._");
            // v8.executeObjectScript("global.obj");
            // v8.executeObjectScript("global.__javaGetTypeInfo");
            // v8.executeObjectScript("global.__javaCreateInstance");
            // v8.executeObjectScript("global.__javaCallMethod");

            // V8Object glob = v8.getObject("global");
            // glob.release();
            // V8Object lodash = glob.getObject("_");
            // lodash.release();

            // V8Array arr = new V8Array(v8);
            // Object[] hmx = hm.toArray();
            // for (Object bj : hmx)
            // {
            //     V8Value o = (V8Value)bj;
            //     if (o.isReleased())
            //         continue;

            //     V8Object vo = (V8Object)o;
            //     arr.add("0", vo);
            //     V8Object json = v8.getObject("global").getObject("JSON");
            //     hm2.add(json.executeStringFunction("prune", arr));
            // }
            njs.release();
        }
        catch (Exception e) {
            System.out.println("DBG: " + hm2.size());
            // for (String o : hm2)
            // {
            //     System.out.println("--------> Undisposed: " + o);
            // }
            throw e;
        }
    }
}
