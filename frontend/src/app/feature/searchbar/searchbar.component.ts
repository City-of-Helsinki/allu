import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';

import {MapHub} from '../../service/map/map-hub';
import {PostalAddress} from '../../model/common/postal-address';
import {Observable} from 'rxjs/Observable';
import {NotificationService} from '../../service/notification/notification.service';
import {ArrayUtil} from '../../util/array-util';
import {StringUtil} from '../../util/string.util';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationStatus} from '../../model/application/application-status';
import {MapSearchFilter} from '../../service/map-search-filter';

enum BarType {
  SIMPLE,
  BAR,
  ADVANCED
}

@Component({
  selector: 'searchbar',
  templateUrl: './searchbar.component.html',
  styleUrls: ['./searchbar.component.scss']
})
export class SearchbarComponent implements OnInit, OnDestroy {
  @Input() datesRequired = false;
  @Input() barType: string = BarType[BarType.BAR];

  @Output() onShowAdvanced = new EventEmitter<boolean>();

  searchForm: FormGroup;
  addressControl: FormControl;
  matchingAddresses: Observable<Array<PostalAddress>>;
  statusTypes = EnumUtil.enumValues(ApplicationStatus);

  private coordinateSubscription: Subscription;
  private searchFilterSubscription: Subscription;

  constructor(private fb: FormBuilder, private mapHub: MapHub) {
    this.addressControl = this.fb.control('');
    this.searchForm = this.fb.group({
      address: this.addressControl,
      startDate: this.datesRequired ? [undefined, Validators.required] : undefined,
      endDate: this.datesRequired ? [undefined, Validators.required] : undefined,
      statusTypes: [[]]
    });
  }

  ngOnInit(): void {
    this.searchFilterSubscription = this.mapHub.searchFilter()
      .subscribe(filter => this.searchForm.patchValue(filter, {emitEvent: false}));

    this.coordinateSubscription = this.mapHub.coordinates()
      .filter(coords => !coords.isDefined())
      .subscribe(coords => NotificationService.message('Osoitetta ei löytynyt', 4000));

    this.searchForm.valueChanges.subscribe(form => this.notifySearchUpdated(form));

    this.matchingAddresses = this.addressControl.valueChanges
      .debounceTime(300)
      .filter(searchTerm => !!searchTerm && searchTerm.length >= 3)
      .switchMap(searchTerm => this.mapHub.findMatchingAddresses(searchTerm))
      .map(matching => matching.sort(ArrayUtil.naturalSort((address: PostalAddress) => address.uiStreetAddress)));
  }

  ngOnDestroy(): void {
    this.coordinateSubscription.unsubscribe();
    this.searchFilterSubscription.unsubscribe();
  }

  public notifySearchUpdated(filter: MapSearchFilter): void {
    this.mapHub.addSearchFilter(filter);
  }

  public addressSelected(streetAddress: string) {
    this.mapHub.coordinateSearch(StringUtil.capitalize(streetAddress));
    this.searchForm.patchValue({address: streetAddress});
  }

  public showMore() {
    this.onShowAdvanced.emit(true);
  }
}
