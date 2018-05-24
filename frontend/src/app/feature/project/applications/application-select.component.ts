import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl} from '@angular/forms';
import {Subject, Observable} from 'rxjs';
import {Application} from '../../../model/application/application';
import {Store} from '@ngrx/store';
import * as fromProject from '../reducers/';
import {Search} from '../actions/application-search-actions';
import {MatOption} from '@angular/material';
import {debounceTime, takeUntil} from 'rxjs/internal/operators';

@Component({
  selector: 'application-select',
  templateUrl: './application-select.component.html',
  styleUrls: ['./application-select.component.scss']
})
export class ApplicationSelectComponent implements OnInit, OnDestroy {
  @Input() matchingApplications: Application[];

  @Output() searchChange = new EventEmitter<string>(true);
  @Output() selectedChange = new EventEmitter<number>(true);

  searchControl = new FormControl();

  private destroy = new Subject<boolean>();

  constructor() {
  }

  ngOnInit(): void {
    this.searchControl.valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300)
    ).subscribe(term => this.searchChange.emit(term));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  add(option: MatOption): void {
    const application = option.value;
    this.selectedChange.emit(application.id);
    this.searchControl.reset();
  }

  displayName(application: Application): string {
    return application
      ? `${application.applicationId}: ${application.name}`
      : undefined;
  }
}
