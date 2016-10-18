import {Component, Input, OnInit, OnDestroy, Output, EventEmitter, AfterViewInit} from '@angular/core';

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
export class SearchbarComponent implements OnInit, AfterViewInit {

  @Input() addressSearch: string;
  @Input() startDate: Date;
  @Input() endDate: Date;
  @Output() searchUpdated = new EventEmitter<SearchbarFilter>();

  private pickadateParams = PICKADATE_PARAMETERS;
  private notFound: boolean;

  constructor(private mapHub: MapHub) {}

  ngOnInit(): void {
    this.notifySearchUpdated();

    this.mapHub.coordinates()
      .filter(coords => !coords.isDefined())
      .forEach(coords => Materialize.toast('Osoitetta ei löytynyt', 4000));
  }

  ngAfterViewInit(): void {
    setTimeout(() => Materialize.updateTextFields(), 10);
  }

  public notifySearchUpdated(): void {
    let filter = new SearchbarFilter(this.addressSearch, this.startDate, this.endDate);
    this.searchUpdated.emit(filter);
    this.mapHub.addSearchFilter(filter);
  }

  public searchAddress(term: string) {
    this.mapHub.addSearch(term);
    this.notifySearchUpdated();
  }

  set uiStartDate(date: string) {
    this.startDate = TimeUtil.getDateFromUi(date);
    this.notifySearchUpdated();
  }

  get uiStartDate(): string {
    return TimeUtil.getUiDateString(this.startDate);
  }

  set uiEndDate(date: string) {
    this.endDate = TimeUtil.getDateFromUi(date);
    this.notifySearchUpdated();
  }

  get uiEndDate(): string {
    return TimeUtil.getUiDateString(this.endDate);
  }
}
