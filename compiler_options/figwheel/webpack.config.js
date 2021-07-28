const path = require('path');

module.exports = [
    // Config that prepares a bundle in the figwheel-target directory.
    {
      name: 'figwheel-un-minified',
      output: {
        path: path.resolve('./figwheel-target/public/out'),
        filename: 'main.js',
      },
      entry: './figwheel-target/public/out/index.js',
      module: {
        rules: [
          {
            test: /\.js$/,
            enforce: "pre",
            use: ["source-map-loader"],
          },
        ],
      },
      optimization: {
        minimize: false,
      }
    }
];

