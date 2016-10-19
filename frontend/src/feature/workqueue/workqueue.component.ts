import {Component, OnInit, OnDestroy, Input} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {MdTabChangeEvent} from '@angular/material/tabs';
import '../../rxjs-extensions.ts';

import {Application} from '../../model/application/application';
import {ApplicationHub} from '../../service/application/application-hub';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {translations} from '../../util/translations';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationStatus} from '../../model/application/application-status-change';
import {ApplicationType} from '../../model/application/type/application-type';
import {Sort} from '../../model/common/sort';

@Component({
  selector: 'workqueue',
  template: require('./workqueue.component.html'),
  styles: [
    require('./workqueue.component.scss')
  ]
})
export class WorkQueueComponent implements OnInit, OnDestroy {
  applications: Observable<Array<Application>>;
  tabs = ['Omat', 'Yhteiset'];
  tab = 'Omat';
  private selectedApplicationIds = new Array<number>();
  private applicationQuery = new BehaviorSubject<ApplicationSearchQuery>(new ApplicationSearchQuery());
  private sort: Sort;
  private translations = translations;
  private items: Array<string> = ['Ensimmäinen', 'Toinen', 'Kolmas', 'Neljäs', 'Viides'];
  private handlers: Array<string> = ['TestHandler'];
  private applicationStatuses = EnumUtil.enumValues(ApplicationStatus);
  private applicationTypes = EnumUtil.enumValues(ApplicationType);

  constructor(private applicationHub: ApplicationHub) {
  }

  ngOnInit() {
    this.applications = this.applicationQuery.asObservable()
      .debounceTime(300)
      .distinctUntilChanged()
      .switchMap(query => this.applicationHub.searchApplications(query));
  }

  ngOnDestroy() {}

  queryChanged(query: ApplicationSearchQuery) {
    this.applicationQuery.next(query.withSort(this.sort));
  }

  sortChanged(sort: Sort) {
    // use old query parameters and new sort
    this.sort = sort;
    this.queryChanged(this.applicationQuery.getValue());
  }

  selectionChanged(applicationIds: Array<number>) {
    this.selectedApplicationIds = applicationIds;
  }

  tabSelected(event: MdTabChangeEvent) {
    this.tab = this.tabs[event.index];
  }
}
