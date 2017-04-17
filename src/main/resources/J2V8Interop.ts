
import {JavaTypeRegistry, JavaTypeInfo} from './JavaTypeRegistry';
export {JsClassGenerator} from './JsClassGenerator';
export {JsClassGenerator2} from './JsClassGenerator2';

export class J2V8
{
    public static import(classname: string): JavaTypeInfo | null
    {
        // console.log(classname);
        // return null;
        // TODO: generate Java class proxies
        // __javaGetClassInfo(classname);
        // return J2V8;

        let type = JavaTypeRegistry.instance.resolveType(classname);
        return type;
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
