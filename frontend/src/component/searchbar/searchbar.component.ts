import {Component, Input, OnInit, OnDestroy, Output, EventEmitter} from '@angular/core';
import {MdToolbar} from '@angular2-material/toolbar';
import {MaterializeDirective} from 'angular2-materialize';

import {Event} from '../../event/event';
import {StringUtil} from '../../util/string.util';
import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {SearchbarFilter} from '../../event/search/searchbar-filter';
import {SearchbarUpdateEvent} from '../../event/search/searchbar-updated-event';
import {MapHub} from '../../service/map-hub';

@Component({
  selector: 'searchbar',
  moduleId: module.id,
  template: require('./searchbar.component.html'),
  styles: [
    require('./searchbar.component.scss')
  ],
  directives: [
    MdToolbar,
    MaterializeDirective
  ]
})

export class SearchbarComponent  {

  @Output() searchUpdated = new EventEmitter();
  @Input() search: string;
  private _startDate: Date;
  private _endDate: Date;

  constructor(private mapHub: MapHub) {}

  public handle(event: Event): void {
  }

  public notifySearchUpdated(): void {
    let filter = new SearchbarFilter(this.search, this._startDate, this._endDate);
    this.mapHub.addSearch(this.search);
  }

  set startDate(date: Date) {
    this._startDate = date;
    this.notifySearchUpdated();
  }

  get startDate(): Date {
    return this._startDate;
  }

  set endDate(date: Date) {
    this._endDate = date;
    this.notifySearchUpdated();
  }

  get endDate(): Date {
    return this._endDate;
  }
}
