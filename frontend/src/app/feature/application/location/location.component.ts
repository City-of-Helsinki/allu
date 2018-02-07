import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationStart, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
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
import {ProgressStep} from '../progressbar/progress-step';
import {ArrayUtil} from '../../../util/array-util';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {AreaRental} from '../../../model/application/area-rental/area-rental';
import {FixedLocationArea} from '../../../model/common/fixed-location-area';
import {FixedLocationSection} from '../../../model/common/fixed-location-section';
import {NumberUtil} from '../../../util/number.util';
import {LocationForm} from './location-form';
import {LocationState} from '../../../service/application/location-state';
import {Location} from '../../../model/common/location';
import {KindsWithSpecifiers} from '../../../model/application/type/application-specifier';
import {defaultFilter, MapSearchFilter} from '../../../service/map-search-filter';
import {CityDistrictService} from '../../../service/map/city-district.service';
import {FixedLocationService} from '../../../service/map/fixed-location.service';
import {Subject} from 'rxjs/Subject';

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

  areas = new Array<FixedLocationArea>();
  areaSections = new Array<FixedLocationSection>();
  editedItemCount = 0;
  application: Application;
  progressStep: ProgressStep;
  kindsSelected = false;
  districts: Observable<Array<CityDistrict>>;
  multipleLocations = false;

  private destroy = new Subject<boolean>();

  constructor(
    private applicationStore: ApplicationStore,
    private locationState: LocationState,
    private mapService: MapUtil,
    private router: Router,
    private mapStore: MapStore,
    private cityDistrictService: CityDistrictService,
    private fixedLocationService: FixedLocationService,
    private fb: FormBuilder) {

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
      info: ['']
    });
  }

  ngOnInit() {
    this.mapStore.searchFilterChange(defaultFilter);

    this.router.events
      .takeUntil(this.destroy)
      .filter(e => e instanceof NavigationStart)
      .subscribe(() => this.mapStore.searchFilterChange(defaultFilter));

    this.application = this.applicationStore.snapshot.application;
    this.multipleLocations = this.application.type === ApplicationType[ApplicationType.AREA_RENTAL];
    this.kindsSelected = this.application.kinds.length > 0;
    this.loadFixedLocations();

    this.progressStep = ProgressStep.LOCATION;
    this.mapStore.searchFilter
      .takeUntil(this.destroy)
      .subscribe(filter => this.searchUpdated(filter));

    this.mapStore.shape
      .takeUntil(this.destroy)
      .subscribe(shape => this.shapeAdded(shape));

    this.districts = this.cityDistrictService.get();

    this.initForm();

    this.mapStore.editedLocation
      .takeUntil(this.destroy)
      .subscribe(loc => this.editLocation(loc));

    this.areaCtrl.valueChanges
      .takeUntil(this.destroy)
      .subscribe(id => this.onAreaChange(id));

    this.sectionsCtrl.valueChanges
      .takeUntil(this.destroy)
      .distinctUntilChanged(ArrayUtil.numberArrayEqual)
      .subscribe(ids => this.onSectionsChange(ids));
  }

  ngOnDestroy() {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.mapStore.selectedApplicationChange(this.application);
  }

  onApplicationTypeChange(type: ApplicationType) {
    this.application.type = ApplicationType[type];
    this.application.extension = this.createExtension(type);
    this.multipleLocations = type === ApplicationType.AREA_RENTAL;
  }

  onKindSpecifierChange(kindsWithSpecifiers: KindsWithSpecifiers) {
    this.application.kindsWithSpecifiers = kindsWithSpecifiers;
    this.kindsSelected = this.application.kinds.length > 0;
    this.loadFixedLocations();
    this.notifyEditingAllowed();
    this.resetFixedLocations();
  }

  searchUpdated(filter: MapSearchFilter) {
    this.locationForm.patchValue({
      streetAddress: filter.address,
      startTime: filter.startDate,
      endTime: filter.endDate
    }, {emitEvent: false});
  }

  store(form: LocationForm): void {
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
    this.application.locations = this.locationState.locationsSnapshot;
    this.application.updateDatesFromLocations();

    if (this.application.id) {
      this.applicationStore.save(this.application)
        .subscribe(
          app => {
            NotificationService.message(findTranslation('application.action.saved'));
            this.router.navigate(['/applications', String(app.id), 'summary']);
          },
          err => NotificationService.error(err));
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
    return this.cityDistrictService.byId(id).map(d => d.name);
  }

  notifyEditingAllowed(): void {
    const isAllowedForKind = Some(this.application.kinds)
      .map(kinds => kinds.every(kind => drawingAllowedForKind(kind)))
      .orElse(true);
    this.mapStore.drawingAllowedChange(isAllowedForKind && (this.sectionsCtrl.value.length === 0));
  }

  get submitAllowed(): boolean {
    // Nothing is currently edited or location is edited but its values are valid
    return this.locationState.editIndex === undefined || this.locationForm.valid;
  }

  private editLocation(loc: Location): void {
    if (!!loc) {
      this.locationForm.patchValue(LocationForm.from(loc));
      this.mapStore.searchFilterChange(this.createFilter(loc));
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
    if (hasSingleKind(this.application.typeEnum)) {
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
      .filter(location => location.fixedLocationIds.length > 0)
      .do(location => this.sectionsCtrl.patchValue(location.fixedLocationIds));
  }

  private onAreaChange(id: number): void {
    if (NumberUtil.isDefined(id) && hasSingleKind(this.application.typeEnum)) {
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

    if (this.application.firstLocation) {
      const formValues = LocationForm.from(this.application.firstLocation);
      this.locationForm.patchValue(formValues);
      this.districtName(formValues.cityDistrictId).subscribe(name => this.locationForm.patchValue({cityDistrictName: name}));
      this.mapStore.searchFilterChange(this.createFilter(this.application.firstLocation));
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
    this.mapStore.searchFilterChange({address: undefined, startDate: undefined, endDate: undefined});
  }

  private sortedAreaSectionsFrom(area: FixedLocationArea): Array<FixedLocationSection> {
    if (hasSingleKind(this.application.typeEnum)) {
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
