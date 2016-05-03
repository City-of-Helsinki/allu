import {Event} from '../event';
import {NameItem} from '../../name-item';
export class ScientistListingEvent extends Event {
  constructor(public nameItems: Array<NameItem>) {
    super('ScientistListingEvent');
  }
}
