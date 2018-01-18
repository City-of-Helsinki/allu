import {Directive, Input, Output, EventEmitter, HostListener, Renderer, ViewContainerRef, OnInit
} from '@angular/core';
import {Sort} from '../../../model/common/sort';

@Directive({
  selector: 'th[sortBy]'
})
export class SortByDirective implements OnInit {
  @Input() sortBy: string;
  @Output() onSortChange = new EventEmitter<Sort>();

  private localSort: Sort;
  private tableHeaderEl: HTMLTableHeaderCellElement;
  private iconEl: HTMLElement;


  constructor(private renderer: Renderer, public  viewContainerRef: ViewContainerRef) {
    this.tableHeaderEl = viewContainerRef.element.nativeElement;
    this.tableHeaderEl.classList.add('clickable');

    // Add icon
    this.iconEl = this.renderer.createElement(this.tableHeaderEl, 'i');
    this.iconEl.className = 'material-icons sort-icon';
    this.iconEl.innerHTML = '';
  }

  ngOnInit(): void {
    this.localSort = this.localSort || new Sort(undefined, undefined);
    this.tableHeaderEl.appendChild(this.iconEl);
  }

  @HostListener('click', ['$event']) onClick(event: any) {
    this.changeSort();
  }

  @Input() set currentSort(sort: Sort) {
    this.localSort = sort;
    this.updateIcon();
  }

  changeSort(): void {
    const unsorted = this.localSort.field !== this.sortBy || this.localSort.direction === undefined;

    if (unsorted) {
      this.localSort = new Sort(this.sortBy, 'desc');
    } else if (this.localSort.direction === 'desc') {
      this.localSort = new Sort(this.localSort.field, 'asc');
    } else {
      this.localSort = new Sort(this.sortBy, undefined);
    }

    this.onSortChange.emit(this.localSort);
    this.updateIcon();
  }

  updateIcon(): void {
    this.iconEl.innerHTML = this.iconForField();
  }

  private iconForField(): string {
    return this.sortBy === this.localSort.field ? this.localSort.icon() : '';
  }
}
