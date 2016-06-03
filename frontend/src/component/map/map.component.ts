import 'leaflet';
import 'leaflet-draw';
import {Component, ViewChild} from '@angular/core';
import {NavigatorComponent} from '../navigator/navigator.component';
import {MapService} from '../../service/map.service';
import {GeocodingService} from '../../service/geocoding.service';

import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {EventService} from '../../event/event.service';

import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';
import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {ShapeAnnounceEvent} from '../../event/announce/shape-announce-event';
import {WorkqueueService} from '../../service/workqueue.service';

@Component({
    selector: 'map',
    moduleId: module.id,
    template: require('./map.component.html'),
    styles: [
        require('./map.component.scss')
    ],
    directives: [NavigatorComponent]
})
export class MapComponent implements EventListener {
    // @ViewChild(MarkerComponent) markerComponent:MarkerComponent;

    private mapService: MapService;
    private geocoder: GeocodingService;
    private workqueue: WorkqueueService;
    private marker: any;
    private applicationArea: L.LayerGroup<L.ILayer>;

    constructor(mapService: MapService, geocoder: GeocodingService, workqueue: WorkqueueService, private eventService: EventService) {
        this.eventService.subscribe(this);
        this.mapService = mapService;
        this.geocoder = geocoder;
        this.workqueue = workqueue;
        this.applicationArea = undefined;
    }

    public handle(event: Event): void {
      if (event instanceof ApplicationSelectionEvent) {
        let area = event.area;
        if (this.applicationArea) {
          this.mapService.map.removeLayer(this.applicationArea);
        }
        this.applicationArea = new L.GeoJSON(area).addTo(this.mapService.map);
      }
    }

    ngOnInit() {
      this.mapService.map = new L.Map('map', {
        zoomControl: false,
        center: new L.LatLng(60.175264, 24.940692),
        zoom: 14,
        minZoom: 4,
        maxZoom: 18,
        layers: [this.mapService.baseMaps.CartoDB]
      });
      L.control.zoom({ position: 'topright' }).addTo(this.mapService.map);

      let drawnItems = new L.GeoJSON();
      this.mapService.map.addLayer(drawnItems);

      let drawControl = new L.Control.Draw({
        position: 'topright',
        draw: {
          polygon: {
            shapeOptions: {
              color: '#BA1200'
            }
          },
          marker: false
        },
        edit: {
          featureGroup: drawnItems
        }
      });
      this.mapService.map.addControl(drawControl);
      let that = this;
      this.mapService.map.on('draw:created', function (e: any) {
        let type = e.layerType,
            layer = e.layer;

        drawnItems.addLayer(layer);
        that.eventService.send(that, new ShapeAnnounceEvent(drawnItems.toGeoJSON()));
      });

      // Add zoom controls

      L.control.layers(this.mapService.baseMaps).addTo(this.mapService.map);
      L.control.scale().addTo(this.mapService.map);

    }

    ngOnDestroy() {
      // TODO: See how to destroy map, so that it will be built again.
      this.eventService.unsubscribe(this);
      if (this.mapService.map) {
        this.mapService.map.removeLayer(this.mapService.baseMaps.CartoDB);
      }
    }
}
