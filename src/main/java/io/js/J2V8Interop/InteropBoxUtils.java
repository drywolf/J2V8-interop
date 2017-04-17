package io.js.J2V8Interop;

import com.eclipsesource.v8.*;

public class InteropBoxUtils
{
    // TODO: implement a more generic / maintainable version of this code
    public static V8Object boxJavaObject(JavaObjectHeap heap, V8 v8, Object o) {
        final String valKey = "__val";
        final String ptrKey = "__ptr";
        final String clsKey = "__cls";

        //V8Object res = new V8Object(v8);
        if (o == null)
            return new V8Object(v8).addNull(valKey);

        Class<?> clz = o.getClass();
        if (clz == Boolean.class)
            return new V8Object(v8).add(valKey, (boolean) o);
        else if (clz == Double.class)
            return new V8Object(v8).add(valKey, (double) o);
        else if (clz == Integer.class)
            return new V8Object(v8).add(valKey, (int) o);
        else if (clz == String.class)
            return new V8Object(v8).add(valKey, (String) o);
        else if (clz == CharSequence.class)
            return new V8Object(v8).add(valKey, o.toString());
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
