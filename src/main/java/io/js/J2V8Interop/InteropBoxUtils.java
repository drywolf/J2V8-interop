package io.js.J2V8Interop;

import com.eclipsesource.v8.*;

public class InteropBoxUtils
{
    static final String valKey = "__val";
    static final String ptrKey = "__ptr";
    static final String clsKey = "__cls";
    static final String typehashKey = "__typehash";

    // TODO: implement a more generic / maintainable version of this code
    public static V8Object boxJavaObject(JavaObjectHeap heap, V8 v8, Object o) {

        //V8Object res = new V8Object(v8);
        if (o == null)
            return new V8Object(v8).addNull(valKey);

        Class<?> clz = o.getClass();
        if (clz == Boolean.class)
            return new V8Object(v8)
                .add(valKey, (boolean) o)
                .add(typehashKey, System.identityHashCode(Boolean.class));
        else if (clz == Double.class)
            return new V8Object(v8)
                .add(valKey, (double) o)
                .add(typehashKey, System.identityHashCode(Double.class));
        else if (clz == Integer.class)
            return new V8Object(v8)
                .add(valKey, (int) o)
                .add(typehashKey, System.identityHashCode(Integer.class));
        else if (clz == String.class)
            return new V8Object(v8)
                .add(valKey, (String) o)
                .add(typehashKey, System.identityHashCode(String.class));
        else if (clz == CharSequence.class)
            return new V8Object(v8)
                .add(valKey, o.toString());
        else if (clz.isArray()) {
            // TODO: implement boxing arrays
            throw new RuntimeException("Boxing arrays not implemented");
            // Object[] oarr = toObjectArray(o);
            // V8Array arr = new V8Array(v8);
            // for (int i = 0; i < oarr.length; i++) {
            //     arr.push(toV8Object(v8, oarr[i]));
            // }
            // res.add(valKey, arr);
            // arr.release();
        } else if (o instanceof V8Value)
            return new V8Object(v8).add(valKey, (V8Value) o);
        else if (o instanceof Object) {
            JavaHeapEntry e = heap.getEntry(o);

            if (e == null)
                e = heap.put(o);

            return e.jsPtr;
        } else {
            throw new RuntimeException("Unknown boxing for type: " + clz);
        }
    }
}
