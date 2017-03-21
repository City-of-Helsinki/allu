import {Component, Input, OnInit, OnDestroy, Output, EventEmitter} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';
import {Subject} from 'rxjs/Subject';

import {SearchbarFilter} from '../../service/searchbar-filter';
import {MapHub} from '../../service/map/map-hub';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../util/time.util';
import {MaterializeUtil} from '../../util/materialize.util';
import {PostalAddress} from '../../model/common/postal-address';
import {Observable} from 'rxjs';

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
export class SearchbarComponent implements OnInit, OnDestroy {
  @Input() filter: Observable<SearchbarFilter> = Observable.empty();
  @Input() datesRequired: boolean = false;
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
  }

  ngOnInit(): void {
    this.searchForm = this.fb.group({
      address: [''],
      startDate: this.datesRequired ? ['', Validators.required] : [''],
      endDate: this.datesRequired ? ['', Validators.required] : ['']
    });

    this.filter.subscribe(filter => {
      this.searchForm.patchValue({
        address: filter.search,
        startDate: TimeUtil.getUiDateString(filter.startDate),
        endDate: TimeUtil.getUiDateString(filter.endDate)
      });
    });

    this.coordinateSubscription = this.mapHub.coordinates()
      .filter(coords => !coords.isDefined())
      .subscribe(coords => MaterializeUtil.toast('Osoitetta ei löytynyt', 4000));

    this.searchForm.valueChanges.subscribe(form => this.notifySearchUpdated(form));
    this.notifySearchUpdated(this.searchForm.value);
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
