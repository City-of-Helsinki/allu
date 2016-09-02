import {SearchbarFilter} from './searchbar-filter';
import {Event} from '../event';

export class SearchbarUpdateEvent extends Event {
  constructor(public searchbarFilter: SearchbarFilter) {
    super();
  }
}
