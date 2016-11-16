import {Component, Input, OnInit, OnDestroy, Output, EventEmitter, AfterViewInit} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';

import {StringUtil} from '../../util/string.util';
import {SearchbarFilter} from '../../service/searchbar-filter';
import {MapHub} from '../../service/map-hub';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../util/time.util';
import {UIStateHub} from '../../service/ui-state/ui-state-hub';
import {UIState} from '../../service/ui-state/ui-state';
import {Geocoordinates} from '../../model/common/geocoordinates';
import {MaterializeUtil} from '../../util/materialize.util';
import {PostalAddress} from '../../model/common/postal-address';

@Component({
  selector: 'searchbar',
  template: require('./searchbar.component.html'),
  styles: [
    require('./searchbar.component.scss')
  ]
})
export class SearchbarComponent implements OnInit, OnDestroy, AfterViewInit {

  @Input() addressSearch: string;
  @Input() startDate: Date;
  @Input() endDate: Date;
  @Output() searchUpdated = new EventEmitter<SearchbarFilter>();

  addressSearchSubject = new Subject<Array<PostalAddress>>();
  addressSearchResults = this.addressSearchSubject.asObservable()
    .map(addresses => addresses.map(address => { return { id: address.uiStreetAddress, name: address.uiStreetAddress}; }));

  private coordinateSubscription: Subscription;
  private pickadateParams = PICKADATE_PARAMETERS;
  private notFound: boolean;

  constructor(private mapHub: MapHub) {}

  ngOnInit(): void {
    this.notifySearchUpdated();

    this.coordinateSubscription = this.mapHub.coordinates()
      .filter(coords => !coords.isDefined())
      .subscribe(coords => MaterializeUtil.toast('Osoitetta ei löytynyt', 4000));
  }

  ngAfterViewInit(): void {
    MaterializeUtil.updateTextFields(50);
  }

  ngOnDestroy(): void {
    this.coordinateSubscription.unsubscribe();
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

  public onAddressSearchChange(searchTerm: string) {
    this.mapHub.addressSearch(searchTerm)
      .debounceTime(300)
      .subscribe(addresses => this.addressSearchSubject.next(addresses));
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
