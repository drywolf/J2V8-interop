
///<reference path="../../../../../src/main/resources/J2V8Interop.d.ts" />

const path = require('path');

const JObject = J2V8.import("java.lang.Object");

global.prune_options = {replacer:function(value, defaultValue, circular){
	if (value === undefined) return '"-undefined-"';
	return '"-' + (circular ? '$' : '') + '('+value.name+')-"';
}};

global.JSON.prune = require('json-prune');

global.JObject = JObject;

// global.obj = new JObject();
global.obj = new J2V8();

// https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#equals(java.lang.Object)
global.equals = obj.equals(obj);

global.hashCode = obj.hashCode();

global.toString = obj.toString();
