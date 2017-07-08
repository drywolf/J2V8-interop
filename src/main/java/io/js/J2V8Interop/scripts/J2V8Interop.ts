
import {JavaTypeRegistry} from './JavaTypeRegistry';
import {JsClassGenerator} from './JsClassGenerator';
import {JsClassGenerator2} from './JsClassGenerator2';

export {JsClassGenerator} from './JsClassGenerator';
export {JsClassGenerator2} from './JsClassGenerator2';

export declare type JsConstrutible = {new(...args: any[]): any} | null;

export class J2V8Impl
{
    static classes: {[classname: string]: JsConstrutible} = {};

    public static JsClassGenerator = JsClassGenerator;

    public static import(classname: string): JsConstrutible
    {
        let jsClass = J2V8Impl.classes[classname];

        if (jsClass)
            return jsClass;

        let typeInfo = JavaTypeRegistry.instance.resolveType(classname);

        if (typeInfo === null)
            return null;

        jsClass = JsClassGenerator2.createClass(typeInfo);
        J2V8Impl.classes[classname] = jsClass;
        return jsClass;
    }
}

// TODO: get .d.ts type annotations correctly exported for this (configure webpack .d.ts generator)
(global as any).J2V8 = J2V8Impl;
export declare var J2V8: typeof J2V8Impl;
