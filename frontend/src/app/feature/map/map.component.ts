import {AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';

import {MapRole, MapStore} from '../../service/map/map-store';
import {Application} from '../../model/application/application';
import {Some} from '../../util/option';
import {findTranslation} from '../../util/translations';
import {pathStyle, styleByApplicationType} from '../../service/map/map-draw-styles';
import {FixedLocationSection} from '../../model/common/fixed-location-section';
import {Location} from '../../model/common/location';
import * as L from 'leaflet';
import {MapController, ShapeAdded} from '../../service/map/map-controller';
import {Subject} from 'rxjs/Subject';
import {FixedLocationService} from '../../service/map/fixed-location.service';
import {ProjectService} from '../../service/project/project.service';

@Component({
  selector: 'map',
  templateUrl: './map.component.html',
  styleUrls: []
})
export class MapComponent implements OnInit, OnDestroy, AfterViewInit {
  @Input() draw = false;
  @Input() edit = false;
  @Input() zoom = false;
  @Input() selection = false;
  @Input() applicationId: number;
  @Input() projectId: number;
  @Input() showOnlyApplicationArea = false;
  @Input() role: MapRole = 'SEARCH';

  @Output() editedItemCountChanged = new EventEmitter<number>();

  private destroy = new Subject<boolean>();

  constructor(
    private mapStore: MapStore,
    private fixedLocationService: FixedLocationService,
    private projectService: ProjectService,
    private mapController: MapController) {}

  ngOnInit() {
    this.mapStore.roleChange(this.role);
  }

  /**
   * Use after view init for map initialization
   * since map div might not be available during ngOnInit
   */
  ngAfterViewInit(): void {
    this.mapController.init({
      draw: this.draw,
      edit: this.edit,
      zoom: this.zoom,
      selection: this.selection,
      showOnlyApplicationArea: this.showOnlyApplicationArea
    });
    this.initSubscriptions();
    Some(this.projectId).do(id => this.drawProject(id));
    this.mapController.selectDefaultLayer();
  }

  ngOnDestroy() {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  applicationSelected(application: Application) {
    this.mapController.clearDrawn();

    // Check to see if the application has a location
    if (application.hasGeometry()) {
      this.mapController.drawGeometry(application.geometries(), findTranslation(['application.type', application.type]));
      this.mapController.centerAndZoomOnDrawn();
    }
  }

  public centerAndZoomOnDrawn() {
    this.mapController.centerAndZoomOnDrawn();
  }

  private drawProject(id: number) {
    this.projectService.getProjectApplications(id).subscribe(apps => {
      this.drawApplications(apps);
      this.centerAndZoomOnDrawn();
    });
  }

  private drawApplications(applications: Array<Application>) {
    this.mapController.clearDrawn();
    applications
      .filter(app => this.applicationShouldBeDrawn(app))
      .filter(app => app.id !== this.applicationId) // Only draw other than edited application
      .forEach(app => this.drawApplication(app));
  }

  private drawApplication(application: Application): void {
    const featureInfo = {
      id: application.id,
      name: application.name,
      applicationId: application.applicationId,
      startTime: application.uiStartTime,
      endTime: application.uiEndTime
    };

    this.mapController.drawGeometry(
      application.geometries(),
      findTranslation(['application.type', application.type]),
      styleByApplicationType[application.type],
      featureInfo);
  }

  private applicationShouldBeDrawn(application: Application): boolean {
    const allAreDrawn = !this.showOnlyApplicationArea && this.projectId === undefined;
    const isSelectedApplication = this.showOnlyApplicationArea && application.id === this.applicationId;
    return isSelectedApplication || allAreDrawn || application.belongsToProject(this.projectId);
  }

  private drawFocusedLocations(locations: Array<Location>): void {
    this.mapController.clearFocused();
    const geometries = locations.map(loc => loc.geometry).filter(geometry => !!geometry);
    this.mapController.drawFocused(geometries);
  }

  private drawEditedLocation(location: Location): void {
    this.mapController.clearEdited();
    if (location) {
      this.mapController.drawEditableGeometry(location.geometry, pathStyle.DEFAULT);
      this.updateMapControls([location]);
    }
  }

  private updateMapControls(locations: Array<Location>) {
    if (!locations.some(loc => loc.hasFixedGeometry())) {
      const geometryCount = locations.reduce((cur, acc) => cur + acc.geometryCount(), 0);
      this.editedItemCountChanged.emit(geometryCount);
    }
  }

  private drawFixedLocations(fixedLocations: Array<FixedLocationSection>) {
    this.mapController.clearEdited();

    const geometries = fixedLocations.map(fl => fl.geometry);
    if (geometries.length > 0) {
      this.mapController.drawFixedLocations(geometries);
      this.mapController.fitEditedToView();
    }
  }

  private addShape(shapeAdded: ShapeAdded) {
    const shape = this.featuresToGeoJSON(shapeAdded.features);
    this.mapStore.shapeChange(shape);

    if (shapeAdded.affectsControls) {
      this.editedItemCountChanged.emit(shape.features.length);
    }
  }

  private featuresToGeoJSON(featureGroup: L.FeatureGroup): GeoJSON.FeatureCollection<GeoJSON.GeometryObject> {
    const features = L.featureGroup();
    featureGroup.eachLayer(l => {
      if (l instanceof L.Circle) {
        // Convert circle to polygon since GeoJSON does not support circle
        features.addLayer(l.toPolygon());
      } else {
        features.addLayer(l);
      }
    });

    return <GeoJSON.FeatureCollection<GeoJSON.GeometryObject>>features.toGeoJSON();
  }

  private initSubscriptions(): void {
    this.mapStore.coordinates
      .takeUntil(this.destroy)
      .subscribe(opt => opt.map(coordinates => this.mapController.panToCoordinates(coordinates)));

    this.mapController.shapes
      .takeUntil(this.destroy)
      .subscribe(shapes => this.addShape(shapes));

    this.mapStore.applications
      .takeUntil(this.destroy)
      .subscribe(applications => this.drawApplications(applications));

    this.mapStore.selectedApplication
      .takeUntil(this.destroy)
      .filter(app => !!app)
      .subscribe(app => this.applicationSelected(app));

      this.mapStore.selectedSections
        .takeUntil(this.destroy)
        .switchMap(ids => this.fixedLocationService.sectionsByIds(ids))
        .subscribe(fxs => this.drawFixedLocations(fxs));

    this.mapStore.editedLocation
      .takeUntil(this.destroy)
      .subscribe(loc => this.drawEditedLocation(loc));

    this.mapStore.locationsToDraw
      .takeUntil(this.destroy)
      .subscribe(locs => this.drawFocusedLocations(locs));
  }
}
