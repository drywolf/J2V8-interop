package io.js.J2V8Interop;

import com.eclipsesource.v8.*;

public class JavaHeapEntry {
    public Object javaObject;
    public V8Object jsPtr;
    public int javaPtr;
    public JavaHeapEntry(V8 runtime, Object javaObject) {
        this.javaObject = javaObject;
        this.javaPtr = javaObject.hashCode();
        this.jsPtr = new V8Object(runtime);
        this.jsPtr.add("__javaPtr", this.javaPtr);
    }
}
