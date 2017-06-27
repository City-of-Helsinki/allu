export class ObjectUtil {
  static clone(source: Object) {
    let key;
    let value;
    let cloned = Object.create(source);

    for (key in source) {
      if (source.hasOwnProperty(key)) {
        value = source[key];

        if (!!value && typeof value === 'object') {
          cloned[key] = ObjectUtil.clone(value);
        } else {
          cloned[key] = value;
        }
      }
    }
    return cloned;
  }
}
