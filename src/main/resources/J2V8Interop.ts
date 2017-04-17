
import {JavaTypeRegistry} from './JavaTypeRegistry';
import {JsClassGenerator2} from './JsClassGenerator2';

export {JsClassGenerator} from './JsClassGenerator';
export {JsClassGenerator2} from './JsClassGenerator2';

export class J2V8
{
    // TODO: define separate constructible type and reuse it where needed
    public static import(classname: string): {new(...args: any[]): any} | null
    {
        // console.log(classname);
        // return null;
        // TODO: generate Java class proxies
        // __javaGetClassInfo(classname);
        // return J2V8;

        let typeInfo = JavaTypeRegistry.instance.resolveType(classname);

        if (typeInfo === null)
            return null;

        let jsClass = JsClassGenerator2.createClass(typeInfo)
        return jsClass;
    }

    // TODO: just for testing temporarily
    public equals(other: Object): boolean
    {
        return this == other;
    }

    // TODO: just for testing temporarily
    public hashCode(): number
    {
        return 123456|0;
    }

    // TODO: just for testing temporarily
    public toString(): string
    {
        return "hello world!";
    }
}

// TODO: get .d.ts type annotations correctly exported for this (configure webpack .d.ts generator)
(global as any).J2V8 = J2V8;
