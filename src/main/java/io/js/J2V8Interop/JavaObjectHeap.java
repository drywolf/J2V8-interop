package io.js.J2V8Interop;

import java.util.*;
import com.eclipsesource.v8.*;

public class JavaObjectHeap
{
    public class JObj
    {
        public Object __val;
        public int __typehash;
    }

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
        int javaObjHash = System.identityHashCode(javaObject);
        JavaHeapEntry entry = new JavaHeapEntry(this.v8runtime, javaObject);
        heapEntries.put(javaObjHash, entry);
        return entry;
    }

    // TODO: does this cover all cases ?
    public JObj getJObjfromJSBox(V8Object jsBox)
    {
        if (jsBox.contains("__ptr"))
        {
            int javaPtr = jsBox.getInteger("__ptr");
            JavaHeapEntry javaArg = heapEntries.get(javaPtr);
            Object javaObj = javaArg.javaObject;

            JObj jobj = new JObj();
            jobj.__val = javaObj;

            // TODO: what is more performant
            // a) determining the typehash on the JS side and passing it in
            // b) just passing the value from the JS side and trying to figure out the typehash here
            if (jsBox.contains("__typehash"))
                jobj.__typehash = jsBox.getInteger("__typehash");
            else
                jobj.__typehash = System.identityHashCode(javaObj.getClass());

            return jobj;
        }

        // TODO: is this all that is needed to unbox primitive values ?
        Object __val = jsBox.get("__val");

        JObj jobj = new JObj();
        jobj.__val = __val;

        // TODO: what is more performant
        // a) determining the typehash on the JS side and passing it in
        // b) just passing the value from the JS side and trying to figure out the typehash here
        if (jsBox.contains("__typehash"))
            jobj.__typehash = jsBox.getInteger("__typehash");
        else
            jobj.__typehash = System.identityHashCode(__val.getClass());

        return jobj;
    }

    // TODO: this API needs a solidified design / refactoring
    public int getJTypehashfromJSBox(V8Object jsBox)
    {
        if (jsBox.contains("__ptr"))
        {
            int javaPtr = jsBox.getInteger("__ptr");
            JavaHeapEntry javaArg = heapEntries.get(javaPtr);
            return javaArg.javaTypehash;
        }

        // TODO: is this all that is needed to unbox primitive values ?
        return jsBox.getInteger("__typehash");
    }

    public Object getJObjfromPtr(int javaPtr)
    {
        JavaHeapEntry javaArg = heapEntries.get(javaPtr);
        return javaArg.javaObject;
    }
}
