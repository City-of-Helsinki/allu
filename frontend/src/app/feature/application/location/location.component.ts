import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {NavigationStart, Router} from '@angular/router';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Application} from '@model/application/application';
import {MapUtil} from '@service/map/map.util';
import {MapStore} from '@service/map/map-store';
import {Some} from '@util/option';
import {ApplicationType, hasSingleKind} from '@model/application/type/application-type';
import {drawingAllowedForKind} from '@model/application/type/application-kind';
import {ApplicationStore} from '@service/application/application-store';
import {ApplicationExtension} from '@model/application/type/application-extension';
import {CableReport} from '@model/application/cable-report/cable-report';
import {Event} from '@model/application/event/event';
import {ShortTermRental} from '@model/application/short-term-rental/short-term-rental';
import {ExcavationAnnouncement} from '@model/application/excavation-announcement/excavation-announcement';
import {Note} from '@model/application/note/note';
import {CityDistrict} from '@model/common/city-district';
import {TrafficArrangement} from '@model/application/traffic-arrangement/traffic-arrangement';
import {PlacementContract} from '@model/application/placement-contract/placement-contract';
import {NotificationService} from '@feature/notification/notification.service';
import {findTranslation, findTranslationWithDefault} from '@util/translations';
import {AreaRental} from '@model/application/area-rental/area-rental';
import {NumberUtil} from '@util/number.util';
import {LocationForm} from './location-form';
import {LocationState} from '@service/application/location-state';
import {Location} from '@model/common/location';
import {defaultFilter, MapSearchFilter} from '@service/map-search-filter';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '../reducers';
import {select, Store} from '@ngrx/store';
import {filter, map, switchMap, take, takeUntil} from 'rxjs/internal/operators';
import {TimeUtil} from '@util/time.util';
import {KindsWithSpecifiers} from '@model/application/type/application-specifier';
import {MapController} from '@service/map/map-controller';
import {EMPTY} from 'rxjs/internal/observable/empty';
import * as fromLocation from '@feature/application/location/reducers';
import {MapLayer} from '@service/map/map-layer';
import {DistributionEntry} from '@model/common/distribution-entry';
import {DistributionType} from '@model/common/distribution-type';
import {DefaultRecipient} from '@model/common/default-recipient';
import {DefaultRecipientHub} from '@service/recipients/default-recipient-hub';
import {CurrentUser} from '@service/user/current-user';
import {TypeComponent} from '@feature/application/type/type.component';
import {SearchbarComponent} from '@feature/searchbar/searchbar.component';
import {ConfigurationHelperService} from '@service/config/configuration-helper.service';
import {TimePeriod} from '@feature/application/info/time-period';
import {getPaymentTariffs, needsPaymentTariff} from '@feature/common/payment-tariff';
import {FixedLocation, fixedLocationInfo, groupByArea} from '@model/common/fixed-location';
import {SearchByNameOrId} from '@feature/application/actions/application-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import * as UserAreaActions from '@feature/application/location/actions/user-area-actions';
import {Feature, GeometryCollection, GeometryObject} from 'geojson';
import {MapComponent} from '@feature/map/map.component';
import {FormUtil} from '@util/form.util';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {createTranslated} from '@service/error/error-info';
import {ObjectUtil} from '@util/object.util';
import {SaveDistribution} from '@feature/application/actions/application-actions';

@Component({
  selector: 'type',
  viewProviders: [],
  templateUrl: './location.component.html',
  styleUrls: [
    './location.component.scss'
  ]
})
export class LocationComponent implements OnInit, OnDestroy {
  locationForm: FormGroup;
  fixedLocationsCtrl: FormControl;
  fixedLocationInfos: string[];

  location: Location;
  fixedLocations: FixedLocation[] = [];
  editedItemCount = 0;
  application: Application;
  districts: Observable<Array<CityDistrict>>;
  multipleLocations = false;
  drawingAllowed = true;
  invalidGeometry = false;
  searchFilter$: Observable<MapSearchFilter>;
  selectedLayers$: Observable<MapLayer[]>;
  availableLayers$: Observable<MapLayer[]>;
  timePeriod$: Observable<TimePeriod>;
  address$: Observable<string>;
  matchingApplications$: Observable<Application[]>;
  userAreaCount$: Observable<number>;
  userAreas$: Observable<Feature<GeometryObject>[]>;
  userAreasLoading$: Observable<boolean>;

