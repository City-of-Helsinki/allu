import {Component, Input, OnInit, OnDestroy, Output, EventEmitter} from '@angular/core';
import {MdToolbar} from '@angular2-material/toolbar';
import {MaterializeDirective} from 'angular2-materialize';

import {Event} from '../../event/event';
import {StringUtil} from '../../util/string.util';
import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {SearchbarFilter} from '../../event/search/searchbar-filter';
import {SearchbarUpdateEvent} from '../../event/search/searchbar-updated-event';
import {MapHub} from '../../service/map-hub';
import {ApplicationHub} from '../../service/application/application-hub';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../util/time.util';
import {UIStateHub} from '../../service/ui-state/ui-state-hub';
import {UIState} from '../../service/ui-state/ui-state';

// To use Materialize functionality
declare var Materialize: any;

@Component({
  selector: 'searchbar',
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

  @Output() searchUpdated = new EventEmitter<SearchbarFilter>();
  @Input() search: string;

  private pickadateParams = PICKADATE_PARAMETERS;
  private _startDate: Date;
  private _endDate: Date;
  private errorMessage: string;

  constructor(private mapHub: MapHub, private applicationHub: ApplicationHub, private uiState: UIStateHub) {}

  ngOnInit(): void {
    this.notifySearchUpdated();

    this.uiState.uiState().subscribe((state) => this.uiStateUpdated(state));
  }

  public notifySearchUpdated(): void {
    this.mapHub.addSearch(this.search);
    let filter = new SearchbarFilter(this.search, this._startDate, this._endDate);

    this.mapHub.addSearch(this.search);
    this.searchUpdated.emit(filter);
    this.applicationHub.addSearchFilter(filter);
  }

  private uiStateUpdated(state: UIState) {
    if (!state.messages.isEmpty()) {
      Materialize.toast('Osoitetta ei löytynyt', 4000);
      this.uiState.clearMessages();
    }
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
