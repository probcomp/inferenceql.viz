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