  private submitPending = false;
  private destroy = new Subject<boolean>();

  @ViewChild(TypeComponent, { static: true })
  private typeComponent: TypeComponent;
  @ViewChild(SearchbarComponent)
  private searchbarComponent: SearchbarComponent;
  @ViewChild(MapComponent)
  private mapComponent: MapComponent;

  constructor(
    private applicationStore: ApplicationStore,
    private locationState: LocationState,
    private mapService: MapUtil,
    private router: Router,
    private mapStore: MapStore,
    private mapController: MapController,
    private store: Store<fromRoot.State>,
    private fb: FormBuilder,
    private defaultRecipientHub: DefaultRecipientHub,
    private notification: NotificationService,
    private currentUser: CurrentUser,
    private configurationHelper: ConfigurationHelperService) {

    this.fixedLocationsCtrl = this.fb.control([]);

    this.locationForm = this.fb.group({
      id: [undefined],
      locationKey: [undefined],
      locationVersion: [undefined],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      geometry: [undefined],
      fixedLocations: this.fixedLocationsCtrl,
      areaSize: [{value: undefined, disabled: true}],
      areaOverride: [undefined],
      streetAddress: [''],
      postalCode: [undefined],
      city: [''],
      cityDistrictId: [undefined],
      cityDistrictName: [{value: undefined, disabled: true}],
      cityDistrictIdOverride: [undefined],
      underpass: [false],
      info: [''],
      paymentTariff: [{value: undefined, disabled: true}],
      paymentTariffOverride: [undefined]
    });
  }

  ngOnInit() {
    this.mapStore.locationSearchFilterChange(defaultFilter);

    this.router.events.pipe(
      takeUntil(this.destroy),
      filter(e => e instanceof NavigationStart)
    ).subscribe(() => this.mapStore.locationSearchFilterChange(defaultFilter));

    this.application = this.applicationStore.snapshot.application;
    this.multipleLocations = this.application.type === ApplicationType[ApplicationType.AREA_RENTAL];

    this.searchFilter$ = this.mapStore.locationSearchFilter;

    this.mapStore.shape.pipe(takeUntil(this.destroy))
      .subscribe(shape => this.shapeAdded(shape));

    this.districts = this.store.select(fromRoot.getAllCityDistricts);

    this.initForm();

    this.mapStore.editedLocation.pipe(takeUntil(this.destroy))
      .subscribe(loc => this.editLocation(loc));

    this.address$ = this.mapStore.editedLocation.pipe(
      takeUntil(this.destroy),
      filter(loc => !!loc),
      map(loc => loc.postalAddress.streetAddress)
    );

    this.mapStore.invalidGeometry.pipe(takeUntil(this.destroy))
      .subscribe(invalid => this.invalidGeometry = invalid);

    this.store.select(fromApplication.getType).pipe(takeUntil(this.destroy))
      .subscribe(type => this.onApplicationTypeChange(type));

    this.store.select(fromApplication.getKindsWithSpecifiers).pipe(takeUntil(this.destroy))
      .subscribe(kindsWithSpecifiers => this.onKindSpecifierChange(kindsWithSpecifiers));

    this.fixedLocationsCtrl.valueChanges.pipe(
      takeUntil(this.destroy),
    ).subscribe(ids => this.onFixedLocationChange(ids));

    this.searchFilter$.pipe(takeUntil(this.destroy)).subscribe(sf => this.searchUpdated(sf));

    this.loadFixedLocations().pipe(
      takeUntil(this.destroy)
    ).subscribe(fixedLocations => {
      this.fixedLocations = fixedLocations;
      this.setInitialSelections();
    });

    this.availableLayers$ = this.store.pipe(select(fromLocation.getAllLayers));
    this.selectedLayers$ = this.store.pipe(select(fromLocation.getSelectedLayers));

    this.locationForm.get('areaOverride').valueChanges.pipe(
      takeUntil(this.destroy),
      map(val => NumberUtil.isDefined(val) ? Math.floor(val) : null)
    ).subscribe(val => this.locationForm.patchValue({areaOverride: val}, {emitEvent: false}));

    this.timePeriod$ = this.store.pipe(
      select(fromApplication.getKind),
      switchMap(kind => this.configurationHelper.getTimePeriodForKind(kind))
    );

    this.matchingApplications$ = this.store.pipe(select(fromLocation.getMatchingApplications));

    this.userAreasLoading$ = this.store.pipe(select(fromLocation.getUserAreasLoading));
    this.userAreas$ = this.store.pipe(select(fromLocation.getAllUserAreas));
    this.userAreaCount$ = this.store.pipe(select(fromLocation.getUserAreaCount));

    this.store.dispatch(new UserAreaActions.Load());
  }

