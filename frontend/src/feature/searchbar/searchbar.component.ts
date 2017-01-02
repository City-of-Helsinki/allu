import {Component, Input, OnInit, OnDestroy, Output, EventEmitter, AfterViewInit} from '@angular/core';
import {FormGroup, FormBuilder, FormControl} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';
import {Subject} from 'rxjs/Subject';

import {SearchbarFilter} from '../../service/searchbar-filter';
import {MapHub} from '../../service/map-hub';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../util/time.util';
import {MaterializeUtil} from '../../util/materialize.util';
import {PostalAddress} from '../../model/common/postal-address';

enum BarType {
  SIMPLE,
  BAR,
  ADVANCED
};

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
  @Input() barType: string = BarType[BarType.BAR];

  @Output() searchUpdated = new EventEmitter<SearchbarFilter>();
  @Output() onShowAdvanced = new EventEmitter<boolean>();

  searchForm: FormGroup;
  addressSearchSubject = new Subject<Array<PostalAddress>>();
  addressSearchResults = this.addressSearchSubject.asObservable()
    .map(addresses => addresses.map(address => { return { id: address.uiStreetAddress, name: address.uiStreetAddress}; }));


  private coordinateSubscription: Subscription;
  private pickadateParams = PICKADATE_PARAMETERS;
  private notFound: boolean;

  constructor(private fb: FormBuilder, private mapHub: MapHub) {
    this.searchForm = this.fb.group({
      address: '',
      startDate: '',
      endDate: ''
    });
  }

  ngOnInit(): void {
    this.searchForm.patchValue({
      address: this.addressSearch,
      startDate: TimeUtil.getUiDateString(this.startDate),
      endDate: TimeUtil.getUiDateString(this.endDate)
    });

    this.coordinateSubscription = this.mapHub.coordinates()
      .filter(coords => !coords.isDefined())
      .subscribe(coords => MaterializeUtil.toast('Osoitetta ei löytynyt', 4000));

    this.searchForm.valueChanges.subscribe(form => this.notifySearchUpdated(form));
    this.notifySearchUpdated(this.searchForm.value);
  }

  ngAfterViewInit(): void {
    MaterializeUtil.updateTextFields(50);
  }

  ngOnDestroy(): void {
    this.coordinateSubscription.unsubscribe();
  }

  public notifySearchUpdated(form: {address: string, startDate: string, endDate: string}): void {
    let filter = new SearchbarFilter(form.address, TimeUtil.getDateFromUi(form.startDate), TimeUtil.getDateFromUi(form.endDate));
    this.searchUpdated.emit(filter);
    this.mapHub.addSearchFilter(filter);
  }

  public searchAddress(term) {
    this.mapHub.addSearch(term.name);
    this.searchForm.patchValue({address: term.name});
  }

  public onAddressSearchChange(searchTerm: string) {
    this.mapHub.addressSearch(searchTerm)
      .debounceTime(300)
      .subscribe(addresses => this.addressSearchSubject.next(addresses));
  }

  public showMore() {
    this.onShowAdvanced.emit(true);
  }
}
