import {AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';

import {MapRole, MapStore} from '@service/map/map-store';
import {Application} from '@model/application/application';
import {Some} from '@util/option';
import {findTranslation} from '@util/translations';
import {pathStyle, styleByApplicationType} from '@service/map/map-draw-styles';
import {Location} from '@model/common/location';
import * as L from 'leaflet';
import {MapController, ShapeAdded} from '@service/map/map-controller';
import {Observable, Subject} from 'rxjs';
import {ProjectService} from '@service/project/project.service';
import {filter, switchMap, takeUntil} from 'rxjs/internal/operators';
import {TimeUtil} from '@util/time.util';
import {MapUtil} from '@service/map/map.util';
import {Feature, FeatureCollection, GeometryCollection, GeometryObject} from 'geojson';
import {MapLayer} from '@service/map/map-layer';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromMap from '@feature/map/reducers';
import {MapFeatureInfo} from '@service/map/map-feature-info';
import {EnumUtil} from '@util/enum.util';
import {ApplicationType} from '@model/application/type/application-type';
import {FixedLocation} from '@model/common/fixed-location';

@Component({
  selector: 'map',
  templateUrl: './map.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
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
  @Input() availableLayers: MapLayer[] = [];
  @Input() focusOnDrawn = false;

  @Output() editedItemCountChanged = new EventEmitter<number>();

  loading$: Observable<boolean>;

  private destroy = new Subject<boolean>();

  constructor(
    private mapStore: MapStore,
    private projectService: ProjectService,
    private mapController: MapController,
    private store: Store<fromRoot.State>,
    private mapUtil: MapUtil) {}

  ngOnInit() {
    this.mapStore.roleChange(this.role);
    this.mapController.availableLayers = this.availableLayers;

    // fixes the NG0100: ExpressionChangedAfterItHasBeenCheckedError error
    setTimeout(() => {
      this.loading$ = this.store.pipe(select(fromMap.getApplicationsLoading));
    });
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
    this.mapController.centerAndZoomOnEditedAndFocused();
  }

  ngOnDestroy() {
    this.mapController.remove();
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  @Input() set selectedLayers(layers: MapLayer[]) {
    this.mapController.selectedLayers = layers;
  }

  applicationSelected(application: Application) {
    this.mapController.clearDrawn();

    // Check to see if the application has a location
    const geometries = this.geometries(application);
    if (this.geometryCount(geometries) > 0) {
      this.mapController.drawGeometry(geometries, findTranslation(['application.type', application.type]));
      this.mapController.centerAndZoomOnDrawn();
    }
  }

  addGeometry(geometry: GeometryCollection): void {
    this.mapController.drawFixedGeometries([geometry], pathStyle.HIGHLIGHT_ADDED);
    this.mapController.fitEditedToView();
  }

  addFeatures(features: Feature<GeometryObject>[]): void {
    Some(this.mapUtil.wrapToFeatureCollection(features))
      .map(featureCollection => this.mapUtil.sanitizeForLeaflet(featureCollection))
      .map(sanitized => this.mapUtil.unprojectFeatureCollection(sanitized))
      .do(unprojected => {
        this.mapController.drawFeatures(unprojected, pathStyle.HIGHLIGHT_ADDED);
        this.mapController.fitEditedToView();
      });
  }

  private drawAndFocusApplications(applications: Application[]) {
    this.mapController.clearDrawn();
    this.drawApplications(applications);
    this.mapController.centerAndZoomOnDrawn();
  }

  private drawApplications(applications: Array<Application>) {
    const drawnApplications = applications
      .filter(app => this.applicationShouldBeDrawn(app))
      .filter(app => app.id !== this.applicationId); // Only draw other than edited application

    const featureGroupsByType = drawnApplications.reduce((acc, app) => {
      const featureCollections = app.locations
        .map(loc => loc.geometry)
        .map(gc => this.mapUtil.createFeatureCollection(gc, this.createFeatureInfo(app)));

      if (acc[app.type] === undefined) {
        acc[app.type] = this.mapUtil.mergeFeatureCollections(featureCollections);
      } else {
        const existing = [acc[app.type]];
        acc[app.type] = this.mapUtil.mergeFeatureCollections(existing.concat(featureCollections));
      }
      return acc;
    }, {});

    EnumUtil.enumValues(ApplicationType).forEach(type => {
      const layerName = findTranslation(['application.type', type]);
      this.mapController.drawToLayer(layerName, featureGroupsByType[type], styleByApplicationType[type]);
    });
  }

  private createFeatureInfo(application: Application): MapFeatureInfo {
    return {
      id: application.id,
      name: application.name,
      applicationId: application.applicationId,
      startTime: application.startTime,
      endTime: application.endTime,
      recurringEndTime: application.recurringEndTime,
      terminationTime: application.terminationTime,
      applicant: Some(application.applicant.customer).map(c => c.name).orElse(undefined)
    };
  }

  private applicationShouldBeDrawn(application: Application): boolean {
    const allAreDrawn = !this.showOnlyApplicationArea && this.projectId === undefined;
    const isSelectedApplication = this.showOnlyApplicationArea && application.id === this.applicationId;
    const belongsToProject = Some(application.project).map(p => p.id === this.projectId).orElse(false);
    return isSelectedApplication || allAreDrawn || belongsToProject;
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
      const geometryCount = this.geometryCount(locations.map(loc => loc.geometry));
      this.editedItemCountChanged.emit(geometryCount);
    }
  }

  private drawFixedLocations(fixedLocations: FixedLocation[]) {
    this.mapController.clearEdited();

    const geometries = fixedLocations.map(fl => fl.geometry);
    if (geometries.length > 0) {
      this.mapController.drawFixedGeometries(geometries);
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

  private featuresToGeoJSON(featureGroup: L.FeatureGroup): FeatureCollection<GeometryObject> {
    const features = L.featureGroup();
    featureGroup.eachLayer(l => {
      if (l instanceof L.Circle) {
        // Convert circle to polygon since GeoJSON does not support circle
        features.addLayer(l.toPolygon());
      } else {
        features.addLayer(l);
      }
    });

    return <FeatureCollection<GeometryObject>>features.toGeoJSON();
  }

  private initSubscriptions(): void {
    this.store.select(fromMap.getCoordinates).pipe(
      takeUntil(this.destroy),
      filter(coordinates => !!coordinates)
    ).subscribe(coordinates => this.mapController.panToCoordinates(coordinates));

    this.mapController.shapes.pipe(takeUntil(this.destroy))
      .subscribe(shapes => this.addShape(shapes));

    this.store.pipe(
      select(fromMap.getApplications),
      takeUntil(this.destroy)
    ).subscribe(applications => {
      if (this.focusOnDrawn) {
        this.drawAndFocusApplications(applications);
      } else {
        this.drawApplications(applications);
      }
    });

    this.mapStore.selectedApplication.pipe(
      takeUntil(this.destroy),
      filter(app => !!app)
    ).subscribe(app => this.applicationSelected(app));

      this.mapStore.fixedLocations.pipe(
        takeUntil(this.destroy),
        switchMap(ids => this.store.pipe(select(fromRoot.getFixedLocationsByIds(ids)))),
      ).subscribe(fxs => this.drawFixedLocations(fxs));

    this.mapStore.editedLocation.pipe(takeUntil(this.destroy))
      .subscribe(loc => this.drawEditedLocation(loc));

    this.mapStore.locationsToDraw.pipe(takeUntil(this.destroy))
      .subscribe(locs => this.drawFocusedLocations(locs));
  }

  private geometries(application: Application): GeometryCollection[] {
    return application.locations.map(loc => loc.geometry);
  }

  private geometryCount(geometries: GeometryCollection[]): number {
    return geometries.reduce((acc, cur) => acc + MapUtil.geometryCount(cur), 0);
  }
}
