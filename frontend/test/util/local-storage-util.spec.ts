import {LocalStorageUtil} from '../../src/app/util/local-storage.util';

describe('LocalStorageUtil', () => {
  const store = {};

  beforeEach(() => {
    spyOn(localStorage, 'getItem').and.callFake((key) => {
      return store[key];
    });

    spyOn(localStorage, 'setItem').and.callFake((key, item) => {
      store[key] = item;
    });

    spyOn(localStorage, 'removeItem').and.callFake((key) => {
      store[key] = undefined;
    });
  });

  it('get array of items from localstorage', () => {
    const key = 'key';
    const items =  [1, 2, 3, 4];
    store[key] = JSON.stringify(items);
    expect(LocalStorageUtil.getItemArray(key)).toEqual(items);
  });

  it('get empty array when no item is found', () => {
    expect(LocalStorageUtil.getItemArray('none')).toEqual([]);
  });

  it('should remove item from localStorage', () => {
    const key = 'key';
    const items =  [1, 2, 3, 4];
    store[key] = JSON.stringify(items);
    LocalStorageUtil.remove(key);
    expect(localStorage.getItem(key)).toBeUndefined();
  });

  it('should add items to array in localstorage', () => {
    const key = 'key';
    const items =  [1, 2, 3, 4];
    store[key] = JSON.stringify(items);
    const newItems = [5, 6];
    LocalStorageUtil.addItemsToArray(key, newItems);
    expect(LocalStorageUtil.getItemArray(key)).toEqual(items.concat(newItems));
  });

  it('should filter duplicates on add', () => {
    const key = 'key';
    const items =  [1, 2, 3, 4];
    store[key] = JSON.stringify(items);
    LocalStorageUtil.addItemsToArray(key, items);
    expect(LocalStorageUtil.getItemArray(key)).toEqual(items);
  });

  it('should remove given item from array', () => {
    const key = 'key';
    const items =  [1, 2, 3, 4];
    store[key] = JSON.stringify(items);
    LocalStorageUtil.removeItemFromArray(key, 2);
    expect(LocalStorageUtil.getItemArray(key)).toEqual([1, 3, 4]);
  });
});
