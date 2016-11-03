import {Component} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import 'proj4leaflet';
import 'leaflet';

import '../../rxjs-extensions.ts';
import {ProgressStep} from '../../feature/progressbar/progressbar.component';
import {Application} from '../../model/application/application';
import {Location} from '../../model/common/location';
import {PostalAddress} from '../../model/common/postal-address';
import {MapUtil} from '../../service/map.util.ts';
import {SearchbarFilter} from '../../service/searchbar-filter';
import {LocationState} from '../../service/application/location-state';
import {ApplicationHub} from '../../service/application/application-hub';
import {MapHub} from '../../service/map-hub';
import {FixedLocation} from '../../model/common/fixed-location';
import {Some} from '../../util/option';

@Component({
  selector: 'type',
  viewProviders: [],
  template: require('./location.component.html'),
  styles: [
    require('./location.component.scss')
  ]
})
export class LocationComponent {
  areas = new Array<string>();
  sections = new Array<FixedLocation>();
  application: Application;
  progressStep: number;

  private fixedLocations = new Array<FixedLocation>();

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
    this.initFixedLocations();

    this.route.params.subscribe(params => {
      let id = Number(params['id']);

      if (id) {
        this.applicationHub.getApplication(id).subscribe(application => {
          this.application = application;
          this.locationState.location = application.location || new Location();
          this.locationState.startDate = application.startTime;
          this.locationState.endDate = application.endTime;

          this.mapHub.selectApplication(application);

          this.selectedArea = Some(this.fixedLocations.filter(ss => ss.id === application.location.fixedLocationId))
            .filter(ss => ss.length > 0)
            .map(ss => ss[0].area)
            .orElse(undefined);

          this.selectedFixedLocation = application.location.fixedLocationId;
        });
      }

      this.progressStep = ProgressStep.LOCATION;
    });

    this.mapHub.shape().subscribe(shape => this.shapeAdded(shape));
  }

  ngOnDestroy() {
  }

  searchUpdated(filter: SearchbarFilter) {
    this.locationState.location.postalAddress.streetAddress = filter.search;
    this.locationState.startDate = filter.startDate;
    this.locationState.endDate = filter.endDate;
  }

  save() {
    if (this.application.id) {
      // If there is an application to save the location data to
      console.log('Saving location for application id: ', this.application.id);
      this.application.location = this.locationState.location;
      this.application.startTime = this.locationState.startDate;
      this.application.endTime = this.locationState.endDate;
      this.applicationHub.save(this.application).subscribe(application => {
        this.router.navigate(['/applications', application.id, 'summary']);
      });
    } else {
      this.router.navigate(['/applications']);
    }
  }

  set selectedArea(area: string) {
    this.sections = this.fixedLocations.filter(fl => fl.area === area);
  }

  get selectedArea(): string {
    return Some(this.sections)
      .filter(sections => sections.length > 0)
      .map(sections => sections[0].area)
      .orElse(undefined);
  }

  noSections(): boolean {
    return this.sections.every(section => !section.section);
  }

  set selectedFixedLocation(id: number) {
    this.locationState.location.fixedLocationId = id;
  }

  get selectedFixedLocation(): number {
    return this.locationState.location.fixedLocationId;
  }


  private shapeAdded(shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) {
    if (shape.features.length) {
      this.locationState.location.geometry = this.mapService.featureCollectionToGeometryCollection(shape);
    } else {
      this.locationState.location.geometry = undefined;
    }
  }

  private initFixedLocations(): void {
    this.mapHub.fixedLocations()
      .subscribe(fl => {
        this.areas = fl.map(entry => entry.area).filter((v, i, a) => a.indexOf(v) === i); // unique area names
        this.fixedLocations = fl;
      });
  }
}
