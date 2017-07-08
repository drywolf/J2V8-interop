:: generate type bundle in typings folder (.d.ts)
node ./node_modules/dts-bundle-generator/bin/cli.js -o ./typings/J2V8Interop.d.ts ./src/main/java/io/js/J2V8Interop/scripts/J2V8Interop.ts

:: remove "export" directives (they make the typescript service & vscode unhappy)
node ./node_modules/replace/bin/replace.js "export " "" ./typings/J2V8Interop.d.ts -q
