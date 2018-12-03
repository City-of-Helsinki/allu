import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationStart, Router} from '@angular/router';
import {Observable, Subject} from 'rxjs';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Application} from '../../../model/application/application';
import {MapUtil} from '../../../service/map/map.util';
import {MapStore} from '../../../service/map/map-store';
import {Some} from '../../../util/option';
import {ApplicationType, hasSingleKind} from '../../../model/application/type/application-type';
import {drawingAllowedForKind} from '../../../model/application/type/application-kind';
import {ApplicationStore} from '../../../service/application/application-store';
import {ApplicationExtension} from '../../../model/application/type/application-extension';
import {CableReport} from '../../../model/application/cable-report/cable-report';
import {Event} from '../../../model/application/event/event';
import {ShortTermRental} from '../../../model/application/short-term-rental/short-term-rental';
import {ExcavationAnnouncement} from '../../../model/application/excavation-announcement/excavation-announcement';
import {Note} from '../../../model/application/note/note';
import {CityDistrict} from '../../../model/common/city-district';
import {TrafficArrangement} from '../../../model/application/traffic-arrangement/traffic-arrangement';
import {PlacementContract} from '../../../model/application/placement-contract/placement-contract';
import {ArrayUtil} from '../../../util/array-util';
import {NotificationService} from '../../notification/notification.service';
import {findTranslation, findTranslationWithDefault} from '../../../util/translations';
import {AreaRental} from '../../../model/application/area-rental/area-rental';
import {FixedLocationArea} from '../../../model/common/fixed-location-area';
import {FixedLocationSection} from '../../../model/common/fixed-location-section';
import {NumberUtil} from '../../../util/number.util';
import {LocationForm} from './location-form';
import {LocationState} from '../../../service/application/location-state';
import {Location} from '../../../model/common/location';
import {defaultFilter, MapSearchFilter} from '../../../service/map-search-filter';
import {FixedLocationService} from '../../../service/map/fixed-location.service';
import * as fromRoot from '../../allu/reducers';
import * as fromApplication from '../reducers';
import {Store} from '@ngrx/store';
import {distinctUntilChanged, filter, take, takeUntil} from 'rxjs/internal/operators';
import {TimeUtil} from '../../../util/time.util';
import {KindsWithSpecifiers} from '../../../model/application/type/application-specifier';

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
  areaCtrl: FormControl;
  sectionsCtrl: FormControl;

  location: Location;
  areas = new Array<FixedLocationArea>();
  areaSections = new Array<FixedLocationSection>();
  editedItemCount = 0;
  application: Application;
  kindsSelected = false;
  districts: Observable<Array<CityDistrict>>;
  multipleLocations = false;
  invalidGeometry = false;
  showPaymentTariff = false;
  paymentTariffs = ['1', '2', '3', '4a', '4b'];
  searchFilter$: Observable<MapSearchFilter>;

  private destroy = new Subject<boolean>();

  constructor(
    private applicationStore: ApplicationStore,
    private locationState: LocationState,
    private mapService: MapUtil,
    private router: Router,
    private mapStore: MapStore,
    private store: Store<fromRoot.State>,
    private fixedLocationService: FixedLocationService,
    private fb: FormBuilder,
    private notification: NotificationService) {

    this.areaCtrl = this.fb.control(undefined);
    this.sectionsCtrl = this.fb.control([]);

    this.locationForm = this.fb.group({
      id: [undefined],
      locationKey: [undefined],
      locationVersion: [undefined],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      geometry: [undefined],
      area: this.areaCtrl,
      sections: this.sectionsCtrl,
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
    this.kindsSelected = this.application.kinds.length > 0;
    this.showPaymentTariff = [ApplicationType.EXCAVATION_ANNOUNCEMENT, ApplicationType.AREA_RENTAL]
      .indexOf(this.application.type) >= 0;
    this.loadFixedLocations();

    this.searchFilter$ = this.mapStore.locationSearchFilter;

    this.mapStore.shape.pipe(takeUntil(this.destroy))
      .subscribe(shape => this.shapeAdded(shape));

    this.districts = this.store.select(fromRoot.getAllCityDistricts);

    this.initForm();

    this.mapStore.editedLocation.pipe(takeUntil(this.destroy))
      .subscribe(loc => this.editLocation(loc));

    this.areaCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(id => this.onAreaChange(id));

    this.sectionsCtrl.valueChanges.pipe(
      takeUntil(this.destroy),
      distinctUntilChanged(ArrayUtil.numberArrayEqual)
    ).subscribe(ids => this.onSectionsChange(ids));

    this.mapStore.invalidGeometry.pipe(takeUntil(this.destroy))
      .subscribe(invalid => this.invalidGeometry = invalid);

    this.store.select(fromApplication.getType).pipe(takeUntil(this.destroy))
      .subscribe(type => this.onApplicationTypeChange(type));

    this.store.select(fromApplication.getKindsWithSpecifiers).pipe(takeUntil(this.destroy))
      .subscribe(kindsWithSpecifiers => this.onKindSpecifierChange(kindsWithSpecifiers));

    this.searchFilter$.pipe(takeUntil(this.destroy)).subscribe(sf => this.searchUpdated(sf));
  }

  ngOnDestroy() {
    this.destroy.next(true);
    this.destroy.unsubscribe();
    this.mapStore.reset();
  }

  ngAfterViewInit(): void {
    this.mapStore.selectedApplicationChange(this.application);
  }

  onApplicationTypeChange(type: ApplicationType) {
    if (type !== this.application.type) {
      this.application.type = ApplicationType[type];
      this.application.extension = this.createExtension(type);
      this.multipleLocations = type === ApplicationType.AREA_RENTAL;
      this.setInitialDates();
      this.showPaymentTariff = [ApplicationType.EXCAVATION_ANNOUNCEMENT, ApplicationType.AREA_RENTAL].indexOf(type) >= 0;
    }
  }

  onKindSpecifierChange(kindsWithSpecifiers: KindsWithSpecifiers) {
    this.application.kindsWithSpecifiers = kindsWithSpecifiers;
    this.kindsSelected = this.application.kinds.length > 0;
    this.loadFixedLocations();
    this.notifyEditingAllowed();
    this.resetFixedLocations();
  }

  updateReceivedTime(date: Date): void {
    this.application.receivedTime = date;
  }

  searchUpdated(searchFilter: MapSearchFilter) {
    this.locationForm.patchValue({
      streetAddress: searchFilter.address,
      startTime: searchFilter.startDate,
      endTime: searchFilter.endDate
    }, {emitEvent: false});
  }

  onSearchChange(searchFilter: MapSearchFilter): void {
    this.mapStore.locationSearchFilterChange(searchFilter);
  }

  storeLocation(form: LocationForm): void {
    this.locationState.storeLocation(LocationForm.to(form));
    this.resetForm();
  }

  cancelArea(): void {
    this.locationState.cancelEditing();
    this.resetForm();
  }

  cancelLink(): Array<string> {
    return Some(this.application.id)
      .map(id => ['/applications', String(id), 'summary'])
      .orElse(['/home']);
  }

  onSubmit(form: LocationForm) {
    this.locationState.storeLocation(LocationForm.to(form));
    const locations = this.locationState.locationsSnapshot;
    this.application.locations = locations;
    this.application.startTime = TimeUtil.minimum(... locations.map(l => l.startTime));
    this.application.endTime = TimeUtil.maximum(... locations.map(l => l.endTime));

    if (this.application.id) {
      this.applicationStore.save(this.application)
        .subscribe(
          app => {
            this.notification.success(findTranslation('application.action.saved'));
            this.router.navigate(['/applications', String(app.id), 'summary']);
          },
          err => this.notification.errorInfo(err));
    } else {
      this.applicationStore.applicationChange(this.application);
      this.router.navigate(['/applications/edit']);
    }
  }

  editedItemCountChanged(editedItemCount: number) {
    this.editedItemCount = editedItemCount;
    if (editedItemCount > 0) {
      this.areaCtrl.disable({emitEvent: false});
    } else {
      this.areaCtrl.enable({emitEvent: false});
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

    return nothingEdited || (formValid && validGeometry);
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
    }
  }

  private shapeAdded(shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) {
    if (shape.features.length) {
      this.locationForm.patchValue({geometry: this.mapService.featureCollectionToGeometryCollection(shape)});
    } else {
      this.locationForm.patchValue({geometry: undefined});
    }
  }

  private loadFixedLocations(): void {
    if (hasSingleKind(this.application.type)) {
      this.fixedLocationService.existing
        .subscribe(fixedLocations => {
          this.areas = fixedLocations
            .filter(f => f.hasSectionsForKind(this.application.kind))
            .sort(ArrayUtil.naturalSort((area: FixedLocationArea) => area.name));

          this.areaSections = [];
          this.setInitialSelections();
        });
    }
  }

  private setInitialSelections() {
    Some(this.application.firstLocation)
      .map(location => this.areas.filter(area => area.hasSectionIds(location.fixedLocationIds)))
      .filter(areas => areas.length > 0)
      .map(areas => areas[0])
      .do(area => {
        this.locationForm.patchValue({area: area.id});
        this.areaSections = this.sortedAreaSectionsFrom(area);
      });

    Some(this.application.firstLocation)
      .map(location => location.fixedLocationIds)
      .filter(ids => ids.length > 0)
      .do(ids => {
        this.sectionsCtrl.patchValue(ids);
        this.mapStore.selectedSectionsChange(ids);
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

  private onAreaChange(id: number): void {
    if (NumberUtil.isDefined(id) && hasSingleKind(this.application.type)) {
      const area = this.areas.find(a => a.id === id);
      const kind = this.application.kind;

      this.areaSections = this.sortedAreaSectionsFrom(area);

      area.singleDefaultSectionForKind(kind)
        .do(defaultSection => this.sectionsCtrl.patchValue([defaultSection.id]));
    } else {
      this.areaSections = [];
      this.locationForm.patchValue({sections: []});
    }
  }

  private onSectionsChange(ids: Array<number>) {
    this.mapStore.selectedSectionsChange(ids);
  }

  private initForm(): void {
    this.locationState.initLocations(this.application.locations);
    this.location = this.application.firstLocation;

    if (this.location) {
      const formValues = LocationForm.from(this.location);
      this.locationForm.patchValue(formValues);
      this.districtName(formValues.cityDistrictId).subscribe(name => this.locationForm.patchValue({cityDistrictName: name}));
      this.mapStore.locationSearchFilterChange(this.createFilter(this.location));
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
      address: location.postalAddress.streetAddress,
      startDate: location.startTime,
      endDate: location.endTime
    };
  }

  private resetForm(): void {
    this.locationForm.reset(LocationForm.from(new Location()));
    this.mapStore.locationSearchFilterChange({address: undefined, startDate: undefined, endDate: undefined});
  }

  private sortedAreaSectionsFrom(area: FixedLocationArea): Array<FixedLocationSection> {
    if (hasSingleKind(this.application.type)) {
      const kind = this.application.kind;
      return area.namedSectionsForKind(kind).sort(ArrayUtil.naturalSort((s: FixedLocationSection) => s.name));
    } else {
      return [];
    }
  }

  private resetFixedLocations(): void {
    if (this.areaCtrl.value) {
      this.areaCtrl.reset(undefined);
      this.sectionsCtrl.reset([]);
    }
  }
}
