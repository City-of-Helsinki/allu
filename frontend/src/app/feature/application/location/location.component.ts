import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {NavigationStart, Router} from '@angular/router';
import {Observable, Subject} from 'rxjs';
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
import {filter, map, switchMap, takeUntil, takeWhile} from 'rxjs/internal/operators';
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

@Component({
  selector: 'type',
  viewProviders: [],
  templateUrl: './location.component.html',
  styleUrls: [
    './location.component.scss'
  ]
})
export class LocationComponent implements OnInit, OnDestroy, AfterViewInit {
  locationForm: FormGroup;
  fixedLocationsCtrl: FormControl;
  fixedLocationInfos: string[];

  location: Location;
  fixedLocations: FixedLocation[] = [];
  editedItemCount = 0;
  application: Application;
  districts: Observable<Array<CityDistrict>>;
  multipleLocations = false;
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

  @ViewChild(TypeComponent)
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

  ngAfterViewInit(): void {
    this.mapStore.selectedApplicationChange(this.application);
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

  storeLocation(form: LocationForm): void {
    this.locationState.storeLocation(LocationForm.to(form));
    this.resetForm(form);
    this.mapController.clearEdited();
  }

  cancelArea(): void {
    this.locationState.cancelEditing();
    this.resetForm();
    this.mapController.clearEdited();
  }

  cancelLink(): Array<string> {
    return Some(this.application.id)
      .map(id => ['/applications', String(id), 'summary'])
      .orElse(['/home']);
  }

  onSubmit(form: LocationForm) {
    this.submitPending = true;
    this.locationState.storeLocation(LocationForm.to(form));
    const locations = this.locationState.locationsSnapshot;
    this.application.locations = locations;
    this.application.startTime = TimeUtil.minimum(... locations.map(l => l.startTime));
    this.application.endTime = TimeUtil.maximum(... locations.map(l => l.endTime));
    this.applicationStore.applicationChange(this.application);

    const updated = this.applicationStore.snapshot.application;

    this.initDistribution(updated);
    this.addCurrentUserToDistribution(updated);

    const urlSuffix = this.applicationStore.isNew ? 'edit' : 'summary';

    this.applicationStore.save(updated)
      .subscribe(
        app => {
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

  private initDistribution(application: Application): void {
    this.defaultRecipientHub.defaultRecipientsByApplicationType(application.type).pipe(
      takeWhile(() => this.applicationStore.isNew), // Only add default attachments if it is a new application
      takeUntil(this.destroy),
      map(recipients => recipients.map(r => this.toDistributionEntry(r)))
    ).subscribe(distributionEntries => {
      application.decisionDistributionList.push(...distributionEntries);
      this.applicationStore.applicationChange(application);
    }, err => this.notification.error(findTranslation('attachment.error.defaultAttachmentByArea')));
  }

  private addCurrentUserToDistribution(application: Application): void {
    const existingApplication = NumberUtil.isDefined(application.id);
    if (!existingApplication && (application.type === ApplicationType.EVENT
      || application.type === ApplicationType.SHORT_TERM_RENTAL)) {
      this.currentUser.user.subscribe(user => {
        const entry = new DistributionEntry(null, user.realName, DistributionType.EMAIL, user.emailAddress);
        application.decisionDistributionList.push(entry);
        this.applicationStore.applicationChange(application);
      });
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
