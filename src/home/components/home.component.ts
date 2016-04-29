import {Component} from 'angular2/core';
import {CORE_DIRECTIVES, FORM_DIRECTIVES} from 'angular2/common';

import {NameListService} from '../../shared/services/name-list.service';
import {NameItem} from '../../shared/services/name-item';
import {EventService} from '../../shared/services/event/event.service';
import {EventListener} from '../../shared/services/event/event-listener';
import {Event} from '../../shared/services/event/event';
import {ScientistListingEvent} from '../../shared/services/event/hierarchy/scientist-listing-event';
import {AddScientistEvent} from '../../shared/services/event/hierarchy/add-scientist-event';

@Component({
  selector: 'sd-home',
  templateUrl: './home/components/home.component.html',
  styleUrls: ['./home/components/home.component.css'],
  directives: [FORM_DIRECTIVES, CORE_DIRECTIVES]
})
export class HomeComponent implements EventListener {
  newName: string;
  private nameItems: Array<NameItem> = [];

  constructor(public nameListService: NameListService, private eventService: EventService) {
    this.eventService.subscribe(this);
  }

  public handle(event: Event): void {
    console.log('HomeComponent eventti');
    if (event.type === 'ScientistListingEvent') {
      let evt: ScientistListingEvent = <ScientistListingEvent>event;
      this.nameItems = [];
      this.addNameItems(evt.nameItems, this.nameItems);
    }
  }
  
  private addNameItems(from: Array<NameItem>, to: Array<NameItem>) {
    for (var ni of from) {
      to.push(ni);
    }
  }

  /*
   * @param newname  any text as input.
   * @returns return false to prevent default form submit behavior to refresh the page.
   */
  addName(): boolean {
    // this.nameListService.addItem(new NameItem(this.newName, true));
    this.eventService.send(this, new AddScientistEvent(new NameItem(this.newName, true)));
    this.newName = '';
    return false;
  }

  subscribe(): void {
    this.eventService.subscribe(this);
  }

  unsubscribe(): void {
    this.eventService.unsubscribe(this);
  }
}
