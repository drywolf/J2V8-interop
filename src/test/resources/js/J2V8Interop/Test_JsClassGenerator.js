
const JsCG = global.J2V8Interop.JsClassGenerator;

let A = class A extends Object
{
    constructor(x)
    {
        super();
        this.x = x;
    }
};
    // print("SDFSDF " + A.constructor.prototype.toString());
    // print("SDFSDF " + Function.prototype.toString());

let B = JsCG.createBlankClass("A", Object,
function(x)
{
    //Object.apply(this);
    //this.super();
    this.x = x[0];
    print("THIS: " + JSON.stringify(this));
    //return this;
},
clazz =>
{
    let A = x =>
    {
        //throw new Error();
        // this.super();
        //this.x = x;
    };

    // Object.defineProperties(A,
    // {
    //     prototype:
    //     {
    //         configurable: false,
    //         enumerable: false,
    //         value: Object.prototype,
    //         writable: false,
    //     }
    // });
    // A.prototype.constructor = A;

    // Object.defineProperties(clazz.prototype,
    // {
    //     constructor:
    //     {
    //         configurable: true,
    //         enumerable: false,
    //         value: A,
    //         writable: true,
    //     }
    // });
    // clazz.prototype.constructor = A;
});

assertClassEqual(A, B);

let X = class X extends A
{
    constructor(x)
    {
        super(x);
    }

    testMethod1()
    {
        print("testMethod1");
    }
};
let Y = JsCG.createBlankClass("X", B,
function([x])
{
    /// super(x)
    let thiz = Reflect.construct(B, [x], Y);
    
    print("THIS-in: " + JSON.stringify(thiz));

    //Reflect.apply(B, this, [1]);
    //B.call(Y, [x[0]]);
    // B.prototype.constructor.call(this, 1)
    //Object.setPrototypeOf(this, new.target.prototype);
    print("THIS-out: " + JSON.stringify(thiz));

    //return this;
    return thiz;

    // this.x = x;
    // super();

    //B.apply(this, x[0]);
    //this.super(x[0]);
},
clazz =>
{
    // let X = x =>
    // {
    //     throw new Error();
    //     //this.super(x);
    // };

    // Object.defineProperties(X,
    // {
    //     prototype:
    //     {
    //         configurable: false,
    //         enumerable: false,
    //         value: Object.prototype,
    //         writable: false,
    //     }
    // });
    // X.prototype.constructor = X;

    // let testMethod1 = () =>
    // {
    //     print("testMethod1..." + this.x);
    // };

    // let testMethod1 = function()
    // {
    //     print("testMethod1..." + this.x);
    // };

    // TODO: this might be the way to go
    // function testMethod1()
    // {
    //     "use strict";
    //     print("testMethod1..." + this.x);
    // };

    // var createMethod = function(name, body)
    // {
    //     let m = (
    //         class
    //         {
    //             [name]()
    //             {
    //                 body();
    //             }
    //         }
    //     ).prototype[name];

    //     m.name = name;
    //     return m;
    // };

    // let testMethod1 = createMethod("testMethod1", function()
    // {
    //     print("testMethod1..." + this.x);
    // });

    let testMethod1 = (
        class
        {
            testMethod1()
            {
                print("testMethod1..." + this.x);
            }
        }
    ).prototype.testMethod1;

    // let testMethod1 = () =>
    // {
    //     "use strict";
    //     print("testMethod1..." + this.x);
    // };

    Object.defineProperties(clazz.prototype,
    {
        // constructor:
        // {
        //     configurable: true,
        //     enumerable: false,
        //     value: X,
        //     writable: true,
        // },
        testMethod1:
        {
            configurable: true,
            enumerable: false,
            value: testMethod1,
            writable: true,
        }
    });
});

assertClassEqual(X, Y);

let a = new A(1);
let b = new B(1);

let x = new X(1);
let y = new Y(1);

print("----- instance tests -----");

assertPropEqual(x.testMethod1, y.testMethod1, "@.testMethod1");

assertObjectEqual(a, x, "a = x");
assertObjectEqual(a, b, "a = b");
assertObjectEqual(b, x, "b = x");
assertObjectEqual(a, y, "a = y");

print("B.len = " + B.length);
print("Y.len = " + Y.length);

print("B.constr.len = " + B.constructor.length);
print("Y.constr.len = " + Y.constructor.length);

x.testMethod1();
y.testMethod1();

class MyClass extends Y
{
    constructor()
    {
        super(3);
    }
}

print("------myclass---------")
let myc = new MyClass();
myc.testMethod1();

// print(A.toString());
// print(B.toString());
// print(X.toString());
// print(Y.toString());

// print("a = " + JSON.stringify(a));
// print("b = " + JSON.stringify(b));

// assertPropEqual(a, b, "$obj a b");

// print("x = " + JSON.stringify(x.testMethod1));
// print("y = " + JSON.stringify(y.testMethod1));

// assertPropEqual(x, y, "$obj x y");

// let A_props = Object.getOwnPropertyDescriptors(A);
// let B_props = Object.getOwnPropertyDescriptors(B);

// if (A_props.length !== B_props.length)
//     throw new Error("A_props.length !== B_props.length");
