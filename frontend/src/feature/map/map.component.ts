import {Component, Input, Output, EventEmitter, OnInit, OnDestroy} from '@angular/core';

import {MapHub} from '../../service/map-hub';
import {Application} from '../../model/application/application';
import {FixedLocation} from '../../model/common/fixed-location';
import {Some} from '../../util/option';
import {ApplicationHub} from '../../service/application/application-hub';
import {findTranslation} from '../../util/translations';
import {ProjectHub} from '../../service/project/project-hub';
import {MapService, ShapeAdded, MapState} from '../../service/map.service';

@Component({
  selector: 'map',
  template: require('./map.component.html'),
  styles: []
})
export class MapComponent implements OnInit, OnDestroy {
  @Input() draw: boolean = false;
  @Input() edit: boolean = false;
  @Input() zoom: boolean = false;
  @Input() selection: boolean = false;
  @Input() applicationId: number;
  @Input() projectId: number;
  @Input() showOnlyApplicationArea: boolean = false;

  @Output() editedItemCountChanged = new EventEmitter<number>();

  private mapState: MapState;

  constructor(
    private mapService: MapService,
    private mapHub: MapHub,
    private applicationHub: ApplicationHub,
    private projectHub: ProjectHub) {}

  ngOnInit() {
    this.mapState = this.mapService.create(this.draw, this.edit, this.zoom, this.selection, this.showOnlyApplicationArea);

    this.mapState.shapes.subscribe(shapes => this.addShape(shapes));
    this.mapState.mapView.subscribe(view => this.mapHub.addMapView(view));

    this.mapHub.coordinates()
      .subscribe((optCoords) =>
        optCoords.map(coordinates => this.mapState.panToCoordinates(coordinates)));

    this.mapHub.applications().subscribe(applications => this.drawApplications(applications));
    this.mapHub.applicationSelection().subscribe(app => this.applicationSelected(app));
    this.mapHub.selectedFixedLocations().subscribe(fxs => this.drawFixedLocations(fxs));
    // Handle fetching and drawing edited application as separate case
    Some(this.applicationId).do(id => this.applicationHub.getApplication(id).subscribe(app => this.drawEditedApplication(app)));

    Some(this.projectId).do(id => this.drawProject(id));
  }

  ngOnDestroy() {
  }

  applicationSelected(application: Application) {
    this.mapState.clearDrawn();

    // Check to see if the application has a location
    if (application.hasGeometry()) {
      this.mapState.drawGeometry(application.location.geometry, findTranslation(['application.type', application.type]));
      this.mapState.centerAndZoomOnDrawn();
    }
  }

  private drawProject(id: number) {
    this.projectHub.getProjectApplications(id).subscribe(apps => {
      this.drawApplications(apps);
      this.mapState.centerAndZoomOnDrawn();
    });
  }

  private drawApplications(applications: Array<Application>) {
    this.mapState.clearDrawn();
    applications
      .filter(app => app.location !== undefined)
      .filter(app => this.applicationShouldBeDrawn(app))
      .filter(app => app.id !== this.applicationId) // Only draw other than edited application
      .forEach(app => this.mapState.drawGeometry(app.location.geometry, findTranslation(['application.type', app.type])));
  }

  private applicationShouldBeDrawn(application: Application): boolean {
    let allAreDrawn = !this.showOnlyApplicationArea && this.projectId === undefined;
    let isSelectedApplication = this.showOnlyApplicationArea && application.id === this.applicationId;
    return isSelectedApplication || allAreDrawn || application.belongsToProject(this.projectId);
  }


  private drawEditedApplication(application: Application) {
    this.mapState.drawEditableGeometry(application.location.geometry);
    this.updateMapControls(application);
  }

  private updateMapControls(application: Application) {
    if (application.hasFixedGeometry()) {
      this.mapState.setDynamicControls(false);
    } else {
      this.editedItemCountChanged.emit(application.geometryCount());
    }
  }

  private drawFixedLocations(fixedLocations: Array<FixedLocation>) {
    this.mapState.clearEdited();

    let geometries = fixedLocations.map(fl => fl.geometry);
    if (geometries.length > 0) {
      this.mapState.drawFixedLocations(geometries);
      this.mapState.fitEditedToView();
    }

    // Disable editing map with draw controls when we have fixed locations
    this.mapState.setDynamicControls(fixedLocations.length === 0);
  }

  private addShape(shapeAdded: ShapeAdded) {
    let shape = shapeAdded.features.toGeoJSON();
    this.mapHub.addShape(shape);

    if (shapeAdded.affectsControls) {
      this.editedItemCountChanged.emit(shape.features.length);
    }
  }
}
