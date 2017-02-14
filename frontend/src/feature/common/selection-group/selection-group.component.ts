import {
  Component, OnDestroy, Output, EventEmitter, forwardRef, QueryList, AfterContentInit, ContentChildren
} from '@angular/core';
import {SelectionEventService, SelectionEvent} from './selection-event.service';
import {Subscription} from 'rxjs/Subscription';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {SelectionItemComponent} from './selection-item.component';

const SELECTION_GROUP_VALUE_ACCESSOR = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => SelectionGroupComponent),
  multi: true
};

@Component({
  selector: 'selection-group',
  template: '<ng-content></ng-content>',
  styles: [],
  providers: [SELECTION_GROUP_VALUE_ACCESSOR]
})
export class SelectionGroupComponent implements OnDestroy, ControlValueAccessor, AfterContentInit {

  @Output() onSelection = new EventEmitter<any>();

  @ContentChildren(SelectionItemComponent) selectionItems: QueryList<SelectionItemComponent> = new QueryList<SelectionItemComponent>();

  private selectedItems: Array<any> = [];
  private eventSubscription: Subscription;

  constructor(private selectionService: SelectionEventService) {
    this.eventSubscription = selectionService.subscribe(event => this.onSelectionEvent(event));
  }

  ngOnDestroy(): void {
    this.eventSubscription.unsubscribe();
  }


  ngAfterContentInit(): void {
  }

  writeValue(items: Array<any>): void {
    this.selectedItems = items;

    if (items) {
      this.selectionItems.forEach(item => item.selected = this.selectedItems.some(i => i === item.item));
    }
  }

  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: any): void {}

  private _onChange = (_: any) => {};

  private onSelectionEvent(event: SelectionEvent): void {
    if (event.selected) {
      this.selectedItems.push(event.item);
    } else {
      this.selectedItems = this.selectedItems.filter(item => item !== event.item);
    }
    this.onSelection.emit(this.selectedItems);
    this._onChange(this.selectedItems);
  }
}
