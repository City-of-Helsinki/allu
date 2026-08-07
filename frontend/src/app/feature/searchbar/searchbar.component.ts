import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, ValidatorFn, Validators} from '@angular/forms';

import {MapStore} from '@service/map/map-store';
import {PostalAddress} from '@model/common/postal-address';
import {Observable, Subject} from 'rxjs';
import {NotificationService} from '@feature/notification/notification.service';
import {StringUtil} from '@util/string.util';
import {ApplicationStatusGroup} from '@model/application/application-status';
import {MapSearchFilter} from '@service/map-search-filter';
import {EnumUtil} from '@util/enum.util';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {StoredFilter} from '@model/user/stored-filter';
import {debounceTime, filter, map, takeUntil} from 'rxjs/operators';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromMap from '@feature/map/reducers';
import * as fromLocationMapLayers from '@feature/application/location/reducers';
import * as fromApplication from '@feature/application/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {FetchCoordinates, Search} from '@feature/map/actions/address-search-actions';
import {setValidatorsAndValidate} from '@feature/common/validation/validation-util';
import {DateFilter, defaultDateFilter} from '@util/date-filter';
import {TimePeriod} from '@feature/application/info/time-period';
import {ComplexValidator} from '@util/complex-validator';
import {TimeUtil} from '@util/time.util';
import {ApplicationKind} from '@model/application/type/application-kind';
import {FormUtil} from '@util/form.util';
import {TreeStructureNode} from '@feature/common/tree/tree-node';


enum BarType {
  SIMPLE = 'SIMPLE', // Front page
  BAR = 'BAR', // Location
  ADVANCED = 'ADVANCED' // Front page
}

interface SearchForm {
  address?: string;
  startDate?: Date;
  endDate?: Date;
  statuses?: ApplicationStatusGroup[];
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
  @Input() showAddress = true;

  @Output() onShowAdvanced = new EventEmitter<boolean>();
  @Output() addressChange = new EventEmitter<string>();
  @Output() searchChange = new EventEmitter<MapSearchFilter>();

  searchForm: UntypedFormGroup;
  addressControl: UntypedFormControl;
  matchingAddresses$: Observable<PostalAddress[]>;
  statuses = EnumUtil.enumValues(ApplicationStatusGroup);
  MAP_FILTER = StoredFilterType.MAP;
  maxEndDate$: Observable<Date>;
  minStartDate$: Observable<Date>;
  dateFilter: DateFilter = defaultDateFilter;
  mapFilter: Observable<MapSearchFilter>;
  selectedFilter: Observable<StoredFilter>;
  defaultFilter: Observable<StoredFilter>;
  availableFilters: Observable<StoredFilter[]>;
  selectedLayers$: Observable<string[]>;
  layerTree$: Observable<TreeStructureNode<void>>;
  kind$: Observable<ApplicationKind>;

  private _timePeriod: TimePeriod = new TimePeriod();
  private baseDateValidators: ValidatorFn[] = [];
  private destroy = new Subject<boolean>();

  constructor(private fb: UntypedFormBuilder,
              private mapStore: MapStore,
              private storedFilterStore: StoredFilterStore,
              private notification: NotificationService,
              private store: Store<fromRoot.State>) {
    this.baseDateValidators = [];
    this.addressControl = this.fb.control('');
    this.searchForm = this.fb.group({
      address: this.addressControl,
      startDate: undefined,
      endDate: undefined,
      statuses: [[]]
    });
  }

  ngOnInit(): void {
    if (this.datesRequired) {
      this.searchForm.get('startDate').setValidators(Validators.required);
      this.searchForm.get('endDate').setValidators(Validators.required);
      this.baseDateValidators = this.baseDateValidators.concat(Validators.required);
    }

    this.addressControl.valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300),
      filter(term => !!term && term.length >= 3)
    ).subscribe(term => this.store.dispatch(new Search(term)));


    this.searchForm.get('address').valueChanges.pipe(
      takeUntil(this.destroy)
    ).subscribe(address => this.addressChange.emit(address));

    this.searchForm.get('statuses').valueChanges.pipe(
      takeUntil(this.destroy)
    ).subscribe(() => this.onFormChange(this.searchForm.getRawValue()));

    this.matchingAddresses$ = this.store.select(fromMap.getMatchingAddressed);

    this.mapFilter = this.mapStore.mapSearchFilter.pipe(takeUntil(this.destroy));

    this.selectedFilter = this.storedFilterStore.getCurrent(StoredFilterType.MAP);
    this.availableFilters = this.storedFilterStore.getAvailable(StoredFilterType.MAP);
    this.defaultFilter = this.storedFilterStore.getDefault(StoredFilterType.MAP);
    this.selectedLayers$ = this.store.pipe(select(this.getSelectedLayerIds()));
    this.layerTree$ = this.store.pipe(select(this.getLayerTree()));

    this.maxEndDate$ = this.searchForm.get('startDate').valueChanges.pipe(
      map(start => TimeUtil.toTimePeriodEnd(start, this.timePeriod.endTime))
    );

    this.minStartDate$ = this.searchForm.get('endDate').valueChanges.pipe(
      map(end => TimeUtil.toTimePeriodStart(end, this.timePeriod.startTime))
    );

    this.kind$ = this.store.pipe(select(fromApplication.getKind));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onFormChange(value: SearchForm): void {
    this.notifySearchUpdated({
      startDate: value.startDate,
      endDate: value.endDate,
      statuses: value.statuses
    });
  }

  @Input()
  set address(address: string) {
    this.searchForm.patchValue({address: address}, {emitEvent: false});
  }

  @Input()
  set filter(searchFilter: MapSearchFilter) {
    this.searchForm.patchValue(searchFilter, {emitEvent: false});
  }

  @Input()
  set timePeriod(timePeriod: TimePeriod) {
    if (timePeriod) {
      const validators = this.baseDateValidators.concat(ComplexValidator.inTimePeriod(timePeriod.startTime, timePeriod.endTime));
      setValidatorsAndValidate(this.searchForm.get('startDate'), validators);
      setValidatorsAndValidate(this.searchForm.get('endDate'), validators);
      this.dateFilter = (date: Date) => TimeUtil.isInTimePeriod(date, timePeriod.startTime, timePeriod.endTime);
      this._timePeriod = timePeriod;
    } else {
      setValidatorsAndValidate(this.searchForm.get('startDate'), this.baseDateValidators);
      setValidatorsAndValidate(this.searchForm.get('endDate'), this.baseDateValidators);
      this.dateFilter = defaultDateFilter;
      this._timePeriod = new TimePeriod();
    }
  }

  get timePeriod() {
    return this._timePeriod;
  }

  get valid(): boolean {
    return this.searchForm.valid;
  }

  validate(): void {
    FormUtil.validateFormFields(this.searchForm);
  }

  public notifySearchUpdated(searchFilter: MapSearchFilter): void {
    this.searchChange.emit(searchFilter);
  }

  public addressSelected(streetAddress: string) {
    this.store.dispatch(new FetchCoordinates(StringUtil.capitalize(streetAddress)));
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

  private getLayerTree() {
    return this.targetType === ActionTargetType.Location
      ? fromLocationMapLayers.getTreeStructure
      : fromMap.getTreeStructure;
  }
}
