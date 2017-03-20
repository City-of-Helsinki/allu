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
  sections = new Array<FixedLocationSection>();
  editedItemCount = 0;
  application: Application;
  progressStep: ProgressStep;
  typeSelected = false;
  districts: Observable<Array<CityDistrict>>;

  private geometry: GeoJSON.GeometryCollection;

  constructor(
    private applicationState: ApplicationState,
    private mapService: MapUtil,
    private router: Router,
    private mapHub: MapHub,
    private fb: FormBuilder) {

    this.areaCtrl = fb.control(undefined);
    this.areaCtrl.valueChanges.subscribe(id => this.onAreaChange(id));
    this.sectionsCtrl = fb.control([]);
    this.sectionsCtrl.valueChanges.subscribe(ids => this.onSectionsChange(ids));

    this.locationForm = fb.group({
      startTime: [''],
      endTime: [''],
      streetAddress: [''],
      area: this.areaCtrl,
      sections: this.sectionsCtrl,
      info: [''],
      areaSize: [{value: undefined, disabled: true}],
      areaOverride: [undefined],
      cityDistrictName: [{value: undefined, disabled: true}],
      cityDistrictIdOverride: [undefined]
    });
  };

  ngOnInit() {
    this.application = this.applicationState.application;
    this.geometry = this.application.firstLocation.geometry;
    if (this.application.id) {
      this.typeSelected = this.application.kind !== undefined;
      this.loadFixedLocationsForKind(ApplicationKind[this.application.kind]);
    };

    this.progressStep = ProgressStep.LOCATION;
    this.mapHub.shape().subscribe(shape => this.shapeAdded(shape));
    this.districts = this.mapHub.districts();
    this.locationForm.patchValue(LocationForm.from(this.application.firstLocation));
    this.districtName(this.application.firstLocation.cityDistrictId)
      .subscribe(name => this.locationForm.patchValue({cityDistrictName: name}));
  }

  ngOnDestroy() {
  }

  ngAfterViewInit(): void {
    this.mapHub.selectApplication(this.application);
  }

  onApplicationTypeChange(type: ApplicationType) {
    this.application.type = ApplicationType[type];
    this.application.extension = this.createExtension(type);
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

  onSubmit(form: LocationForm) {
    this.application.uiStartTime = form.startTime;
    this.application.uiEndTime = form.endTime;
    let location = this.application.firstLocation;
    location.uiStartTime = form.startTime;
    location.uiEndTime = form.endTime;
    location.postalAddress.streetAddress = form.streetAddress;
    location.info = form.info;
    location.fixedLocationIds = form.sections;
    location.areaOverride = form.areaOverride;
    location.cityDistrictIdOverride = form.cityDistrictIdOverride;
    location.geometry = this.geometry;
    this.application.singleLocation = location;

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

  editingAllowed(): boolean {
    return drawingAllowedForKind(ApplicationKind[this.application.kind]) && this.sectionsCtrl.value.length === 0;
  }

  private shapeAdded(shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) {
    if (shape.features.length) {
      this.geometry = this.mapService.featureCollectionToGeometryCollection(shape);
    } else {
      this.geometry = undefined;
    }
  }

  private loadFixedLocationsForKind(kind: ApplicationKind): void {
    this.mapHub.fixedLocationAreas()
      .subscribe(fixedLocations => {
        this.areas = fixedLocations
          .filter(f => f.hasSectionsForKind(kind))
          .sort(ArrayUtil.naturalSort((area: FixedLocationArea) => area.name));

        this.sections = [];
        this.setInitialSelections();
      });
  }

  private setInitialSelections() {
    Some(this.application.firstLocation)
      .map(location => this.areas.filter(area => area.hasSectionIds(location.fixedLocationIds)))
      .filter(areas => areas.length > 0)
      .map(areas => areas[0].id)
      .do(id => this.locationForm.patchValue({area: id}));

    Some(this.application.firstLocation).do(location => {
      this.locationForm.patchValue({sections: location.fixedLocationIds});
    });
  }

  private onAreaChange(id: number): void {
    if (NumberUtil.isDefined(id)) {
      let area = this.areas.find(a => a.id === id);
      let kind = ApplicationKind[this.application.kind];

      this.sections = area.namedSectionsForKind(kind)
        .sort(ArrayUtil.naturalSort((s: FixedLocationSection) => s.name));

      area.singleDefaultSectionForKind(kind)
        .do(defaultSection => this.locationForm.patchValue({sections: [defaultSection.id]}));
    } else {
      this.sections = [];
      this.locationForm.patchValue({sections: []});
    }
  }

  private onSectionsChange(ids: Array<number>) {
    this.mapHub.selectFixedLocationSections(ids);
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
}
