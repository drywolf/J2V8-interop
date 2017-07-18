
declare function printDebug(x: string): void;

function assert(condition: boolean, message: string)
{
    if (typeof condition !== 'boolean')
        throw new Error("invalid type for assert condition");

    if (typeof message !== 'string')
        throw new Error("invalid type for assert message");

    if (!condition)
        throw new Error(`Assert failed: ${message}`);
}

function assertEqual(expected: any, got: any)
{
    if (typeof expected !== typeof got || expected !== got)
        throw new Error(`Assert failed: Expected: ${expected}\r\nGot: ${got}`);
}

// patch typescript definition of "Object"
interface Object {
    getOwnPropertyDescriptors(obj: Object): Object;
}

function assertPropEqual(a: Object | Function, b: Object | Function, test_name: string)
{
    let ap = JSON.stringify(Object.getOwnPropertyDescriptors(a as Object), null, 2);
    let bp = JSON.stringify(Object.getOwnPropertyDescriptors(b as Object), null, 2);
    test_name;
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

function assertObjectEqual(a: Object, b: Object, test_name: string)
{
    let ao = JSON.stringify(a, null, 2);
    let bo = JSON.stringify(b, null, 2);
    test_name;
    // print("--------------------------------------------------")
    // print("test: " + test_name);
    // print("--------------------------------------------------")
    // print(ao);
    // print(bo);
    assertEqual(ao, bo);
}

function assertClassEqual(a: any, b: any)
{
    assertEqual(a.name, b.name);
    assertPropEqual(a, b, "$");
    assertPropEqual(a.constructor, b.constructor, "$.constructor");
    assertPropEqual(a.prototype, b.prototype, "$.prototype");
    printDebug(a.prototype.constructor.name + " , " + b.prototype.constructor.name);
    assertPropEqual(a.prototype.constructor, b.prototype.constructor, "$.prototype.constructor");
}
