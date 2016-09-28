import {Component} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import {ProgressStep, ProgressMode} from '../../feature/progressbar/progressbar.component';

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

import 'proj4leaflet';
import 'leaflet';
import {MapUtil} from '../../service/map.util.ts';
import {SearchbarFilter} from '../../event/search/searchbar-filter';
import {LocationState} from '../../service/application/location-state';

enum HasChanges {
  NO,
  PENDING,
  YES
}

@Component({
  selector: 'type',
  viewProviders: [],
  template: require('./location.component.html'),
  styles: [
    require('./location.component.scss')
  ]
})
export class LocationComponent implements EventListener {
  private application: Application;
  private id: number;
  private hasChanges: HasChanges = HasChanges.NO;
  private rentingPlace: any;
  private sections: any;
  private area: number;
  private progressStep: number;
  private progressMode: number;

  constructor(
    private locationState: LocationState,
    private eventService: EventService,
    private mapService: MapUtil,
    private router: Router,
    private route: ActivatedRoute) {
    this.progressMode = this.id ? ProgressMode.EDIT : ProgressMode.NEW;
    this.progressStep = ProgressStep.LOCATION;

    this.rentingPlace = [{name: 'Paikka A', value: 'a'}, {name: 'Paikka B', value: 'b'}, {name: 'Paikka C', value: 'c'}];
    this.sections = [{name: 'Lohko A', value: 'a'}, {name: 'Lohko B', value: 'b'}, {name: 'Lohko C', value: 'c'}];
    this.area = undefined;
    this.application = new Application();
    this.locationState.location = new Location();
  };

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.id = Number(params['id']);
    });

    this.eventService.subscribe(this);
    let filter = new ApplicationLoadFilter();
    if (this.id) {
      filter.applicationId = this.id;
      this.eventService.send(this, new ApplicationsLoadEvent(filter));
    }
  }

  ngOnDestroy() {
    this.eventService.unsubscribe(this);
  }

  public handle(event: Event): void {
    if (event instanceof ShapeAnnounceEvent) {
      if (event.shape.features.length) {
        let saEvent = <ShapeAnnounceEvent>event;
        this.locationState.location.geometry = this.mapService.featureCollectionToGeometryCollection(saEvent.shape);
      } else {
        this.locationState.location.geometry = undefined;
      }
      this.hasChanges = HasChanges.YES;
    } else if (event instanceof ApplicationsAnnounceEvent) {
      let aaEvent = <ApplicationsAnnounceEvent> event;
      // we're only interested about applications matching the application being edited
      // TODO: Is this necessary any more?
      if (aaEvent.applications.length === 1 && aaEvent.applications[0].id === this.id) {
        this.application = aaEvent.applications[0];
        this.locationState.location = this.application.location || new Location();

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
    this.locationState.location.postalAddress.streetAddress = filter.search;
    this.locationState.startDate = filter.startDate;
    this.locationState.endDate = filter.endDate;
  }


  save() {
    if (this.id) {
      // If there is an application to save the location data to
      console.log('Saving location for application id: ', this.id);

      if (this.hasChanges === HasChanges.YES) {
        let saveEvent = new ApplicationSaveEvent(this.application);
        this.hasChanges = HasChanges.PENDING;
        this.application.location = this.locationState.location;
        this.eventService.send(this, saveEvent);
      }
      // TODO: disable save button
    } else {
      this.router.navigate(['/applications']);
    }
  }
}
