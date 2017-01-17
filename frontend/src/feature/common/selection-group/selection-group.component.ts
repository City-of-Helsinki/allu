import {Component, OnDestroy, Output, EventEmitter} from '@angular/core';
import {SelectionEventService, SelectionEvent} from './selection-event.service';
import {Subscription} from 'rxjs/Subscription';

@Component({
  selector: 'selection-group',
  template: '<ng-content></ng-content>',
  styles: []
})
export class SelectionGroupComponent implements OnDestroy {

  @Output() onSelection = new EventEmitter<any>();

  private selectedItems: Array<any> = [];
  private eventSubscription: Subscription;

  constructor(private selectionService: SelectionEventService) {
    this.eventSubscription = selectionService.subscribe(event => this.onSelectionEvent(event));
  }

  ngOnDestroy(): void {
    this.eventSubscription.unsubscribe();
  }

  private onSelectionEvent(event: SelectionEvent): void {
    if (event.selected) {
      this.selectedItems.push(event.item);
    } else {
      this.selectedItems = this.selectedItems.filter(item => item !== event.item);
    }
    this.onSelection.emit(this.selectedItems);
  }
}
