import {Component, OnInit, OnDestroy, Input} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import '../../rxjs-extensions.ts';

import {Application} from '../../model/application/application';
import {ApplicationHub} from '../../service/application/application-hub';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {translations} from '../../util/translations';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationStatus} from '../../model/application/application-status-change';
import {ApplicationType} from '../../model/application/type/application-type';
import {Sort} from '../../model/common/sort';
import {Direction} from '../../model/common/sort';

@Component({
  selector: 'workqueue',
  template: require('./workqueue.component.html'),
  styles: [
    require('./workqueue.component.scss')
  ]
})
export class WorkQueueComponent implements OnInit, OnDestroy {
  applicationRows: Array<ApplicationRow>;
  allSelected = false;
  sort = new Sort(undefined, undefined);
  private applicationQuery = new BehaviorSubject<ApplicationSearchQuery>(new ApplicationSearchQuery());
  private translations = translations;
  private items: Array<string> = ['Ensimmäinen', 'Toinen', 'Kolmas', 'Neljäs', 'Viides'];
  private handlers: Array<string> = ['TestHandler'];
  private applicationStatuses = EnumUtil.enumValues(ApplicationStatus);
  private applicationTypes = EnumUtil.enumValues(ApplicationType);

  constructor(private applicationHub: ApplicationHub) {
  }

  ngOnInit() {
    this.applicationQuery.asObservable()
      .debounceTime(300)
      .distinctUntilChanged()
      .switchMap(query => this.applicationHub.searchApplications(query))
      .map(applications => this.toApplicationRows(applications))
      .subscribe(applicationRows => {
        this.applicationRows = applicationRows;
      });
  }

  ngOnDestroy() {
  }

  queryChanged(query: ApplicationSearchQuery) {
    this.applicationQuery.next(query.withSort(this.sort));
  }

  toggleAll() {
    this.allSelected = !this.allSelected;
    this.applicationRows.forEach(row => row.selected = this.allSelected);
  }

  sortBy(field: string): void {
    let unsorted = this.sort.field !== field || this.sort.direction === undefined;

    if (unsorted) {
      this.sort = new Sort(field, Direction.DESC);
    } else if (this.sort.direction === Direction.DESC) {
      this.sort = new Sort(this.sort.field, Direction.ASC);
    } else {
      this.sort = new Sort(field, undefined);
    }
    this.queryChanged(this.applicationQuery.getValue());
  }

  iconForField(field: string): string {
    return field === this.sort.field ? this.sort.icon() : '';
  }

  private toApplicationRows(applications: Array<Application>): Array<ApplicationRow> {
    return applications
      .map(application => {
        return {
          selected: false,
          application: application
        };
      });
  }
}

interface ApplicationRow {
  selected: boolean;
  application: Application;
}
