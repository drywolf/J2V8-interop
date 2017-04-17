
const JObject = global.J2V8Interop.J2V8.import("java.lang.Object");

let a = new JObject();
let b = new JObject();

assertEqual(true, a.equals(a));
assertEqual(true, b.equals(b));

assertEqual(false, a.equals(b));
assertEqual(false, b.equals(a));

let aHash = a.hashCode();
let bHash = b.hashCode();

print("aHash = " + aHash);
print("bHash = " + bHash);

assert(typeof aHash === 'number', "typeof aHash === 'number'");
assert(typeof bHash === 'number', "typeof bHash === 'number'");

assert(aHash !== bHash, "aHash !== bHash");

// TODO: maybe add support for optional JS mixins into Java Proxy types
// e.g. java.lang.Object -> getJsClass() ... returns the generated ES6 class
let aClass = a.getClass();
let bClass = b.getClass();

assertEqual("java.lang.Object", aClass.getName());
assertEqual("java.lang.Object", bClass.getName());

assert(aClass === bClass, "aClass === bClass");

// TODO: think about a way to use Java unit test code to test this API instead
// a) run Java unit-tests in java and store assert-output, then compare it with JS assert-output
// b) port / transpile java unit-test code directly to the API
assertEqual(aClass.getEnclosingMethod(), null);
assertEqual(bClass.getEnclosingMethod(), null);

assertEqual(aClass.getSuperclass(), null);
assertEqual(bClass.getSuperclass(), null);

assert(aClass.isAssignableFrom(bClass), true);
assert(bClass.isAssignableFrom(aClass), true);

// TODO: assert JObject class / instances
// TODO: assert a/b methods/fields/etc. (use lodash & JS type checks)
