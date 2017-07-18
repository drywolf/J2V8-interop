package io.js.J2V8Interop;

import com.eclipsesource.v8.*;

public class JavaHeapEntry {
    public Object javaObject;
    public int javaPtr;
    public int javaTypehash;

    public V8Object jsPtr; // TODO: actually jsHeapEntry ?!

    public JavaHeapEntry(V8 runtime, Object javaObject) {
        Class<?> javaClass = javaObject.getClass();

        this.javaObject = javaObject;
        this.javaPtr = System.identityHashCode(javaObject);
        this.javaTypehash = System.identityHashCode(javaClass);

        this.jsPtr = new V8Object(runtime);
        this.jsPtr.add("__ptr", this.javaPtr);
        this.jsPtr.add("__cls", javaClass.getName());
        this.jsPtr.add("__typehash", this.javaTypehash);
    }
}
