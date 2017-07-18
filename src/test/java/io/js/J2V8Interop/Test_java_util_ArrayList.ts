// TODO: set up proper .d.ts generation for TS code
// then add extra annotations for the Java types used here

interface Number
{
    as(type: any): {__cast_as: any; __cast_value: any;};
}

(Object.prototype as any).as =
(Number.prototype as any).as = function(type: any)
{
    if (!type.__javaClassHash)
        throw new Error("Missing typehash information for type-cast " + type.name);

    let cast_box = { __cast_as: type, __cast_value: this.valueOf() };
    printDebug(this + " as " + cast_box);
    return cast_box;
};

(function () {
    const JArray = J2V8.import("java.util.ArrayList");
    const JObject = J2V8.import("java.lang.Object");

    let a = new JArray();
    let b = new JArray();

    assertEqual(true, a.equals(a));
    assertEqual(true, b.equals(b));

    assertEqual(true, a.equals(b));
    assertEqual(true, b.equals(a));

    assertEqual(false, a == b);
    assertEqual(false, b == a);

    assertEqual(false, a === b);
    assertEqual(false, b === a);

    let aHash = a.hashCode();
    let bHash = b.hashCode();

    printDebug("aHash = " + aHash);
    printDebug("bHash = " + bHash);

    assertEqual('number', typeof aHash);
    assertEqual('number', typeof bHash);

    assertEqual(aHash, bHash);

    // Test index overload: public E remove(int index)
    // https://docs.oracle.com/javase/7/docs/api/java/util/ArrayList.html#remove(int)
    a.add(1);
    assertEqual(1, a.size());
    let rem1 = a.remove(0);
    assertEqual(0, a.size());
    assertEqual(1, rem1);

    // Test Object overload: public boolean remove(Object o)
    // https://docs.oracle.com/javase/7/docs/api/java/util/ArrayList.html#remove(java.lang.Object)
    a.add(1);
    assertEqual(1, a.size());
    let rem2 = a.remove(1..as(JObject));
    assertEqual(0, a.size());
    assertEqual(true, rem2);

    // Test clear()
    a.add("A");
    a.add("B");
    a.add("C");
    assertEqual(3, a.size());
    a.clear();
    assertEqual(0, a.size());
})();