  ngOnDestroy() {
    this.destroy.next(true);
    this.destroy.unsubscribe();
    this.mapStore.reset();
  }

  get showMap() {
    return Some(this.application)
      .map(app => !!app.receivedTime && app.kinds.length > 0)
      .orElse(false);
  }

  get paymentTariffs(): string[] {
    return Some(this.application)
      .map(app => getPaymentTariffs(app.kinds))
      .orElse([]);
  }

  get showPaymentTariff(): boolean {
    if (this.application) {
      return needsPaymentTariff(this.application.type, this.application.kinds);
    } else {
      return false;
    }
  }

  get fixedLocationsSelected(): boolean {
    return Some(this.fixedLocationsCtrl.value)
      .map(fixedLocations => fixedLocations.length > 0)
      .orElse(false);
  }

  onApplicationTypeChange(type: ApplicationType) {
    if (type !== this.application.type) {
      this.application.type = ApplicationType[type];
      this.application.extension = this.createExtension(type);
      this.multipleLocations = type === ApplicationType.AREA_RENTAL;
      this.setInitialDates();
    }
  }

  onKindSpecifierChange(kindsWithSpecifiers: KindsWithSpecifiers) {
    this.application.kindsWithSpecifiers = kindsWithSpecifiers;
    this.loadFixedLocations().pipe(
      takeUntil(this.destroy)
    ).subscribe(fixedLocations => this.fixedLocations = fixedLocations);
    this.notifyEditingAllowed();
    this.resetFixedLocations();
  }

  applicationSearchChange(term: string): void {
    this.store.dispatch(new SearchByNameOrId(ActionTargetType.Location, term));
  }

  geometrySelected(geometry: GeometryCollection): void {
    this.mapComponent.addGeometry(geometry);
  }

  userAreasSelected(features: Feature<GeometryObject>[]) {
    this.mapComponent.addFeatures(features);
  }

  updateReceivedTime(date: Date): void {
    this.application.receivedTime = date;
  }

  onAddressChange(address: string) {
    this.locationForm.patchValue({streetAddress: address});
  }

  searchUpdated(searchFilter: MapSearchFilter) {
    this.locationForm.patchValue({
      startTime: searchFilter.startDate,
      endTime: searchFilter.endDate
    });
  }

  onSearchChange(searchFilter: MapSearchFilter): void {
    this.mapStore.locationSearchFilterChange(searchFilter);
  }

  storeLocation(): void {
    this.mapController.savePending();
    const form: LocationForm = this.locationForm.value;
    this.locationState.storeLocation(LocationForm.to(form));
    this.resetForm(form);
  }

  cancelArea(form: LocationForm): void {
    this.locationState.cancelEditing();
    this.resetForm({streetAddress: form.streetAddress});
    this.mapController.clearEdited();
  }

  cancelLink(): Array<string> {
    return Some(this.application.id)
      .map(id => ['/applications', String(id), 'summary'])
      .orElse(['/home']);
  }

