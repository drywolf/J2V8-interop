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

    public JavaHeapEntry put(Object javaObject)
    {
        int javaObjHash = javaObject.hashCode();
        JavaHeapEntry entry = new JavaHeapEntry(this.v8runtime, javaObject);
        heapEntries.put(javaObjHash, entry);
        return entry;
    }

    public Object getJObjfromJSBox(V8Object ptrBox)
    {
        int javaPtr = ptrBox.getInteger("__javaPtr");
        JavaHeapEntry javaArg = heapEntries.get(javaPtr);
        return javaArg.javaObject;
    }

    public Object getJObjfromPtr(int javaPtr)
    {
        JavaHeapEntry javaArg = heapEntries.get(javaPtr);
        return javaArg.javaObject;
    }
}
