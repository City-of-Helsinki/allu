import {Component, Input, Output, EventEmitter, OnInit, Renderer, ViewContainerRef} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ArrayUtil} from '../../../util/array-util';

@Component({
  selector: 'auto-completion-list',
  template: require('./auto-completion-list.component.html'),
  styles: [
    require('./auto-completion-list.component.scss')
  ]
})
export class AutoCompletionListComponent implements OnInit {
  @Input() entries: Observable<Array<any>>;
  @Input() idField: any;
  @Input() nameField: string;
  @Input() sortBy: (a, b) => number;

  @Output() onSelection = new EventEmitter<any>();
  sortedEntries = [];

  constructor(private renderer: Renderer, private viewContainerRef: ViewContainerRef) {
    this.renderer.setElementClass(viewContainerRef.element.nativeElement, 'auto-completion-list', true);
  }

  ngOnInit(): void {
    this.sortBy = this.sortBy || this.sortByField(this.nameField);

    this.entries
      .subscribe(entries => this.sortedEntries = entries.sort(this.sortBy));
  }

  select(selection: any) {
    this.onSelection.emit(selection);
  }

  private sortByField(fieldName: string): (a, b) => number {
    return ArrayUtil.naturalSort(item => item[fieldName]);
  }
}
