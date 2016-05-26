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


    constructor(mapService: MapService, geocoder: GeocodingService, workqueue: WorkqueueService, private eventService: EventService) {
        this.eventService.subscribe(this);
        this.mapService = mapService;
        this.geocoder = geocoder;
        this.workqueue = workqueue;
    }

    public handle(event: Event): void {

      if (event instanceof ApplicationSelectionEvent) {
        let id = event.id;
        let job = this.workqueue.get(id);

        if (this.marker) {
          this.mapService.map.removeLayer(this.marker);
        }

        if (job.area) {

          if (job.area.type === 'polyline') {
            this.marker = L.polyline(job.area.latlngs, {color: '#BA1200'});
          } else if (job.area.type === 'circle') {
            this.marker = L.circle(job.area.latlngs[0], job.area.radius, {color: '#BA1200'});
          } else {
            this.marker = L.polygon(job.area.latlngs, {color: '#BA1200'});
          }

          this.marker.addTo(this.mapService.map).bindPopup(job.title).openPopup();
          this.mapService.map.setView([job.latitude, job.longitude]);

        } else {
          this.marker = L.marker([job.latitude, job.longitude], {
            icon: L.icon({
              iconUrl: '../../assets/svg/marker.svg',
              iconSize: [40, 40]
            }),
            draggable: false
          }).addTo(this.mapService.map).bindPopup(job.title).openPopup();
          this.mapService.map.setView([job.latitude, job.longitude]);
        }
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

      let drawnItems = new L.FeatureGroup();
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

      this.mapService.map.on('draw:created', function (e: any) {
        let type = e.layerType,
            layer = e.layer;

        drawnItems.addLayer(layer);
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
