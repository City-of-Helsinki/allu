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

/*
 * Compares given swagger definition and data. Both should contain all the keys of each other i.e. all swagger definitions should have
 * corresponding data value and no data value should exist without swagger definition.
 */
function compareAgainstSwaggerSpec(definitionDataPairs) {
  let diff = definitionDataPairs.reduce(
    (acc, curr) => acc.concat(intersectionComplement(new Set(Object.keys(curr.definition)), new Set(Object.keys(curr.data)))), []);
  return diff;
}

/*
 * Returns values that are not shared by the sets.
 */
function intersectionComplement(set1, set2) {
  let intersectionComplement = [];
  if (!set1 || !set2) {
    throw new Error('intersectionComplement requires sets to be defined');
  }
  [...set1].every((o) => set2.has(o) || intersectionComplement.push(o));
  [...set2].every((o) => set1.has(o) || intersectionComplement.push(o));
  return intersectionComplement;
}


module.exports.deepCompareNonNull = deepCompareNonNull;
module.exports.compareAgainstSwaggerSpec = compareAgainstSwaggerSpec;