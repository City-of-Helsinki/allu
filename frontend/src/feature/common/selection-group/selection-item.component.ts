import {Component, Input, ViewEncapsulation} from '@angular/core';
import {SelectionEventService} from './selection-event.service';

type SelectType = 'CHECKBOX' | 'CHIP';

@Component({
  selector: 'selection-item',
  template: require('./selection-item.component.html'),
  styles: [
    require('./selection-item.component.scss')
  ],
  encapsulation: ViewEncapsulation.None
})
export class SelectionItemComponent {

  @Input() item: any;
  @Input() selectType: SelectType = 'CHECKBOX';

  selected = false;

  constructor(private selectionService: SelectionEventService) {}

  notify(): void {
    this.selectionService.next({item: this.item, selected: this.selected});
  }
}
