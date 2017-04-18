package io.js.J2V8Interop;

import java.util.*;
import com.eclipsesource.v8.*;

public class JavaObjectHeap
{
    private HashMap<Integer, JavaHeapEntry> heapEntries = new HashMap<>();
    private V8 v8runtime;

    public JavaObjectHeap(V8 v8runtime)
    {
        this.v8runtime = v8runtime;
    }

    public void release()
    {
        for (JavaHeapEntry entry : heapEntries.values()) {
            entry.jsPtr.release();
        }
        heapEntries.clear();
    }

    public boolean contains(Object javaObject)
    {
        return heapEntries.containsKey(javaObject);
    }

    public JavaHeapEntry getEntry(Object javaObject)
    {
        return heapEntries.get(javaObject);
    }

    public JavaHeapEntry put(Object javaObject)
    {
        int javaObjHash = javaObject.hashCode();
        JavaHeapEntry entry = new JavaHeapEntry(this.v8runtime, javaObject);
        heapEntries.put(javaObjHash, entry);
        return entry;
    }

    // TODO: does this cover all cases ?
    public Object getJObjfromJSBox(V8Object jsBox)
    {
        if (jsBox.contains("__ptr"))
        {
            int javaPtr = jsBox.getInteger("__ptr");
            JavaHeapEntry javaArg = heapEntries.get(javaPtr);
            return javaArg.javaObject;
        }

        // TODO: is this all that is needed to unbox primitive values ?
        return jsBox.get("__val");
    }

    public Object getJObjfromPtr(int javaPtr)
    {
        JavaHeapEntry javaArg = heapEntries.get(javaPtr);
        return javaArg.javaObject;
    }
}
