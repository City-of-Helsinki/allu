import {Component, OnInit, OnDestroy, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import '../../../rxjs-extensions.ts';

import {ProgressStep} from '../../../feature/progressbar/progressbar.component';
import {Application} from '../../../model/application/application';
import {Location} from '../../../model/common/location';
import {MapUtil} from '../../../service/map.util.ts';
import {SearchbarFilter} from '../../../service/searchbar-filter';
import {LocationState} from '../../../service/application/location-state';
import {ApplicationHub} from '../../../service/application/application-hub';
import {MapHub} from '../../../service/map-hub';
import {FixedLocation} from '../../../model/common/fixed-location';
import {Some} from '../../../util/option';
import {ApplicationType} from '../../../model/application/type/application-type';
import {Option} from '../../../util/option';
import {ApplicationSpecifier} from '../../../model/application/type/application-specifier';
import {ApplicationKind} from '../../../model/application/type/application-kind';

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
  progressStep: number;
  typeSelected = false;
  selectedFixedLocations = [];
  editedItemCount = 0;

  private fixedLocations = new Array<FixedLocation>();
  private geometry: GeoJSON.GeometryCollection;

  constructor(
    private locationState: LocationState,
    private mapService: MapUtil,
    private router: Router,
    private route: ActivatedRoute,
    private applicationHub: ApplicationHub,
    private mapHub: MapHub) {
    this.application = new Application();
    this.locationState.location = new Location();
  };

  ngOnInit() {
    this.route.data
      .map((data: {application: Application}) => data.application)
      .filter(application => application.id !== undefined)
      .subscribe(application => {
        this.application = application;
        this.locationState.location = application.location || new Location();
        this.geometry = application.location.geometry;
        this.locationState.startDate = application.startTime;
        this.locationState.endDate = application.endTime;
        this.loadFixedLocationsForKind(ApplicationKind[application.kind]);
        this.locationState.specifiers = application.extension.specifiers.map(s => ApplicationSpecifier[s]);
      });

    this.route.queryParams
      .map((params: {relatedProject}) => params.relatedProject)
      .subscribe(relatedProject => this.locationState.relatedProject = relatedProject);

    this.progressStep = ProgressStep.LOCATION;
    this.mapHub.shape().subscribe(shape => this.shapeAdded(shape));
  }

  ngOnDestroy() {
  }

  ngAfterViewInit(): void {
    this.mapHub.selectApplication(this.application);
  }

  onApplicationTypeChange(type: ApplicationType) {
    this.locationState.applicationType = type;
  }

  onApplicationKindChange(kind: ApplicationKind) {
    this.locationState.applicationKind = kind;
    this.typeSelected = kind !== undefined;

    if (kind !== undefined) {
      this.loadFixedLocationsForKind(kind);
    }
  }

  onApplicationSpecifierChange(specifiers: Array<ApplicationSpecifier>) {
    this.locationState.specifiers = specifiers;
  }

  searchUpdated(filter: SearchbarFilter) {
    this.locationState.location.postalAddress.streetAddress = filter.search;
    this.locationState.startDate = filter.startDate;
    this.locationState.endDate = filter.endDate;
  }

  save() {
    this.locationState.location.fixedLocationIds = this.selectedFixedLocations;
    this.locationState.location.geometry = this.geometry;

    if (this.application.id) {
      // If there is an application to save the location data to
      this.application.location = this.locationState.location;
      this.application.startTime = this.locationState.startDate;
      this.application.endTime = this.locationState.endDate;
      this.application.extension.specifiers = this.locationState.specifiers.map(s => ApplicationSpecifier[s]);
      this.applicationHub.save(this.application).subscribe(application => {
        this.router.navigate(['/applications', application.id, 'summary']);
      });
    } else {
      this.router.navigate(['/applications/info']);
    }
  }

  set selectedArea(area: string) {
    this.sections = this.fixedLocations.filter(fl => fl.area === area).sort(FixedLocation.sortBySection);
    // When only 1 section it is area's fixed location which should be selected
    // Otherwise select none as they are sections
    this.selectedFixedLocations = this.sections.length === 1
      ? [this.sections[0].id]
      : [];
    this.mapHub.selectFixedLocations(this.selectedFixedLocations);
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
}
