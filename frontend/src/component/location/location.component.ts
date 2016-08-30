import {Component} from '@angular/core';
import {Router, RouteParams, ROUTER_DIRECTIVES} from '@angular/router-deprecated';

import {MD_INPUT_DIRECTIVES} from '@angular2-material/input';
import {MdButton} from '@angular2-material/button';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import {MdToolbar} from '@angular2-material/toolbar';
import {MaterializeDirective} from 'angular2-materialize';

import {MapComponent} from '../map/map.component';
import {ProgressStep, ProgressMode, ProgressbarComponent} from '../../component/progressbar/progressbar.component';
import {ApplicationListComponent} from '../application/list/application-list.component';

import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {Event} from '../../event/event';
import {EventListener} from '../../event/event-listener';
import {Application} from '../../model/application/application';
import {EventService} from '../../event/event.service';
import {ApplicationSaveEvent} from '../../event/save/application-save-event';
import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {ShapeAnnounceEvent} from '../../event/announce/shape-announce-event';
import {ApplicationLoadFilter} from '../../event/load/application-load-filter';
import {ErrorEvent} from '../../event/error-event';
import {Location} from '../../model/common/location';
import {PostalAddress} from '../../model/common/postal-address';
import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';
import {SearchbarComponent} from '../../component/searchbar/searchbar.component';

import 'proj4leaflet';
import 'leaflet';
import {MapUtil} from '../../service/map.util.ts';
import {SearchbarFilter} from '../../event/search/searchbar-filter';

enum HasChanges {
  NO,
  PENDING,
  YES
}



@Component({
  selector: 'type',
  viewProviders: [],
  moduleId: module.id,
  template: require('./location.component.html'),
  styles: [
    require('./location.component.scss')
  ],
  directives: [
    ROUTER_DIRECTIVES,
    MdToolbar,
    MD_INPUT_DIRECTIVES,
    MD_CARD_DIRECTIVES,
    MdButton,
    MaterializeDirective,
    MapComponent,
    ProgressbarComponent,
    ApplicationListComponent,
    SearchbarComponent
  ],
  providers: []
})
export class LocationComponent implements EventListener {
  private application: Application;
  private id: number;
  private features: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>;
  private hasChanges: HasChanges = HasChanges.NO;
  private rentingPlace: any;
  private sections: any;
  private area: number;
  private progressStep: number;
  private progressMode: number;

  constructor(private eventService: EventService, private mapService: MapUtil, private router: Router, params: RouteParams) {
    // A location of a certain application must be editable. This means if there is an id associated with the route, it should go there.
    // If there is no parameter id, this.id will be 0.
    this.id = Number(params.get('id'));
    this.progressMode = this.id ? ProgressMode.EDIT : ProgressMode.NEW;
    this.progressStep = ProgressStep.LOCATION;

    this.rentingPlace = [{name: 'Paikka A', value: 'a'}, {name: 'Paikka B', value: 'b'}, {name: 'Paikka C', value: 'c'}];
    this.sections = [{name: 'Lohko A', value: 'a'}, {name: 'Lohko B', value: 'b'}, {name: 'Lohko C', value: 'c'}];
    this.area = undefined;
  };

  public handle(event: Event): void {
    if (event instanceof ShapeAnnounceEvent) {
      if (event.shape.features.length) {
        console.log('LocationComponent.handle ShapeAnnounceEvent', event.shape.features);
        let singleCoordinate = event.shape.features[0].geometry.coordinates[0][0];
        console.log('Geometry coordinate', singleCoordinate);
        let myProj = this.mapService.getEPSG3879();
        console.log('projected coordinate', myProj.projection.project(new L.LatLng(singleCoordinate[1], singleCoordinate[0])));
        let saEvent = <ShapeAnnounceEvent>event;
        this.features = saEvent.shape;
        this.hasChanges = HasChanges.YES;
      } else {
        // All shapes have been deleted
        this.features = undefined;
        this.hasChanges = HasChanges.YES;
      }
    } else if (event instanceof ApplicationsAnnounceEvent) {
      let aaEvent = <ApplicationsAnnounceEvent> event;
      // we're only interested about applications matching the application being edited
      // TODO: Is this necessary any more?
      if (aaEvent.applications.length === 1 && aaEvent.applications[0].id === this.id) {
        this.application = aaEvent.applications[0];
        this.eventService.send(this, new ApplicationSelectionEvent(this.application));
        if (this.hasChanges === HasChanges.PENDING) {
          this.hasChanges = HasChanges.NO;
          this.router.navigate(['/Summary', {id: this.id}]);
        }
      }
    } else if (event instanceof ErrorEvent) {
      let eEvent = <ErrorEvent>event;
      if (eEvent.originalEvent instanceof ApplicationSaveEvent) {
        let asEvent = <ApplicationSaveEvent>eEvent.originalEvent;
        if (asEvent.application.id === this.application.id) {
          // TODO: Errorcontrol
        }
      }
    }
  }

  searchUpdated(filter: SearchbarFilter) {
    this.application.location = this.createOrGetLocation();
    this.application.location.postalAddress.streetAddress = filter.search;
  }


  save() {
    if (this.id) {
      // If there is an application to save the location data to
      console.log('Saving location for application id: ', this.id);

      if (this.hasChanges === HasChanges.YES) {
        if (this.features) {
          // For existing applications, which do not have a location
          this.application.location = this.createOrGetLocation();
          this.application.location.geometry = this.mapService.featureCollectionToGeometryCollection(this.features);
        } else {
          // Location is removed entirely
          this.application.location = undefined;
        }
        console.log('this.application', this.application);
        let saveEvent = new ApplicationSaveEvent(this.application);
        this.hasChanges = HasChanges.PENDING;
        this.eventService.send(this, saveEvent);
      }
      // TODO: disable save button
    } else {
      // No application to save location data to
      if (this.hasChanges === HasChanges.YES && this.features) {
        localStorage.setItem('features', JSON.stringify(this.mapService.featureCollectionToGeometryCollection(this.features)));
      } else {
        localStorage.removeItem('features');
      }
      this.router.navigate(['/Applications/Type']);
    }
  }

  ngOnInit() {
    this.eventService.subscribe(this);
    let filter = new ApplicationLoadFilter();
    if (this.id) {
      filter.applicationId = this.id;
      this.eventService.send(this, new ApplicationsLoadEvent(filter));
    }
    this.application = Application.emptyApplication();
  }

  ngOnDestroy() {
    this.eventService.unsubscribe(this);
  }

  private createOrGetLocation(): Location {
    return this.application.location
      ? this.application.location
      : new Location(undefined, undefined, new PostalAddress(undefined, undefined, undefined));
  }
}
