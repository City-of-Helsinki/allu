export class ObjectUtil {
  static clone(source: any) {
    if (typeof source !== 'object') {
      return source;
    } else {
      let key;
      let value;
      let cloned = Object.create(source);

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
}
