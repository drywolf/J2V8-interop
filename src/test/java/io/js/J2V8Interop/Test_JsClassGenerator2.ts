
(function () {
    const JObject = J2V8.import("java.lang.Object");

    let a = new JObject();
    let b = new JObject();

    print("a.equals(b) = " + a.equals(b));
    print("a.equals(a) = " + a.equals(a));
    print("b.equals(b) = " + b.equals(b));
    print("b.equals(a) = " + b.equals(a));

    // TODO: assert JObject class / instances
    // TODO: assert a/b methods/fields/etc. (use lodash & JS type checks)
})();
