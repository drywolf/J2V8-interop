package io.js.J2V8Interop;

import com.eclipsesource.v8.*;

public class InteropRuntime {

    private V8 v8runtime;
    private JavaObjectHeap javaHeap;

    public InteropRuntime(V8 v8runtime)
    {
        this.v8runtime = v8runtime;
        this.javaHeap = new JavaObjectHeap(v8runtime);
    }

    public V8 getV8Runtime()
    {
        return this.v8runtime;
    }

    public JavaObjectHeap getHeap()
    {
        return this.javaHeap;
    }
}
