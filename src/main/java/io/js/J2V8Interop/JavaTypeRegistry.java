package io.js.J2V8Interop;

import java.lang.reflect.Method;
import java.util.HashMap;

// TODO: this is just used experimentally, the algorithmic usage needs to be clarified/solidified
public class JavaTypeRegistry {
    private HashMap<Class<?>, JavaTypeEntry> types = new HashMap<Class<?>, JavaTypeEntry>();

    public JavaTypeEntry addClassMethodByHash(Class<?> clazz, int methodHash, Method method) {
        JavaTypeEntry typeinfo = this.types.get(clazz);

        if (typeinfo == null) {
            typeinfo = new JavaTypeEntry();
            this.types.put(clazz, typeinfo);
        }

       typeinfo.addMethod(methodHash, method);
        return typeinfo;
    }
    public Method getClassMethodByHash(Class<?> clazz, int methodHash) {
        JavaTypeEntry typeinfo = this.types.get(clazz);

        if (typeinfo == null)
            return null;

        Method method = typeinfo.getMethod(methodHash);
        return method;
    }

    public static JavaTypeRegistry Instance = new JavaTypeRegistry();
}
