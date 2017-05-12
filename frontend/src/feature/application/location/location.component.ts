import {Component, OnInit, OnDestroy, AfterViewInit} from '@angular/core';
import {Router} from '@angular/router';
import '../../../rxjs-extensions.ts';
import {Observable} from 'rxjs/Observable';
import {FormBuilder, FormGroup, Validators, FormControl} from '@angular/forms';
import {Application} from '../../../model/application/application';
import {MapUtil} from '../../../service/map/map.util';
import {SearchbarFilter} from '../../../service/searchbar-filter';
import {MapHub} from '../../../service/map/map-hub';
import {Some} from '../../../util/option';
import {ApplicationType} from '../../../model/application/type/application-type';
import {ApplicationSpecifier} from '../../../model/application/type/application-specifier';
import {ApplicationKind, drawingAllowedForKind} from '../../../model/application/type/application-kind';
import {ApplicationState} from '../../../service/application/application-state';
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
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Component({
  selector: 'type',
  viewProviders: [],
  template: require('./location.component.html'),
  styles: [
    require('./location.component.scss')
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
  typeSelected = false;
  districts: Observable<Array<CityDistrict>>;
  searchbarFilter$ = new BehaviorSubject<SearchbarFilter>(new SearchbarFilter());
  multipleLocations: boolean = false;

  constructor(
    private applicationState: ApplicationState,
    private locationState: LocationState,
    private mapService: MapUtil,
    private router: Router,
    private mapHub: MapHub,
    private fb: FormBuilder) {

    this.areaCtrl = fb.control(undefined);
    this.sectionsCtrl = fb.control([]);

    this.locationForm = fb.group({
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
      info: ['']
    });
  };

  ngOnInit() {
    this.application = this.applicationState.application;
    this.multipleLocations = this.application.type === ApplicationType[ApplicationType.AREA_RENTAL];

    if (this.application.id) {
      this.typeSelected = this.application.kind !== undefined;
      this.loadFixedLocationsForKind(ApplicationKind[this.application.kind]);
    };

    this.progressStep = ProgressStep.LOCATION;
    this.mapHub.shape().subscribe(shape => this.shapeAdded(shape));
    this.districts = this.mapHub.districts();

    this.initForm();
    this.mapHub.editedLocation().subscribe(loc => this.editLocation(loc));

    this.areaCtrl.valueChanges.subscribe(id => this.onAreaChange(id));
    this.sectionsCtrl.valueChanges
      .distinctUntilChanged(ArrayUtil.numberArrayEqual)
      .subscribe(ids => this.onSectionsChange(ids));
  }

  ngOnDestroy() {
  }

  ngAfterViewInit(): void {
    this.mapHub.selectApplication(this.application);
  }

  get searchbarFilter() {
    return this.searchbarFilter$.asObservable();
  }

  onApplicationTypeChange(type: ApplicationType) {
    this.application.type = ApplicationType[type];
    this.application.extension = this.createExtension(type);
    this.multipleLocations = type === ApplicationType.AREA_RENTAL;
  }

  onApplicationKindChange(kind: ApplicationKind) {
    this.application.kind = ApplicationKind[kind];
    this.typeSelected = kind !== undefined;

    if (kind !== undefined) {
      this.loadFixedLocationsForKind(kind);
    }
  }

  onApplicationSpecifierChange(specifiers: Array<ApplicationSpecifier>) {
    Some(this.application.extension).do(extension =>
      extension.specifiers = specifiers.map(s => ApplicationSpecifier[s]));
  }

  searchUpdated(filter: SearchbarFilter) {
    this.locationForm.patchValue({
      startTime: filter.uiStartDate,
      endTime: filter.uiEndDate,
      streetAddress: filter.search
    });
  }

  store(form: LocationForm): void {
    this.locationState.storeLocation(LocationForm.to(form));
    this.resetForm();
  }

  cancel(): void {
    this.locationState.cancelEditing();
    this.resetForm();
  }

  onSubmit(form: LocationForm) {
    this.locationState.storeLocation(LocationForm.to(form));
    this.application.locations = this.locationState.locationsSnapshot;
    this.application.updateDatesFromLocations();

    if (this.application.id) {
      this.applicationState.save(this.application)
        .subscribe(
          app => NotificationService.message(findTranslation('application.action.saved')),
          err => NotificationService.error(err));
    } else {
      this.applicationState.application = this.application;
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
    return this.mapHub.districtById(id).map(d => d.name);
  }

  get editingAllowed(): boolean {
    return drawingAllowedForKind(ApplicationKind[this.application.kind]) && this.sectionsCtrl.value.length === 0;
  }

  get submitAllowed(): boolean {
    // Nothing is currently edited or location is edited but its values are valid
    return this.locationState.editIndex === undefined || this.locationForm.valid;
  }

  private editLocation(loc: Location): void {
    if (!!loc) {
      this.locationForm.patchValue(LocationForm.from(loc));
      this.searchbarFilter$.next(this.createFilter(loc));
    }
  }

  private shapeAdded(shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) {
    if (shape.features.length) {
      this.locationForm.patchValue({geometry: this.mapService.featureCollectionToGeometryCollection(shape)});
    } else {
      this.locationForm.patchValue({geometry: undefined});
    }
  }

  private loadFixedLocationsForKind(kind: ApplicationKind): void {
    this.mapHub.fixedLocationAreas()
      .subscribe(fixedLocations => {
        this.areas = fixedLocations
          .filter(f => f.hasSectionsForKind(kind))
          .sort(ArrayUtil.naturalSort((area: FixedLocationArea) => area.name));

        this.areaSections = [];
        this.setInitialSelections();
      });
  }

  private setInitialSelections() {
    Some(this.application.firstLocation)
      .map(location => this.areas.filter(area => area.hasSectionIds(location.fixedLocationIds)))
      .filter(areas => areas.length > 0)
      .map(areas => areas[0].id)
      .do(id => this.locationForm.patchValue({area: id}));

    Some(this.application.firstLocation)
      .filter(location => location.fixedLocationIds.length > 0)
      .do(location => this.sectionsCtrl.patchValue({sections: location.fixedLocationIds}));
  }

  private onAreaChange(id: number): void {
    if (NumberUtil.isDefined(id)) {
      let area = this.areas.find(a => a.id === id);
      let kind = ApplicationKind[this.application.kind];

      this.areaSections = area.namedSectionsForKind(kind)
        .sort(ArrayUtil.naturalSort((s: FixedLocationSection) => s.name));

      area.singleDefaultSectionForKind(kind)
        .do(defaultSection => this.sectionsCtrl.patchValue([defaultSection.id]));
    } else {
      this.areaSections = [];
      this.locationForm.patchValue({sections: []});
    }
  }

  private onSectionsChange(ids: Array<number>) {
    this.mapHub.selectFixedLocationSections(ids);
  }

  private initForm(): void {
    this.locationState.initLocations(this.application.locations);

    if (this.application.firstLocation) {
      let formValues = LocationForm.from(this.application.firstLocation);
      this.locationForm.patchValue(formValues);
      this.districtName(formValues.cityDistrictId).subscribe(name => this.locationForm.patchValue({cityDistrictName: name}));
      this.searchbarFilter$.next(this.createFilter(this.application.firstLocation));
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

  private createFilter(location: Location): SearchbarFilter {
    return new SearchbarFilter(
      location.postalAddress.streetAddress,
      location.startTime,
      location.endTime
    );
  }

  private resetForm(): void {
    this.locationForm.reset(LocationForm.from(new Location()));
    this.searchbarFilter$.next(new SearchbarFilter());
  }
}
