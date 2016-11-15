import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {AutoCompletionEntry} from './auto-completion-entry';

export type SelectIdentifier = number | string;

@Component({
  selector: 'auto-completion-list',
  template: require('./auto-completion-list.component.html'),
  styles: [
    require('./auto-completion-list.component.scss')
  ]
})
export class AutoCompletionListComponent implements OnInit {
  @Input() entries: Observable<Array<AutoCompletionEntry>>;
  @Output() onSelection = new EventEmitter<SelectIdentifier>();
  sortedEntries = [];

  ngOnInit(): void {
    this.entries
      .subscribe(entries => this.sortedEntries = entries);
  }

  select(selection: SelectIdentifier) {
    this.onSelection.emit(selection);
  }
}
