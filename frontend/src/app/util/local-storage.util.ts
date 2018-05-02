import {ArrayUtil} from './array-util';

export class LocalStorageUtil {
  static remove(key: string) {
    localStorage.removeItem(key);
  }

  static getItemArray<T>(key: string): T[] {
    const item = localStorage.getItem(key);
    return item
      ? JSON.parse(item)
      : [];
  }

  static addItemsToArray<T>(key: string, items: T[]) {
    const current = LocalStorageUtil.getItemArray(key);
    const updated = current.concat(items).filter(ArrayUtil.unique);
    LocalStorageUtil.setItemArray(key, updated);
  }

  static removeItemFromArray<T>(key: string, T) {
    const current = LocalStorageUtil.getItemArray(key);
    LocalStorageUtil.setItemArray(key, current.filter(i => i !== T));
  }

  static setItemArray<T>(key: string, items: T[]) {
    localStorage.setItem(key, JSON.stringify(items));
  }
}
