package io.js.J2V8Interop;

import java.io.Closeable;
import java.util.HashSet;

import com.eclipsesource.v8.*;

public class V8Stackframe implements Closeable
{
    private HashSet<V8Value> items = new HashSet<>();

    public V8Object getObject(V8Object obj, String key)
    {
        V8Object stackobj = obj.getObject(key);
        items.add(stackobj);
        return stackobj;
    }

    public V8Object getObject(V8Array arr, int index)
    {
        V8Object stackobj = arr.getObject(index);
        items.add(stackobj);
        return stackobj;
    }

    public V8Array getArray(V8Array array, int index)
    {
        V8Array stackarr = array.getArray(index);
        items.add(stackarr);
        return stackarr;
    }

    public void close()
    {
        for (V8Value v : items)
            v.release();

        items.clear();
    }
}
