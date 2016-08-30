import {Component, Input, OnInit, OnDestroy, Output, EventEmitter} from '@angular/core';
import {MdToolbar} from '@angular2-material/toolbar';
import {MaterializeDirective} from 'angular2-materialize';

import {Event} from '../../event/event';
import {StringUtil} from '../../util/string.util';
import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {SearchbarFilter} from '../../event/search/searchbar-filter';
import {SearchbarUpdateEvent} from '../../event/search/searchbar-updated-event';
import {MapHub} from '../../service/map-hub';
import {ApplicationHub} from '../../service/application-hub';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../util/time.util';

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

export class SearchbarComponent implements OnInit {

  @Output() searchUpdated = new EventEmitter();
  @Input() search: string;

  private pickadateParams = PICKADATE_PARAMETERS;
  private _startDate: Date;
  private _endDate: Date;

  constructor(private mapHub: MapHub, private applicationHub: ApplicationHub) {}

  ngOnInit(): void {
    this.notifySearchUpdated();
  }

  public notifySearchUpdated(): void {
    this.mapHub.addSearch(this.search);
    let filter = new SearchbarFilter(this.search, this._startDate, this._endDate);

    this.mapHub.addSearch(this.search);
    this.applicationHub.addSearchFilter(filter);
  }

  set startDate(date: string) {
    this._startDate = TimeUtil.getDateFromUi(date);
    this.notifySearchUpdated();
  }

  get startDate(): string {
    return TimeUtil.getUiDateString(this._startDate);
  }

  set endDate(date: string) {
    this._endDate = TimeUtil.getDateFromUi(date);
    this.notifySearchUpdated();
  }

  get endDate(): string {
    return TimeUtil.getUiDateString(this._endDate);
  }
}
