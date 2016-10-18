import {Component} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import 'proj4leaflet';
import 'leaflet';

import {ProgressStep} from '../../feature/progressbar/progressbar.component';
import {Application} from '../../model/application/application';
import {Location} from '../../model/common/location';
import {PostalAddress} from '../../model/common/postal-address';
import {MapUtil} from '../../service/map.util.ts';
import {SearchbarFilter} from '../../service/searchbar-filter';
import {LocationState} from '../../service/application/location-state';
import {ApplicationHub} from '../../service/application/application-hub';
import {MapHub} from '../../service/map-hub';

@Component({
  selector: 'type',
  viewProviders: [],
  template: require('./location.component.html'),
  styles: [
    require('./location.component.scss')
  ]
})
export class LocationComponent {
  private application: Application;
  private rentingPlace: any;
  private sections: any;
  private area: number;
  private progressStep: number;

  constructor(
    private locationState: LocationState,
    private mapService: MapUtil,
    private router: Router,
    private route: ActivatedRoute,
    private applicationHub: ApplicationHub,
    private mapHub: MapHub) {
    this.rentingPlace = [{name: 'Paikka A', value: 'a'}, {name: 'Paikka B', value: 'b'}, {name: 'Paikka C', value: 'c'}];
    this.sections = [{name: 'Lohko A', value: 'a'}, {name: 'Lohko B', value: 'b'}, {name: 'Lohko C', value: 'c'}];
    this.area = undefined;
    this.application = new Application();
    this.locationState.location = new Location();
  };

  ngOnInit() {
    this.route.params.subscribe(params => {
      let id = Number(params['id']);

      if (id) {
        this.applicationHub.getApplication(id).subscribe(application => {
          this.application = application;
          this.locationState.location = application.location || new Location();
          this.locationState.startDate = application.startTime;
          this.locationState.endDate = application.endTime;
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

  private shapeAdded(shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) {
    if (shape.features.length) {
      this.locationState.location.geometry = this.mapService.featureCollectionToGeometryCollection(shape);
    } else {
      this.locationState.location.geometry = undefined;
    }
  }
}
