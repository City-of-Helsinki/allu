import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

import {MapStore} from '@service/map/map-store';
import {PostalAddress} from '@model/common/postal-address';
import {Observable, Subject} from 'rxjs';
import {NotificationService} from '@feature/notification/notification.service';
import {ArrayUtil} from '@util/array-util';
import {StringUtil} from '@util/string.util';
import {ApplicationStatusGroup} from '@model/application/application-status';
import {MapSearchFilter} from '@service/map-search-filter';
import {EnumUtil} from '@util/enum.util';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {StoredFilter} from '@model/user/stored-filter';
import {debounceTime, filter, map, takeUntil} from 'rxjs/internal/operators';
import {select, Store} from '@ngrx/store';
import * as fromMap from '@feature/map/reducers';
import * as fromLocationMapLayers from '@feature/application/location/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {merge} from 'rxjs/internal/observable/merge';
import {FetchCoordinates, Search} from '@feature/map/actions/address-search-actions';


enum BarType {
  SIMPLE = 'SIMPLE', // Front page
  BAR = 'BAR', // Location
  ADVANCED = 'ADVANCED' // Front page
}

@Component({
  selector: 'searchbar',
  templateUrl: './searchbar.component.html',
  styleUrls: ['./searchbar.component.scss']
})
export class SearchbarComponent implements OnInit, OnDestroy {
  @Input() datesRequired = false;
  @Input() barType: BarType = BarType.BAR;
  @Input() targetType: ActionTargetType = ActionTargetType.Home;

  @Output() onShowAdvanced = new EventEmitter<boolean>();
  @Output() searchChange = new EventEmitter<MapSearchFilter>();

  searchForm: FormGroup;
  addressControl: FormControl;
  matchingAddresses$: Observable<PostalAddress[]>;
  statuses = EnumUtil.enumValues(ApplicationStatusGroup);
  MAP_FILTER = StoredFilterType.MAP;
  mapFilter: Observable<MapSearchFilter>;
  selectedFilter: Observable<StoredFilter>;
  defaultFilter: Observable<StoredFilter>;
  availableFilters: Observable<StoredFilter[]>;
  selectedLayers$: Observable<string[]>;
  availableLayers$: Observable<string[] | number[]>;

  private destroy = new Subject<boolean>();

  constructor(private fb: FormBuilder,
              private mapStore: MapStore,
              private storedFilterStore: StoredFilterStore,
              private notification: NotificationService,
              private store: Store<fromMap.State>) {
    this.addressControl = this.fb.control('');
    this.searchForm = this.fb.group({
      address: this.addressControl,
      startDate: this.datesRequired ? [undefined, Validators.required] : undefined,
      endDate: this.datesRequired ? [undefined, Validators.required] : undefined,
      statuses: [[]]
    });
  }

  ngOnInit(): void {
    this.addressControl.valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300),
      filter(term => !!term && term.length >= 3)
    ).subscribe(term => this.store.dispatch(new Search(term)));

    merge(
      this.searchForm.get('address').valueChanges,
      this.searchForm.get('statuses').valueChanges
    ).pipe(
      takeUntil(this.destroy)
    ).subscribe(() => this.onFormChange(this.searchForm.getRawValue()));

    this.matchingAddresses$ = this.store.select(fromMap.getMatchingAddressed);

    this.mapFilter = this.mapStore.mapSearchFilter.pipe(takeUntil(this.destroy));

    this.selectedFilter = this.storedFilterStore.getCurrent(StoredFilterType.MAP);
    this.availableFilters = this.storedFilterStore.getAvailable(StoredFilterType.MAP);
    this.defaultFilter = this.storedFilterStore.getDefault(StoredFilterType.MAP);
    this.availableLayers$ = this.store.pipe(select(this.getLayerIds()));
    this.selectedLayers$ = this.store.pipe(select(this.getSelectedLayerIds()));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onFormChange(value: MapSearchFilter): void {
    this.notifySearchUpdated(value);
  }

  @Input()
  set filter(searchFilter: MapSearchFilter) {
    this.searchForm.patchValue(searchFilter, {emitEvent: false});
  }

  public notifySearchUpdated(searchFilter: MapSearchFilter): void {
    this.searchChange.emit(searchFilter);
  }

  public addressSelected(streetAddress: string) {
    this.store.dispatch(new FetchCoordinates(StringUtil.capitalize(streetAddress)));
    // this.mapStore.coordinateSearchChange();
    this.searchForm.patchValue({address: streetAddress});
  }

  public showMore() {
    this.onShowAdvanced.emit(true);
  }

  public selectFilter(searchFilter: StoredFilter) {
    this.storedFilterStore.currentMapFilterChange(searchFilter);
  }

  private getLayerIds() {
    return this.targetType === ActionTargetType.Location
      ? fromLocationMapLayers.getLayerIds
      : fromMap.getLayerIds;
  }

  private getSelectedLayerIds() {
    return this.targetType === ActionTargetType.Location
      ? fromLocationMapLayers.getSelectedLayerIds
      : fromMap.getSelectedLayerIds;
  }
}
