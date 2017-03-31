
///<reference path="C:/code/J2V8-interop/src/main/resources/J2V8Interop.d.ts" />

const path = require('path');

const JObject = J2V8.import("java.lang.Object");

global.JObject = JObject;

// global.obj = new JObject();
global.obj = new J2V8();

// https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#equals(java.lang.Object)
global.equals = obj.equals(obj);

global.hashCode = obj.hashCode();

global.toString = obj.toString();
