import {NameItem} from './name-item';
export class NameListService {
  public nameItems: Array<NameItem> =
    [{name: 'Edsger Dijkstra 2', active: false }, {name: 'Donald Knuth', active: false },
      {name: 'Alan Turing', active: false }, {name: 'Grace Hopper', active: false }];

  addItem(nameItem: NameItem) : void {
    this.nameItems.push(nameItem);
  }

  getItems() : Array<NameItem> {
    return this.nameItems;
  }
/*
  names = [
    'Edsger Dijkstra',
    'Donald Knuth',
    'Alan Turing',
    'Grace Hopper'
  ];

  get(): string[] {
    return this.names;
  }
  add(value: string): void {
    this.names.push(value);
  }
  */
}
