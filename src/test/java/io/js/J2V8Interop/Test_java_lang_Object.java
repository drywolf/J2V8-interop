package io.js.J2V8Interop;

import com.eclipsesource.v8.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.text.*;

import org.apache.commons.io.Charsets;

import org.junit.*;
import org.junit.rules.*;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class Test_java_lang_Object {

    private void printDate(String prefix)
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        Date date = new Date();
        System.out.println(prefix + dateFormat.format(date));
    }

    @Test
    public void basic_ClassGen()
    {
        V8 v8 = V8.createV8Runtime();
        J2V8Interop.injectInteropRuntime(v8);

        TestUtils.injectDebugUtils(v8);

        TestUtils.runTestScript(v8, "./src/test/resources/js/J2V8Interop/assert-utils.js");
        TestUtils.runTestScript(v8, "./src/test/resources/js/J2V8Interop/Test_java_lang_Object.js");

        J2V8Interop.releaseInterop(v8);
        v8.release();
    }
}
