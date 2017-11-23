import {Component, Input, ViewEncapsulation} from '@angular/core';
import {SelectionEventService} from './selection-event.service';

type SelectType = 'CHECKBOX' | 'CHIP';

@Component({
  selector: 'selection-item',
  templateUrl: './selection-item.component.html',
  styleUrls: ['./selection-item.component.scss'],
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
