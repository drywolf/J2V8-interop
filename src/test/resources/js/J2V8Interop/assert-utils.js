function assert(condition, message)
{
    if (typeof condition !== 'boolean')
        throw new Error("invalid type for assert condition");

    if (typeof message !== 'string')
        throw new Error("invalid type for assert message");

    if (!condition)
        throw new Error(`Assert failed: ${message}`);
}

function assertEqual(expected, got)
{
    if (typeof expected !== typeof got || expected !== got)
        throw new Error(`Assert failed: Expected: ${expected}\r\nGot: ${got}`);
}

function assertPropEqual(a, b, test_name)
{
    let ap = JSON.stringify(Object.getOwnPropertyDescriptors(a), null, 2);
    let bp = JSON.stringify(Object.getOwnPropertyDescriptors(b), null, 2);
    // print("--------------------------------------------------")
    // print("test: " + test_name);
    // print("--------------------------------------------------")
    // print(typeof a);
    // print(typeof b);
    assertEqual(typeof a, typeof b);
    // print(ap);
    // print(bp);
    assertEqual(ap, bp);
}

function assertObjectEqual(a, b, test_name)
{
    let ao = JSON.stringify(a, null, 2);
    let bo = JSON.stringify(b, null, 2);
    // print("--------------------------------------------------")
    // print("test: " + test_name);
    // print("--------------------------------------------------")
    // print(ao);
    // print(bo);
    assertEqual(ao, bo);
}

function assertClassEqual(a, b)
{
    assertEqual(a.name, b.name);
    assertPropEqual(a, b, "$");
    assertPropEqual(a.constructor, b.constructor, "$.constructor");
    assertPropEqual(a.prototype, b.prototype, "$.prototype");
    // print(a.prototype.constructor.name + " , " + b.prototype.constructor.name);
    assertPropEqual(a.prototype.constructor, b.prototype.constructor, "$.prototype.constructor");
}
