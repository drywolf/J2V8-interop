declare function __javaGetTypeInfo(classname: string): JavaTypeInfo;

export interface JavaTypeInfo
{
    name: string;
    methods: { [name: string]: JavaMethodInfo; };
}

export interface JavaMethodInfo
{
    name: string;
    args: { [name: string]: JavaMethodArgInfo; };
}

export interface JavaMethodArgInfo
{
    name: string;
    type: JavaTypeInfo;
}

export class JavaTypeRegistry
{
    private _types: { [name: string]: JavaTypeInfo; } = {};

    public resolveType(typename: string): JavaTypeInfo
    {
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
