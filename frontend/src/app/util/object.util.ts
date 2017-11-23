export class ObjectUtil {
  static clone(source: any) {
    if (typeof source !== 'object') {
      return source;
    } else {
      let key;
      let value;
      const cloned = Object.create(source);

      for (key in source) {
        if (source.hasOwnProperty(key)) {
          value = source[key];

          if (!!value && value instanceof Date) {
            cloned[key] = new Date(value.getTime());
          } else if (!!value && value.constructor === Array) {
            cloned[key] = value.map(entry => ObjectUtil.clone(entry));
          } else if (!!value && typeof value === 'object') {
            cloned[key] = ObjectUtil.clone(value);
          } else {
            cloned[key] = value;
          }
        }
      }
      return cloned;
    }
  }

  static equal(first: any, second: any): boolean {
    if (first === second) {
      return true;
    } else if (first === undefined || second === undefined) {
      return false;
    } else {
      let prop;
      for (prop in first) {
        if (first.hasOwnProperty(prop) !== second.hasOwnProperty(prop)) {
          return false;
        } else if (typeof first[prop] !== typeof second[prop]) {
          return false;
        } else {
          switch (typeof (first[prop])) {
            case 'object':
            case 'function':
              if (!ObjectUtil.equal(first[prop], second[prop])) {
                return false;
              }
              break;
            default:
              if (first[prop] !== second[prop]) {
                return false;
              }
              break;
          }
        }
      }
      return true;
    }
  }
}
