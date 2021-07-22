const path = require('path');

module.exports = [
    // Config for no minification.
    {
      name: 'un-minified',
      output: {
        path: path.resolve('./out'),
        filename: 'main.js',
      },
      entry: './out/index.js',
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
    },

    // Config for minification.
    {
      name: 'minified',
      output: {
        path: path.resolve('./out'),
        filename: 'main.js',
      },
      entry: './out/index.js',
      module: {
        rules: [
          {
            test: /\.js$/,
            enforce: "pre",
            use: ["source-map-loader"],
          },
        ],
      }
    }
];

