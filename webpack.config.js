var path = require('path');

module.exports =
{
    context: path.join(__dirname, './src/main/resources'),
    entry: {
        J2V8Interop: './J2V8Interop.ts'
    },
    output: {
        path: path.join(__dirname, './src/main/resources'),
        filename: 'J2V8Interop.js'
    },
    resolve: {
        extensions: ['.webpack.js', '.web.js', '.ts', '.tsx'],
    },
    plugins: [

    ],
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                exclude: /node_modules/,
                loader: 'awesome-typescript-loader'
            },
        ]
    },
}
