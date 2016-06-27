import {Component} from '@angular/core';
import {RouteParams} from '@angular/router-deprecated';

import {MD_INPUT_DIRECTIVES} from '@angular2-material/input';
import {MdButton} from '@angular2-material/button';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import {MdToolbar} from '@angular2-material/toolbar';

import {MapComponent} from '../map/map.component';

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
import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';

import 'proj4leaflet';
import 'leaflet';
import {MapService} from '../../service/map.service';

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
    MdToolbar,
    MD_INPUT_DIRECTIVES,
    MD_CARD_DIRECTIVES,
    MdButton,
    MapComponent
  ],
  providers: []
})
export class LocationComponent implements EventListener {
  private application: Application;
  private id: number;
  private features: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>;
  private enableSave: boolean = false;
  private hasChanges: HasChanges = HasChanges.NO;

  constructor(private eventService: EventService, private mapService: MapService, params: RouteParams) {
    this.id = Number(params.get('id'));
  };

  public handle(event: Event): void {
    if (event instanceof ShapeAnnounceEvent) {
      console.log('LocationComponent.handle ShapeAnnounceEvent', event.shape.features);
      let singleCoordinate = event.shape.features[0].geometry.coordinates[0][0];
      console.log('Geometry coordinate', singleCoordinate);
      let myProj = this.mapService.getEPSG3879();
      console.log('projected coordinate', myProj.projection.project(new L.LatLng(singleCoordinate[1], singleCoordinate[0])));
      let saEvent = <ShapeAnnounceEvent>event;
      this.features = saEvent.shape;
      this.hasChanges = HasChanges.YES;
      this.enableSave = true;
    } else if (event instanceof ApplicationsAnnounceEvent) {
      let aaEvent = <ApplicationsAnnounceEvent> event;
      // we're only interested about applications matching the application being edited
      if (aaEvent.applications.length === 1 && aaEvent.applications[0].id === this.id) {
        this.application = aaEvent.applications[0];
        this.eventService.send(this, new ApplicationSelectionEvent(this.application));
        if (this.hasChanges === HasChanges.PENDING) {
          this.enableSave = false;
          this.hasChanges = HasChanges.NO;
        }
      }
    } else if (event instanceof ErrorEvent) {
      let eEvent = <ErrorEvent>event;
      if (eEvent.originalEvent instanceof ApplicationSaveEvent) {
        let asEvent = <ApplicationSaveEvent>eEvent.originalEvent;
        if (asEvent.application.id === this.application.id) {
          if (this.hasChanges === HasChanges.PENDING) {
            this.enableSave = true;
          } else {
            this.enableSave = false;
          }
        }
      }
    }
  }


  save() {
    console.log('Saving location for application id: ', this.id);
    if (!this.application.location) {
      this.application.location = new Location(undefined, undefined, undefined);
    }
    console.log('Geometry', this.features[0]);
    this.application.location.geometry = this.features;
    // TODO: temporary bug hiding: backend doesn't return type for applicant
    this.application.applicant.type = 'Person';
    let saveEvent = new ApplicationSaveEvent(this.application);
    this.hasChanges = HasChanges.PENDING;
    this.enableSave = false;
    this.eventService.send(this, saveEvent);
  }

  ngOnInit() {
    this.eventService.subscribe(this);
    let filter = new ApplicationLoadFilter();
    filter.applicationId = this.id;
    this.eventService.send(this, new ApplicationsLoadEvent(filter));
  }

  ngOnDestroy() {
    this.eventService.unsubscribe(this);
  }
}
