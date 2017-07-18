import * as _ from 'lodash';

import {JavaTypeInfo} from './JavaTypeRegistry';
import {JsProxyHeap, JsHeapEntry} from './JsProxyHeap';

declare function print(message: string): void;
declare function __javaCreateInstance(target_class: JavaClassProxy, instance: any): JsHeapEntry;
declare function __javaCallMethod(__thisPtr: number, methodName: string, methodHash: number, args: any[]): JsHeapEntry;

export declare type JsConstrutible =
{
    new(...args: any[]): any;
    __javaPackage: string;
    __javaClassName: string;
    __javaClassHash: number;
} | null;

interface JavaClassProxy
{
    __javaPackage: string;
    __javaClassName: string;
    __javaClassHash: number;
}

class JavaInstanceProvider
{
    public static initJavaInstance(target_class: JavaClassProxy, instance: any)
    {
        // call Java factory which will also set __ptr on instance
       let heapEntry = __javaCreateInstance(target_class, instance);
       JsProxyHeap.instance.putEntry(heapEntry);
    }
}

declare var $__CtorSuper__$: any;
// declare var $__MethodHash__$: number;
// declare var $__javaInstancePtr__$: number;

class $__JsProxySuperClassName__$ { constructor(...args: any[]) { args; } }

class $__JsProxyClassName__$ extends $__JsProxySuperClassName__$
{
    __ptr?: number;

    $__JavaClassMetaData__$(){}

    $__JavaInstanceMetaData__$(){}

    // TODO: constructor signature gen (multiple overloads)
    constructor($__CtorArgs__$: any)
    {
        // TODO: type checks
        super($__CtorSuper__$);

        //print("------------> new.target " + (new.target as any).__javaPackage);
        //print("------------> new.target " + (new.target as any).__javaClassName);
        //print("------------> new.target " + (new.target as any).__javaClassHash);

        let arg0 = (arguments.length > 0 && arguments[0]) || undefined;

        if ((arg0 && arg0.__ptr) // a boxed Java ptr is passed, just create a class instance to wrap this given ptr
            || new.target !== $__JsProxyClassName__$) // only the top-most class in the inheritance-chain should create a new Java instance
        {
        }
        else
        {
            JavaInstanceProvider.initJavaInstance(new.target as any, this);
        }

        $__CtorArgs__$;
    }

    $__CtorProxies__$(){}

    $__MethodProxies__$(){}
}

function $__JsCtor__$($__CtorArgs__$: any)
{
    $__CtorArgs__$;
}

// TODO: temporary TS lazy workarround for hash constants access
declare var J2V8: any;

interface JsArg extends JsHeapEntry
{
    __cast_as?: JsHeapEntry; // TODO: should be JavaTypeInfo
    __cast_value?: JsHeapEntry;
}

function $__JsMethod__$($__MethodArgs__$: any)
{
    $__MethodArgs__$;

    // print("function $__JsMethod__$ --> this.__ptr = " + this.__ptr);

    // TODO: define correct behavior for this situation
    if (!this.__ptr)
    {
        print(`Warning: ignoring JS method-call "function $__JsMethod__$" because the call-target is not defined`);
        throw new Error("this == null");
        // return;
    }

    // TODO: proper marshalling of JS values to Java values
    let boxedArgs = _.map(arguments, (arg: JsArg & number) =>
    {
        if (arg.__cast_as && arg.__cast_value !== undefined && arg.__cast_value !== null)
        {
            if (arg.__cast_value.__ptr)
                return {
                    __ptr: arg.__cast_value.__ptr,
                    __typehash: (arg.__cast_as as any).__javaClassHash, // TODO: cleanup hash|__typehash|__javaClassHash
                };
            else
                return {
                    __typehash: (arg.__cast_as as any).__javaClassHash, // TODO: cleanup hash|__typehash|__javaClassHash
                    __val: arg.__cast_value,
                };
        }

        if (arg.__ptr)
            // return { __ptr: arg.__ptr };
            return arg; // TODO: this might be unsafe, because we trust the rest of the code to not mutate this object

        let typehash: number;

        // TODO: experimental prototyping code, needs full implementation
        // TODO: this might be redundant since if no typehash is passed,
        // the java side will fill it in anyways (this is a question of performance optimization)
        if (Number.isInteger(arg))
            typehash = J2V8.INTEGER_HASH;
        else if(typeof arg === 'string')
            typehash = J2V8.STRING_HASH;
        else if(typeof arg === 'boolean')
            typehash = J2V8.BOOLEAN_HASH;
        else
            typehash = -1;

        return { __val: arg, __typehash: typehash };
    });

    let argsListHash = boxedArgs.reduce((acc, val) =>
    {
        return acc ^ val.__typehash;
    }, 0);

    // let javaMethodHash = this.prototype['function $__JsMethod__$'];

    // print("jsBoxedArgs: " + JSON.stringify(marshalledArgs));

    let returnBox = __javaCallMethod(this.__ptr, "function $__JsMethod__$", argsListHash, boxedArgs);

    if (returnBox.__ptr)
    {
        // TODO: unbox the ptr to the JS class instance off the heap
        //throw new Error("TODO: unbox the ptr to the JS class instance off the heap");
        // let clazz = J2V8.import(returnBox.__cls);

        let entry = JsProxyHeap.instance.getOrCreateInstance(returnBox);
        return entry.__val;
    }

    return returnBox.__val;
}