  onSubmit() {
    if (this.submitAllowed) {
      const application = ObjectUtil.clone(this.application);
      this.submitPending = true;
      this.mapController.savePending();
      this.locationState.storeLocation(LocationForm.to(this.locationForm.value), false);
      const locations = this.locationState.locationsSnapshot;
      application.locations = locations;
      application.startTime = TimeUtil.minimum(... locations.map(l => l.startTime));
      application.endTime = TimeUtil.maximum(... locations.map(l => l.endTime));

      const isNew = this.applicationStore.isNew;
      const urlSuffix = isNew ? 'edit' : 'summary';

      this.applicationStore.save(application)
        .subscribe(
          app => {
            this.createInitialDistribution(app.type, isNew).pipe(
              take(1)
            ).subscribe(distribution => this.store.dispatch(new SaveDistribution(distribution)));
            this.destroy.next(true);
            this.notification.success(findTranslation('location.action.saved'));
            this.router.navigate(['/applications', app.id, urlSuffix]);
            this.submitPending = false;
          },
          err => {
            this.locationState.editLocation(0);
            this.notification.errorInfo(err);
            this.submitPending = false;
          });
    } else {
      FormUtil.validateFormFields(this.locationForm);
      this.typeComponent.validate();
      this.searchbarComponent.validate();
      this.store.dispatch(new NotifyFailure(createTranslated('common.field.faultyValueTitle', 'common.field.faultyValue')));
    }
  }

  editedItemCountChanged(editedItemCount: number) {
    this.editedItemCount = editedItemCount;
    if (editedItemCount > 0) {
      this.fixedLocationsCtrl.disable({emitEvent: false});
    } else {
      this.fixedLocationsCtrl.enable({emitEvent: false});
    }
  }

  districtName(id: number): Observable<string> {
    return this.store.select(fromRoot.getCityDistrictName(id));
  }

  notifyEditingAllowed(): void {
    const isAllowedForKind = Some(this.application.kinds)
      .map(kinds => kinds.every(kind => drawingAllowedForKind(kind)))
      .orElse(true);
    this.drawingAllowed = isAllowedForKind;
    this.mapStore.drawingAllowedChange(isAllowedForKind);
  }

  get submitAllowed(): boolean {
    // Nothing is currently edited or location is edited but its values are valid
    const nothingEdited = this.locationState.editIndex === undefined;
    const formValid = (this.locationForm.valid && !!this.locationForm.value['geometry']);
    const validGeometry = !this.invalidGeometry;
    const typeFormValid = this.typeComponent.valid;
    // Component might be hidden if required values have not been selected
    const searchFormValid = this.searchbarComponent ? this.searchbarComponent.valid : true;
    const validData = formValid && validGeometry && typeFormValid && searchFormValid;

    return !this.submitPending && (nothingEdited || validData);
  }

  paymentTariff() {
    const paymentTariff = this.locationForm.getRawValue().paymentTariff;
    if (paymentTariff === 'undefined') {
      return findTranslation('location.paymentTariffUndefined');
    } else {
      return findTranslationWithDefault('location.paymentTariffValue', 'tariff', paymentTariff);
    }
  }

  private editLocation(loc: Location): void {
    if (!!loc) {
      this.locationForm.patchValue(LocationForm.from(loc));
      this.mapStore.locationSearchFilterChange(this.createFilter(loc));
      this.location = loc;
    } else {
      this.location = undefined;
      this.resetForm();
    }
  }

  private shapeAdded(shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) {
    if (shape.features.length) {
      this.locationForm.patchValue({geometry: this.mapService.featureCollectionToGeometryCollection(shape)});
    } else {
      this.locationForm.patchValue({geometry: undefined});
    }
  }

  private loadFixedLocations(): Observable<FixedLocation[]> {
    if (hasSingleKind(this.application.type)) {
      return this.store.pipe(
        select(fromRoot.getFixedLocationsByKind(this.application.kind)),
        takeUntil(this.destroy),
        map(fixedLocations => fixedLocations.filter(fl => fl.active))
      );
    } else {
      return EMPTY;
    }
  }

  private setInitialSelections() {
    Some(this.application.firstLocation)
      .map(location => location.fixedLocationIds)
      .filter(ids => ids.length > 0)
      .do(ids => {
        this.fixedLocationsCtrl.patchValue(ids);
      });
  }

  private setInitialDates() {
    if (!this.application.firstLocation) {
      if (this.application.type === ApplicationType[ApplicationType.PLACEMENT_CONTRACT]) {
        const startDate = new Date();
        const endDate = new Date();
        endDate.setFullYear(endDate.getFullYear() + 1);
        this.mapStore.locationSearchFilterChange({startDate: startDate, endDate: endDate});
      } else {
        this.mapStore.locationSearchFilterChange({startDate: undefined, endDate: undefined});
      }
    }
  }

