import {Component, Input} from '@angular/core';
import {SelectionEventService} from './selection-event.service';

@Component({
  selector: 'selection-item',
  template: require('./selection-item.component.html'),
  styles: [
    require('./selection-item.component.scss')
  ]
})
export class SelectionItemComponent {

  @Input() item: any;
  @Input() itemLabel: string;

  selected = false;

  constructor(private selectionService: SelectionEventService) {}

  toggle(): void {
    this.selected = !this.selected;
    this.selectionService.next({item: this.item, selected: this.selected});
  }
}
