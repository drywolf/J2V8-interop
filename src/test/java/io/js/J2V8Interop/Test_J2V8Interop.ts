
(function () {
    // const path = require('path');

    const JObject = J2V8.import("java.lang.Object");

    const g = global as any;

    // g.prune_options = {
    //     replacer: function (value: any, defaultValue: any, circular: any) {
    //         defaultValue;
    //         if (value === undefined) return '"-undefined-"';
    //         return '"-' + (circular ? '$' : '') + '(' + value.name + ')-"';
    //     }
    // };

    // g.JSON.prune = require('json-prune');

    g.JObject = JObject;

    let obj = new JObject();
    // g.obj = obj;

    // https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#equals(java.lang.Object)
    g.equals = obj.equals(obj);

    g.hashCode = obj.hashCode();

    g.toString = obj.toString();
})();
