import {AfterViewInit, Component, ContentChildren, EventEmitter, forwardRef, OnDestroy, Output, QueryList} from '@angular/core';
import {SelectionEvent, SelectionEventService} from './selection-event.service';
import {Subscription, BehaviorSubject} from 'rxjs';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {SelectionItemComponent} from './selection-item.component';

const SELECTION_GROUP_VALUE_ACCESSOR = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => SelectionGroupComponent),
  multi: true
};

const ID_FIELD = 'id';

@Component({
  selector: 'selection-group',
  template: '<ng-content></ng-content>',
  styleUrls: [
    './selection-group.component.scss'
  ],
  providers: [SELECTION_GROUP_VALUE_ACCESSOR, SelectionEventService]
})
export class SelectionGroupComponent implements OnDestroy, ControlValueAccessor, AfterViewInit {
  @Output() select = new EventEmitter<SelectionEvent>();

  @ContentChildren(SelectionItemComponent, {descendants: true}) selectionItems: QueryList<SelectionItemComponent>;

  private selectedItems$ = new BehaviorSubject<Array<any>>([]);
  private eventSubscription: Subscription;
  private selectedItemsSubscription: Subscription;

  constructor(selectionService: SelectionEventService) {
    this.eventSubscription = selectionService.subscribe(event => this.onSelectionEvent(event));
  }

  ngOnDestroy(): void {
    this.eventSubscription.unsubscribe();
    this.selectedItemsSubscription.unsubscribe();
  }

  ngAfterViewInit(): void {
    // Defer setting the value in order to avoid the "Expression
    // has changed after it was checked" errors from Angular.
    Promise.resolve().then(() => {
      this.selectedItemsSubscription = this.selectedItems$.subscribe(items =>
        this.selectionItems.forEach(item => {
          item.selected = items.some(i => this.isSame(i, item.item));
        }));
    });
  }

  writeValue(items: Array<any>): void {
    if (items) {
      this.selectedItems$.next(items);
    }
  }

  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: any): void {}

  private _onChange = (_: any) => {};

  private onSelectionEvent(event: SelectionEvent): void {
    const current = this.selectedItems$.getValue();
    let next;
    if (event.selected) {
      next = current.concat(event.item);
    } else {
      next = current.filter(item => !this.isSame(item, event.item));
    }
    this.select.emit(event);
    this._onChange(next);
    this.selectedItems$.next(next);
  }

  private isSame(item1: any, item2: any): boolean {
    return (item1 === item2) || this.idSame(item1, item2);
  }

  private idSame(item1: any, item2: any): boolean {
    return item1.hasOwnProperty(ID_FIELD)
      && item2.hasOwnProperty(ID_FIELD)
      && item1.id === item2.id;
  }
}