  private onFixedLocationChange(ids: number[]) {
    ids = ids || [];
    this.mapStore.selectedFixedLocationsChange(ids);
    const selected = this.fixedLocations.filter(fl => ids.indexOf(fl.id) >= 0);
    const grouped = groupByArea(selected);
    this.fixedLocationInfos = Object.keys(grouped).map(key => fixedLocationInfo(key, grouped[key]));
  }

  private initForm(): void {
    this.locationState.initLocations(this.application.locations);
    const location = this.application.firstLocation || new Location();
    if (!this.multipleLocations) {
      this.location = location;
      const formValues = LocationForm.from(this.location);
      this.locationForm.patchValue(formValues);
      this.districtName(formValues.cityDistrictId).subscribe(name => this.locationForm.patchValue({cityDistrictName: name}));
      this.mapStore.locationSearchFilterChange(this.createFilter(this.location));
      this.locationState.editLocation(0);
    }
  }

  private createEmptyExtension(type: ApplicationType): ApplicationExtension {
    switch (type) {
      case ApplicationType.CABLE_REPORT:
        return new CableReport();
      case ApplicationType.EVENT:
        return new Event();
      case ApplicationType.SHORT_TERM_RENTAL:
        return new ShortTermRental();
      case ApplicationType.EXCAVATION_ANNOUNCEMENT:
        return new ExcavationAnnouncement();
      case ApplicationType.NOTE:
        return new Note();
      case ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS:
        return new TrafficArrangement();
      case ApplicationType.PLACEMENT_CONTRACT:
        return new PlacementContract();
      case ApplicationType.AREA_RENTAL:
        return new AreaRental();
      default:
        throw new Error('Extension for type' + type + ' not implemented yet');
    }
  }

  private createExtension(applicationType: ApplicationType): ApplicationExtension {
    return Some(applicationType)
      .map(type => this.createEmptyExtension(type))
      .map(ext => {
        ext.applicationType = ApplicationType[applicationType];
        return ext;
      }).orElse(undefined);
  }

  private createFilter(location: Location): MapSearchFilter {
    return {
      startDate: location.startTime,
      endDate: location.endTime
    };
  }

  private resetForm(form: LocationForm = new LocationForm()): void {
    const location = LocationForm.to(form);
    this.locationForm.reset({streetAddress: form.streetAddress, startTime: form.startTime, endTime: form.endTime, sections: []});
    this.mapStore.locationSearchFilterChange({startDate: location.startTime, endDate: location.endTime});
  }

  private resetFixedLocations(): void {
    this.fixedLocationsCtrl.reset([]);
  }

  private createInitialDistribution(type: ApplicationType, isNew: boolean): Observable<DistributionEntry[]> {
    if (isNew) {
      return combineLatest([
        this.getDefaultRecipients(type),
        this.getCurrentUserDistribution(type)
      ]).pipe(
        take(1),
        map(([defaultRecipients, currentUser]) => defaultRecipients.concat(currentUser))
      );
    } else {
      return EMPTY;
    }

  }

  private getDefaultRecipients(type: ApplicationType): Observable<DistributionEntry[]> {
    return this.defaultRecipientHub.defaultRecipientsByApplicationType(type).pipe(
      map(recipients => recipients.map(r => this.toDistributionEntry(r)))
    );
  }

  private getCurrentUserDistribution(type: ApplicationType): Observable<DistributionEntry[]> {
    if ((type === ApplicationType.EVENT || type === ApplicationType.SHORT_TERM_RENTAL)) {
      return this.currentUser.user.pipe(
        map(user => [new DistributionEntry(null, user.realName, DistributionType.EMAIL, user.emailAddress)])
      );
    } else {
      return of([]);
    }
  }

  private toDistributionEntry(recipient: DefaultRecipient): DistributionEntry {
    const de = new DistributionEntry();
    de.name = recipient.email;
    de.email = recipient.email;
    de.distributionType = DistributionType.EMAIL;
    return de;
  }
}
