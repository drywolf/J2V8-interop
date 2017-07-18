package io.js.J2V8Interop;

import java.lang.reflect.Method;
import java.util.HashMap;

// TODO: this is just used experimentally, the algorithmic usage needs to be clarified/solidified
public class JavaTypeEntry {
    private HashMap<Integer, Method> methods = new HashMap<Integer, Method>();

    public void addMethod(int hash, Method method) {
        methods.put(hash, method);
    }

    public Method getMethod(int hash) {
        return methods.get(hash);
    }
}
