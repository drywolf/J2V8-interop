
const JsCG = global.J2V8Interop.JsClassGenerator2;
const JObjectTypeInfo = global.J2V8Interop.J2V8.import("java.lang.Object");

function assert(a, b)
{
    if (a !== b || typeof a !== 'string' || typeof b !== 'string')
        throw new Error(`Assert failed:\r\nExpected: ${a}\r\nGot: ${b}`);
}

function assertPropEqual(a, b, test_name)
{
    let ap = JSON.stringify(Object.getOwnPropertyDescriptors(a), null, 2);
    let bp = JSON.stringify(Object.getOwnPropertyDescriptors(b), null, 2);
    print("--------------------------------------------------")
    print("test: " + test_name);
    print("--------------------------------------------------")
    print(typeof a);
    print(typeof b);
    assert(typeof a, typeof b);
    print(ap);
    print(bp);
    assert(ap, bp);
}

function assertObjectEqual(a, b, test_name)
{
    let ao = JSON.stringify(a, null, 2);
    let bo = JSON.stringify(b, null, 2);
    print("--------------------------------------------------")
    print("test: " + test_name);
    print("--------------------------------------------------")
    print(ao);
    print(bo);
    assert(ao, bo);
}

function assertClassEqual(a, b)
{
    assert(a.name, b.name);
    assertPropEqual(a, b, "$");
    assertPropEqual(a.constructor, b.constructor, "$.constructor");
    assertPropEqual(a.prototype, b.prototype, "$.prototype");
    print(a.prototype.constructor.name + " , " + b.prototype.constructor.name);
    assertPropEqual(a.prototype.constructor, b.prototype.constructor, "$.prototype.constructor");
}

const JObject = JsCG.createClass(JObjectTypeInfo);

let a = new JObject();
let b = new JObject();

print("a.equals(b) = " + a.equals(b));
print("a.equals(a) = " + a.equals(a));
print("b.equals(b) = " + b.equals(b));
print("b.equals(a) = " + b.equals(a));

// TODO: assert JObject class / instances
// TODO: assert a/b methods/fields/etc. (use lodash & JS type checks)
