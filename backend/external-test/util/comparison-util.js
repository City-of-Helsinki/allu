
/*
 * Compares non null values of a to b. Objects and arrays are compared recursively.
 */
function deepCompareNonNull(path, a, b) {
  if (a === null) return [];
  if (b === null) return diffPair(path, a, b);
  if (typeof a !== typeof b) return diffPair(path, a, b);
  if (typeof a !== 'object' && !Array.isArray(a)) {
    if (a === b) {
      return [];
    } else {
      return diffPair(path, a, b);
    }
  }
  let diffList = [];
  if (Array.isArray(a)) {
    for (let i = 0; i < a.length; ++i) {
      diffList = diffList.concat(deepCompareNonNull(path, a[i], b[i]));
    }
  } else {
    // object comparison
    for (let k of Object.keys(a)){
      diffList = diffList.concat(deepCompareNonNull(path + '/' + k, a[k], b[k]));
    }
  }

  return diffList;
}

function diffPair(path, a, b) {
  return [{'path': path, 'original': a, 'compared': b}];
}

module.exports.deepCompareNonNull = deepCompareNonNull;
