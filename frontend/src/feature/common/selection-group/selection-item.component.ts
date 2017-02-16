import {Component, Input} from '@angular/core';
import {SelectionEventService} from './selection-event.service';

type SelectType = 'CHECKBOX' | 'CHIP';

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
  @Input() selectType: SelectType = 'CHECKBOX';

  selected = false;

  constructor(private selectionService: SelectionEventService) {}

  toggle(): void {
    this.selected = !this.selected;
    this.notify();
  }

  notify(): void {
    this.selectionService.next({item: this.item, selected: this.selected});
  }
}
