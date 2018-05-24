import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

import {MapStore} from '../../service/map/map-store';
import {PostalAddress} from '../../model/common/postal-address';
import {Observable, Subject} from 'rxjs';
import {NotificationService} from '../../service/notification/notification.service';
import {ArrayUtil} from '../../util/array-util';
import {StringUtil} from '../../util/string.util';
import {ApplicationStatusGroup} from '../../model/application/application-status';
import {MapSearchFilter} from '../../service/map-search-filter';
import {EnumUtil} from '../../util/enum.util';
import {StoredFilterType} from '../../model/user/stored-filter-type';
import {StoredFilterStore} from '../../service/stored-filter/stored-filter-store';
import {StoredFilter} from '../../model/user/stored-filter';
import {TimeUtil} from '../../util/time.util';
import {debounceTime, filter, map, take, takeUntil} from 'rxjs/internal/operators';

enum BarType {
  SIMPLE, // Front page
  BAR, // Location
  ADVANCED // Front page
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
  MAP_FILTER = StoredFilterType.MAP;
  mapFilter: Observable<MapSearchFilter>;
  selectedFilter: Observable<StoredFilter>;
  defaultFilter: Observable<StoredFilter>;
  availableFilters: Observable<StoredFilter[]>;

  private destroy = new Subject<boolean>();

  constructor(private fb: FormBuilder,
              private mapStore: MapStore,
              private storedFilterStore: StoredFilterStore,
              private notification: NotificationService) {
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

    this.mapStore.coordinates.pipe(
      takeUntil(this.destroy),
      filter(coords => !coords.isDefined())
    ).subscribe(() => this.notification.success('Osoitetta ei löytynyt'));

    this.searchForm.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(form => this.notifySearchUpdated(form));

    this.addressControl.valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300),
      filter(searchTerm => !!searchTerm && searchTerm.length >= 3)
    ).subscribe(searchTerm => this.mapStore.addressSearchChange(searchTerm));

    this.matchingAddresses = this.mapStore.matchingAddresses.pipe(
      takeUntil(this.destroy),
      map(matching => matching.sort(ArrayUtil.naturalSort((address: PostalAddress) => address.uiStreetAddress)))
    );

    this.mapFilter = this.mapStore.mapSearchFilter.pipe(takeUntil(this.destroy));

    this.initSearch();

    this.selectedFilter = this.storedFilterStore.getCurrent(StoredFilterType.MAP);
    this.availableFilters = this.storedFilterStore.getAvailable(StoredFilterType.MAP);
    this.defaultFilter = this.storedFilterStore.getDefault(StoredFilterType.MAP);
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  public notifySearchUpdated(searchFilter: MapSearchFilter): void {
    this.adjustStartAndStopDates(searchFilter);
    if (this.useLocationSearch) {
      this.mapStore.locationSearchFilterChange(searchFilter);
    } else {
      this.storedFilterStore.resetCurrent(StoredFilterType.MAP);
      this.mapStore.mapSearchFilterChange(searchFilter);
    }
  }

  public addressSelected(streetAddress: string) {
    this.mapStore.coordinateSearchChange(StringUtil.capitalize(streetAddress));
    this.searchForm.patchValue({address: streetAddress});
  }

  public showMore() {
    this.onShowAdvanced.emit(true);
  }

  public selectFilter(searchFilter: StoredFilter) {
    this.storedFilterStore.currentChange(searchFilter);
  }

  private initSearch(): void {
    const searchFilter = this.useLocationSearch ? this.mapStore.locationSearchFilter : this.mapStore.mapSearchFilter;
    searchFilter.pipe(take(1))
      .subscribe(sf => this.searchForm.patchValue(sf, {emitEvent: false}));
  }

  private get useLocationSearch(): boolean {
    return BarType.BAR === BarType[this.barType];
  }

  private adjustStartAndStopDates(searchFilter: MapSearchFilter): void {
    if (searchFilter.startDate) {
      searchFilter.startDate = TimeUtil.toStartDate(searchFilter.startDate);
    }
    if (searchFilter.endDate) {
      searchFilter.endDate = TimeUtil.toEndDate(searchFilter.endDate);
    }
  }
}
