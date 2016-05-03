import {Task} from '../task';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';
import {NameListService} from '../../name-list.service';
import {AddScientistEvent} from '../../event/hierarchy/add-scientist-event';
import {ScientistListingEvent} from '../../event/hierarchy/scientist-listing-event';
import {EventListener} from '../../event/event-listener';

export class AddScientistTask extends Task {

  constructor(private nameListService: NameListService) {
    super();
  }

  public run(runner: EventListener, eventService: EventService, event: Event): void {
    let evt: AddScientistEvent = <AddScientistEvent>event;
    evt.nameItem.active = false;
    this.nameListService.addItem(evt.nameItem);
    eventService.send(runner, new ScientistListingEvent(this.nameListService.getItems()));
  }
}
