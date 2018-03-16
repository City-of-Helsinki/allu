import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

import {MapStore} from '../../service/map/map-store';
import {PostalAddress} from '../../model/common/postal-address';
import {Observable} from 'rxjs/Observable';
import {NotificationService} from '../../service/notification/notification.service';
import {ArrayUtil} from '../../util/array-util';
import {StringUtil} from '../../util/string.util';
import {ApplicationStatusGroup} from '../../model/application/application-status';
import {MapSearchFilter} from '../../service/map-search-filter';
import {Subject} from 'rxjs/Subject';
import {EnumUtil} from '../../util/enum.util';

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
  statuses = EnumUtil.enumValues(ApplicationStatusGroup);

  private destroy = new Subject<boolean>();

  constructor(private fb: FormBuilder, private mapStore: MapStore) {
    this.addressControl = this.fb.control('');
    this.searchForm = this.fb.group({
      address: this.addressControl,
      startDate: this.datesRequired ? [undefined, Validators.required] : undefined,
      endDate: this.datesRequired ? [undefined, Validators.required] : undefined,
      statuses: [[]]
    });
  }

  ngOnInit(): void {
    this.searchForm.patchValue(this.mapStore.snapshot.mapSearchFilter);

    this.mapStore.coordinates
      .takeUntil(this.destroy)
      .filter(coords => !coords.isDefined())
      .subscribe(() => NotificationService.message('Osoitetta ei löytynyt', 4000));

    this.searchForm.valueChanges
      .takeUntil(this.destroy)
      .subscribe(form => this.notifySearchUpdated(form));

    this.addressControl.valueChanges
      .takeUntil(this.destroy)
      .debounceTime(300)
      .filter(searchTerm => !!searchTerm && searchTerm.length >= 3)
      .subscribe(searchTerm => this.mapStore.addressSearchChange(searchTerm));

    this.matchingAddresses = this.mapStore.matchingAddresses
      .takeUntil(this.destroy)
      .map(matching => matching.sort(ArrayUtil.naturalSort((address: PostalAddress) => address.uiStreetAddress)));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  public notifySearchUpdated(filter: MapSearchFilter): void {
    this.mapStore.searchFilterChange(filter);
  }

  public addressSelected(streetAddress: string) {
    this.mapStore.coordinateSearchChange(StringUtil.capitalize(streetAddress));
    this.searchForm.patchValue({address: streetAddress});
  }

  public showMore() {
    this.onShowAdvanced.emit(true);
  }
}
