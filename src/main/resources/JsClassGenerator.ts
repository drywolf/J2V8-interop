
//declare function __extends(d: any, b: any): any;

// var _extends = (function () {
//     var extendStatics = Object.setPrototypeOf ||
//         ({ __proto__: [] } instanceof Array && function (d: any, b: any) { d.__proto__ = b; }) ||
//         function (d: any, b: any) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
//     return function (d: any, b: any) {
//         extendStatics(d, b);
//         function __() { var t = this; t.constructor = d; }
//         d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new (__() as any));
//     };
// })();

// function applyConstruct(ctor, params) {
//     var obj, newobj;
    
//     function fakeCtor() {
//     }
//     fakeCtor.prototype = ctor.prototype;
//     obj = new fakeCtor();
//     obj.constructor = ctor;
//     newobj = ctor.apply(obj, params);
//     if (newobj !== null && (typeof newobj === "object" || typeof newobj === "function")) {
//         obj = newobj;
//     }
//     return obj;
// }

export class JsClassGenerator extends Object
{
    public static createBlankClass(name: string, xtends: FunctionConstructor, ctor?: Function, configure?: Function): Function
    {
        let clazz = class /*extends xtends*/
        {
            //constructor(x: any){super(); this.x = x;}

            constructor()
            {
                // super();
                // this.x = x;
                //super();
                // print("native-this: " + JSON.stringify(this));
                // print("native-args: " + JSON.stringify(arguments));

                if (ctor)
                    return ctor.bind(this, arguments)();
                    // ctor.apply(this, arguments);
                // applyConstruct(ctor, arguments);
            }
            
            // constructor()
            // {
            //     // TODO: generate type checking code for constructor arguments
            //     if (typeof xtends !== 'undefined')
            //     {
            //         var a = arguments;
            //         var l = a.length;
            //         if (l > 3) return xtends.apply(this, a);
            //         var obj: any = !l ? new xtends : l < 2 ? new xtends(a[0]) : l < 3 ? new xtends(a[0], a[1]) : new xtends(a[0], a[1], a[2]);
            //         var p = Object.getPrototypeOf(this);
            //         Object.setPrototypeOf ? Object.setPrototypeOf(obj, p) : obj.__proto__ = p;
            //         return obj;

            //         // xtends.call(this, arguments);
            //         // xtends.apply(this, arguments);
            //     }
            // }
        };

        //__extends(clazz, xtends);

        let props: any =
        {
            // length:
            // {
            //     configurable: true,
            //     enumerable: false,
            //     value: 0,
            //     writable: false,
            // },

            // prototype:
            // {
            //     configurable: false,
            //     enumerable: false,
            //     value: xtends,
            //     writable: false,
            // },
        };
        
        // delete clazz.length;
        props.length =
        {
            configurable: true,
            enumerable: false,
            value: 1,
            writable: false,
        };

        props.name =
        {
            configurable: true,
            enumerable: false,
            value: name,
            writable: false,
        };

        if (typeof xtends !== 'undefined')
        {
            // clazz.prototype = Object.create(xtends.prototype);
            props.prototype = Object.create(xtends.prototype);
        }

        // props.constructor = (args: any) =>
        // {
        //     args;
        // };

        Object.defineProperties(clazz, props);

        clazz.prototype.constructor = clazz;

        // clazz.constructor = (a: any, b: any) => {a; b; return this;};
        // print(clazz.constructor.length.toString());

        if (typeof configure !== 'undefined')
        {
            configure(clazz);
        }

        return clazz;
    }
}