const JsProxyClassTemplateString: string = $__JsProxyClassName__$.toString();
const JsCtorTemplateString: string = $__JsCtor__$.toString();
const JsMethodTemplateString: string = $__JsMethod__$.toString();

export class JsClassGenerator2
{
    public static createClass(javaType: JavaTypeInfo): JsConstrutible
    {
        //print("template " + JsProxyClassTemplateString);
        let classCode: string = /*"(function() {" + */JsProxyClassTemplateString;

        classCode = classCode.replace(/\$__JsProxyClassName__\$/g, javaType.name);

        let javaClassMetaData =
`
    static get __javaPackage() { return '${javaType.package}'; }
    static get __javaClassName() { return '${javaType.name}'; }
    static get __javaClassHash() { return ${javaType.hash}; }
`;
        classCode = classCode.replace(/\$__JavaClassMetaData__\$\(\)\s*{\s*}/g, javaClassMetaData);

        // TODO: handle super type + use cache for generated Js->Java class proxies
        if (javaType.extends)
            classCode = classCode.replace(/extends \$__JsProxySuperClassName__\$/g, `extends J2V8.import("${javaType.extends}")`);
        else
            classCode = classCode.replace(/extends \$__JsProxySuperClassName__\$/g, "");

        if (javaType.extends)
            classCode = classCode.replace(/super\(\$__CtorSuper__\$\);/g, "super();");
        else
            classCode = classCode.replace(/super\(\$__CtorSuper__\$\);/g, "");

        classCode = classCode.replace(/\$__CtorArgs__\$/g, "");

        let ctorCodes = "";

        for (let ctorName in javaType.constructors)
        {
            let ctor = javaType.constructors[ctorName];

            let ctorCode = JsCtorTemplateString;
            ctorCode = ctorCode.replace(/function \$__JsCtor__\$/g, `static ctor_${ctor.hash}`);
            ctorCode = ctorCode.replace(/\$__CtorArgs__\$/g, _.map(ctor.args, x => x.name).join(", "));

            ctorCodes += ctorCode + "\r\n";
        }

        classCode = classCode.replace(/\$__CtorProxies__\$\(\)\s*{\s*}/g, ctorCodes);

        // TODO: generate real+overload conststructor that will call into proxies depending on given arguments

        let methodCodes = "";

        // TODO: replace by utility method and use for ctors and methods
        for (let methodName in javaType.methods)
        {
            let method = javaType.methods[methodName];

            // TODO: why is this check suddenly necessary ??!!
            if (!method.name)
            {
                printDebug("skipping method...");
                continue;
            }

            let methodCode = JsMethodTemplateString;
            methodCode = methodCode.replace(/function \$__JsMethod__\$/g, method.name);
            methodCode = methodCode.replace(/\$__MethodArgs__\$/g, _.map(method.args, x => x.name).join(", "));
            //methodCode = methodCode.replace(/\$__javaInstancePtr__\$/g, javaType.hash.toString());
            methodCode = methodCode.replace(/\$__MethodHash__\$/g, method.hash.toString());

            methodCodes += methodCode + "\r\n";
        }

        classCode = classCode.replace(/\$__MethodProxies__\$\(\)\s*{\s*}/g, methodCodes);

        // TODO: generate real+overload methods that will call into proxies depending on given arguments

//         classCode = classCode +
// `
//     ${javaType.name}.__javaPackage = '${javaType.package}';
//     ${javaType.name}.__javaClassName = '${javaType.name}';
//     ${javaType.name}.__javaClassHash = ${javaType.hash};
//     return ${javaType.name} })();
// `;

        //print("METHODS: " + methodCodes);

        // print("generated class: " + javaType.name);
        // print("#methods: " + Object.keys(javaType.methods).length);
        // print("#constructors: " + Object.keys(javaType.constructors).length);

        // printDebug(classCode);
        // TODO: for debugging only
        //classCode = `class ${javaType.name} {}`;
        return eval(classCode);
    }
}

declare function printDebug(x: string): void;
