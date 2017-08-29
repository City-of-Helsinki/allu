import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';

import {SearchbarFilter} from '../../service/searchbar-filter';
import {MapHub} from '../../service/map/map-hub';
import {TimeUtil} from '../../util/time.util';
import {PostalAddress} from '../../model/common/postal-address';
import {Observable} from 'rxjs';
import {NotificationService} from '../../service/notification/notification.service';
import {ArrayUtil} from '../../util/array-util';
import {StringUtil} from '../../util/string.util';

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
  addressControl: FormControl;
  matchingAddresses: Observable<Array<PostalAddress>>;

  private coordinateSubscription: Subscription;

  constructor(private fb: FormBuilder, private mapHub: MapHub) {
    this.addressControl = this.fb.control('');
    this.searchForm = this.fb.group({
      address: this.addressControl,
      startDate: this.datesRequired ? ['', Validators.required] : [''],
      endDate: this.datesRequired ? ['', Validators.required] : ['']
    });
  }

  ngOnInit(): void {
    this.filter.subscribe(filter => {
      this.searchForm.patchValue({
        address: filter.search,
        startDate: TimeUtil.getUiDateString(filter.startDate),
        endDate: TimeUtil.getUiDateString(filter.endDate)
      });
    });

    this.coordinateSubscription = this.mapHub.coordinates()
      .filter(coords => !coords.isDefined())
      .subscribe(coords => NotificationService.message('Osoitetta ei löytynyt', 4000));

    this.searchForm.valueChanges.subscribe(form => this.notifySearchUpdated(form));
    this.notifySearchUpdated(this.searchForm.value);

    this.matchingAddresses = this.addressControl.valueChanges
      .debounceTime(300)
      .filter(searchTerm => !!searchTerm && searchTerm.length >= 3)
      .switchMap(searchTerm => this.mapHub.addressSearch(searchTerm))
      .map(matching => matching.sort(ArrayUtil.naturalSort((address: PostalAddress) => address.uiStreetAddress)));
  }

  ngOnDestroy(): void {
    this.coordinateSubscription.unsubscribe();
  }

  public notifySearchUpdated(form: {address: string, startDate: string, endDate: string}): void {
    let filter = new SearchbarFilter(form.address, TimeUtil.getDateFromUi(form.startDate), TimeUtil.getDateFromUi(form.endDate));
    this.searchUpdated.emit(filter);
    this.mapHub.addSearchFilter(filter);
  }

  public addressSelected(streetAddress: string) {
    this.mapHub.addSearch(StringUtil.capitalize(streetAddress));
    this.searchForm.patchValue({address: streetAddress});
  }

  public showMore() {
    this.onShowAdvanced.emit(true);
  }
}
