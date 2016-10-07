import {Component, Input, OnInit, OnDestroy, Output, EventEmitter} from '@angular/core';

import {StringUtil} from '../../util/string.util';
import {SearchbarFilter} from '../../service/searchbar-filter';
import {MapHub} from '../../service/map-hub';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../util/time.util';
import {UIStateHub} from '../../service/ui-state/ui-state-hub';
import {UIState} from '../../service/ui-state/ui-state';
import {Geocoordinates} from '../../model/common/geocoordinates';

// To use Materialize functionality
declare var Materialize: any;

@Component({
  selector: 'searchbar',
  template: require('./searchbar.component.html'),
  styles: [
    require('./searchbar.component.scss')
  ]
})
export class SearchbarComponent implements OnInit {

  @Input() addressSearch: string;
  @Output() searchUpdated = new EventEmitter<SearchbarFilter>();

  private pickadateParams = PICKADATE_PARAMETERS;
  private _startDate: Date;
  private _endDate: Date;
  private notFound: boolean;

  constructor(private mapHub: MapHub) {}

  ngOnInit(): void {
    this.notifySearchUpdated();

    this.mapHub.coordinates()
      .filter(coords => !coords.isDefined())
      .forEach(coords => Materialize.toast('Osoitetta ei löytynyt', 4000));
  }

  public notifySearchUpdated(): void {
    let filter = new SearchbarFilter(this.addressSearch, this._startDate, this._endDate);
    this.searchUpdated.emit(filter);
    this.mapHub.addSearchFilter(filter);
  }

  public searchAddress(term: string) {
    this.mapHub.addSearch(term);
    this.notifySearchUpdated();
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
