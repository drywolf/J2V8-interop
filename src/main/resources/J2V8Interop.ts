
import {JavaTypeRegistry, JavaTypeInfo} from './JavaTypeRegistry';

export class J2V8
{
    public static import(classname: string): JavaTypeInfo
    {
        console.log(classname);
        // return null;
        // TODO: generate Java class proxies
        // __javaGetClassInfo(classname);
        // return J2V8;

        let type = JavaTypeRegistry.instance.resolveType(classname);
        return type;
    }

    public static release()
    {
        console.log("release");
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
