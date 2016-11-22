import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {AutoCompletionEntry} from './auto-completion-entry';

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

  ngOnInit(): void {
    this.sortBy = this.sortBy || this.sortByField(this.nameField);

    this.entries
      .subscribe(entries => this.sortedEntries = entries.sort(this.sortBy));
  }

  select(selection: any) {
    this.onSelection.emit(selection);
  }


  private sortByField(fieldName: string): (a, b) => number {
    return (left, right) => {
      if (left[fieldName] > right[fieldName]) {
        return 1;
      }
      if (left[fieldName] < right[fieldName]) {
        return -1;
      }
      // a must be equal to b
      return 0;
    };
  }
}
