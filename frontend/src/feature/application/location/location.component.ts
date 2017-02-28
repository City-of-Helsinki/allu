import {Component, OnInit, OnDestroy, AfterViewInit} from '@angular/core';
import {Router} from '@angular/router';
import '../../../rxjs-extensions.ts';
import {Observable} from 'rxjs/Observable';
import {FormBuilder} from '@angular/forms';

import {Application} from '../../../model/application/application';
import {MapUtil} from '../../../service/map/map.util';
import {SearchbarFilter} from '../../../service/searchbar-filter';
import {MapHub} from '../../../service/map/map-hub';
import {FixedLocation} from '../../../model/common/fixed-location';
import {Some, Option} from '../../../util/option';
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

@Component({
  selector: 'type',
  viewProviders: [],
  template: require('./location.component.html'),
  styles: [
    require('./location.component.scss')
  ]
})
export class LocationComponent implements OnInit, OnDestroy, AfterViewInit {
  areas = new Array<string>();
  sections = new Array<FixedLocation>();
  application: Application;
  progressStep: ProgressStep;
  typeSelected = false;
  selectedFixedLocations = [];
  editedItemCount = 0;
  districts: Observable<Array<CityDistrict>>;
  selectedDistrict: CityDistrict;

  private fixedLocations = new Array<FixedLocation>();
  private geometry: GeoJSON.GeometryCollection;

  constructor(
    private applicationState: ApplicationState,
    private mapService: MapUtil,
    private router: Router,
    private mapHub: MapHub,
    private fb: FormBuilder) {
  };

  ngOnInit() {
    this.application = this.applicationState.application;
    this.geometry = this.application.location.geometry;
    if (this.application.id) {
      this.typeSelected = this.application.kind !== undefined;
      this.loadFixedLocationsForKind(ApplicationKind[this.application.kind]);
    };

    this.progressStep = ProgressStep.LOCATION;
    this.mapHub.shape().subscribe(shape => this.shapeAdded(shape));
    this.districts = this.mapHub.districts();
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
    this.application.location.postalAddress.streetAddress = filter.search;
    this.application.startTime = filter.startDate;
    this.application.endTime = filter.endDate;
  }

  save() {
    this.application.location.fixedLocationIds = this.selectedFixedLocations;
    this.application.location.geometry = this.geometry;

    if (this.application.id) {
      this.applicationState.save(this.application)
        .subscribe(app => console.log('Application saved'));
    } else {
      this.applicationState.application = this.application;
      this.router.navigate(['/applications/edit']);
    }
  }

  set selectedArea(area: string) {
    this.sections = this.fixedLocations.filter(fl => fl.area === area).sort(FixedLocation.sortBySection);
    // When only 1 section it is area's fixed location which should be selected
    // Otherwise select none as they are sections
    this.selectedFixedLocations = this.sections.length === 1
      ? [this.sections[0].id]
      : [];
    if (!!area) {
      this.mapHub.selectFixedLocations(this.selectedFixedLocations);
    }
  }

  get selectedArea(): string {
    return this.firstSection.map(section => section.area).orElse(undefined);
  }

  get firstSection(): Option<FixedLocation> {
    return Some(this.sections)
      .filter(sections => sections.length > 0)
      .map(sections => sections[0]);
  }

  set selectedFxs(fixedLocations: Array<number>) {
    this.selectedFixedLocations = fixedLocations;
    this.mapHub.selectFixedLocations(fixedLocations);
  }

  get selectedFxs(): Array<number> {
    return this.selectedFixedLocations;
  }

  noSections(): boolean {
    return this.sections.every(section => !section.section);
  }

  editedItemCountChanged(editedItemCount: number) {
    this.editedItemCount = editedItemCount;
  }

  districtName(id: number): Observable<string> {
    return this.mapHub.districtById(id).map(d => d.name);
  }

  editingAllowed(): boolean {
    return drawingAllowedForKind(ApplicationKind[this.application.kind]) && this.selectedFixedLocations.length === 0;
  }

  private shapeAdded(shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) {
    if (shape.features.length) {
      this.geometry = this.mapService.featureCollectionToGeometryCollection(shape);
    } else {
      this.geometry = undefined;
    }
  }

  private loadFixedLocationsForKind(kind: ApplicationKind): void {
    this.mapHub.fixedLocations()
      .subscribe(fl => {
        this.areas = fl
          .filter(f => f.applicationKind === kind)
          .map(entry => entry.area)
          .filter((v, i, a) => a.indexOf(v) === i); // unique area names

        this.fixedLocations = fl.filter(f => f.applicationKind === kind);
        this.sections = [];

        this.setSelections();
      });
  }

  private setSelections() {
    this.selectedArea = Some(this.application.location)
      .map(location => this.fixedLocations.filter(fLoc => location.fixedLocationIds.indexOf(fLoc.id) >= 0))
      .filter(fLocs => fLocs.length > 0)
      .map(fLocs => fLocs[0].area)
      .orElse(undefined);

    Some(this.application.location).do(location => {
      this.selectedFixedLocations = location.fixedLocationIds;
    });
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
      default:
        throw new Error('Extension for ' + ApplicationType[type] + ' not implemented yet');
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
