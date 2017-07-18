
(function () {
    const JsCG = J2V8.JsClassGenerator;

    let A = class A extends Object {
        x: any;

        constructor(x: any) {
            super();
            this.x = x;
        }
    };
    // printDebug("SDFSDF " + A.constructor.prototype.toString());
    // printDebug("SDFSDF " + Function.prototype.toString());

    let B = JsCG.createBlankClass("A", Object,
        function (x: any) {
            //Object.apply(this);
            //this.super();
            this.x = x[0];
            printDebug("THIS: " + JSON.stringify(this));
            //return this;
        },
        (clazz: any) => {
            clazz;
            // let A = x => {
            //     //throw new Error();
            //     // this.super();
            //     //this.x = x;
            // };

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

    assertClassEqual(A as any, B);

    class X extends A {
        constructor(x: any) {
            super(x);
        }

        testMethod1() {
            printDebug("testMethod1");
        }
    };

    let Y = JsCG.createBlankClass("X", B,
        function (args: Array<any>) {
            /// super(x)
            let thiz = Reflect.construct(B, [args[0]], Y);

            printDebug("THIS-in: " + JSON.stringify(thiz));

            //Reflect.apply(B, this, [1]);
            //B.call(Y, [x[0]]);
            // B.prototype.constructor.call(this, 1)
            //Object.setPrototypeOf(this, new.target.prototype);
            printDebug("THIS-out: " + JSON.stringify(thiz));

            //return this;
            return thiz;

            // this.x = x;
            // super();

            //B.apply(this, x[0]);
            //this.super(x[0]);
        },
        (clazz: any) => {
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
            //     printDebug("testMethod1..." + this.x);
            // };

            // let testMethod1 = function()
            // {
            //     printDebug("testMethod1..." + this.x);
            // };

            // TODO: this might be the way to go
            // function testMethod1()
            // {
            //     "use strict";
            //     printDebug("testMethod1..." + this.x);
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
            //     printDebug("testMethod1..." + this.x);
            // });

            let testMethod1 = (
                class {
                    testMethod1() {
                        printDebug("testMethod1..." + (this as any).x);
                    }
                }
            ).prototype.testMethod1;

            // let testMethod1 = () =>
            // {
            //     "use strict";
            //     printDebug("testMethod1..." + this.x);
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
        }) as typeof X;

    assertClassEqual(X, Y);

    let a = new A(1);
    let b = new B(1);

    let x = new X(1);
    let y = new Y(1);

    printDebug("----- instance tests -----");

    assertPropEqual(x.testMethod1, y.testMethod1, "@.testMethod1");

    assertObjectEqual(a, x, "a = x");
    assertObjectEqual(a, b, "a = b");
    assertObjectEqual(b, x, "b = x");
    assertObjectEqual(a, y, "a = y");

    printDebug("B.len = " + B.length);
    printDebug("Y.len = " + Y.length);

    printDebug("B.constr.len = " + B.constructor.length);
    printDebug("Y.constr.len = " + Y.constructor.length);

    x.testMethod1();
    y.testMethod1();

    class MyClass extends Y {
        constructor() {
            super(3);
        }
    }

    printDebug("------myclass---------")
    let myc = new MyClass();
    myc.testMethod1();

    // printDebug(A.toString());
    // printDebug(B.toString());
    // printDebug(X.toString());
    // printDebug(Y.toString());

    // printDebug("a = " + JSON.stringify(a));
    // printDebug("b = " + JSON.stringify(b));

    // assertPropEqual(a, b, "$obj a b");

    // printDebug("x = " + JSON.stringify(x.testMethod1));
    // printDebug("y = " + JSON.stringify(y.testMethod1));

    // assertPropEqual(x, y, "$obj x y");

    // let A_props = Object.getOwnPropertyDescriptors(A);
    // let B_props = Object.getOwnPropertyDescriptors(B);

    // if (A_props.length !== B_props.length)
    //     throw new Error("A_props.length !== B_props.length");
})();
