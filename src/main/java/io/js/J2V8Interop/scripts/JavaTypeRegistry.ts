declare function __javaGetTypeInfo(classname: string): JavaTypeInfo;

export interface JavaTypeInfo
{
    package: string;
    name: string;
    hash: number;
    hashstr: number;

    extends: string;

    constructors: { [name: string]: JavaMethodInfo; };
    methods: { [name: string]: JavaMethodInfo; };
}

export interface JavaMethodInfo
{
    name: string;
    hash: number;
    hashstr: number;
    args: JavaMethodArgInfo[];
}

export interface JavaMethodArgInfo
{
    name: string;
    type: JavaTypeInfo;
}

export class JavaTypeRegistry
{
    private _types: { [name: string]: JavaTypeInfo; } | null = {};

    public resolveType(typename: string): JavaTypeInfo | null
    {
        if (this._types === null)
            return null;

        let type = this._types[typename];

        if (!type)
        {
            type = __javaGetTypeInfo(typename);
            this._types[typename] = type;
        }

        return type;
    }

    public static instance = new JavaTypeRegistry();
}
