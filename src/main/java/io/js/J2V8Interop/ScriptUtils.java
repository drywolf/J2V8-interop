package io.js.J2V8Interop;

import java.io.*;
import org.apache.commons.io.IOUtils;

public class ScriptUtils {
    public static <T> String getScriptSource(T thiz) {
        Class<?> cls = thiz.getClass();
        return getScriptSource(cls.getClassLoader(), cls.getName() + ".js");
    }
    public static <T> String getScriptSource(T thiz, String path) {
        return getScriptSource(thiz.getClass().getClassLoader(), path);
    }
    
    public static String getScriptSource(ClassLoader classLoader, String path) {
        InputStream in = classLoader.getResourceAsStream(path);
        try {
            return IOUtils.toString(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
