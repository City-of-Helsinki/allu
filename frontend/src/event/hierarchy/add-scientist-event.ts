import {Event} from '../event';
import {NameItem} from '../../name-item';

export class AddScientistEvent extends Event {
  constructor(public nameItem: NameItem) {
    super('AddScientistEvent');
  }
}
