// Taken from https://davidwalsh.name/javascript-arguments
function getArgs(func) {
  // First match everything inside the function argument parens.
  // NOTE: the following regex has been slightly changed form the original.
  var args = func.toString().match(/function.*?\(([^)]*)\)/)[1];

  // Split the arguments string into an array comma delimited.
  return args.split(',').map(function(arg) {
    // Ensure no inline comments are parsed and trim the whitespace.
    return arg.replace(/\/\*.*\*\//, '').trim();
  }).filter(function(arg) {
    // Ensure no undefined values are added.
    return arg;
  });
}


/**
 * Generates Gaussian noise via a Box-Muller transform.
 * @param {number} mu Mean of normal distribution.
 * @param {number} sigma Varance of normal distribution.
 */
function gaussianNoise(mu, sigma) {
    const u1 = Math.random();
    const u2 = Math.random();
    return mu + sigma * Math.sqrt(-2 * Math.log(u1))
        * Math.cos(2 * Math.PI * u2);
};
