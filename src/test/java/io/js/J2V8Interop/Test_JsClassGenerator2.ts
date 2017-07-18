
(function () {
    const JObject = J2V8.import("java.lang.Object");

    let a = new JObject();
    let b = new JObject();

    printDebug("a.equals(b) = " + a.equals(b));
    printDebug("a.equals(a) = " + a.equals(a));
    printDebug("b.equals(b) = " + b.equals(b));
    printDebug("b.equals(a) = " + b.equals(a));

    // TODO: assert JObject class / instances
    // TODO: assert a/b methods/fields/etc. (use lodash & JS type checks)
})();
