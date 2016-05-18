import 'leaflet';
import 'leaflet-draw';
import {Component, ViewChild} from '@angular/core';
import {NavigatorComponent} from '../navigator/navigator.component';
// import {MarkerComponent} from '../marker/marker.component';
import {MapService} from '../../service/map.service';
import {GeocodingService} from '../../service/geocoding.service';

import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {EventService} from '../../event/event.service';
// import {Location} from '../core/location.class';

import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';

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
    private marker: L.Marker;


    constructor(mapService: MapService, geocoder: GeocodingService, private eventService: EventService) {
        this.eventService.subscribe(this);
        this.mapService = mapService;
        this.geocoder = geocoder;
    }

    public handle(event: Event): void {
      console.log('EVENT: ' + event);

      if (event instanceof ApplicationSelectionEvent) {
        if (this.marker) {
          this.mapService.map.removeLayer(this.marker);
        }

        this.marker = L.marker([event.latLng.latitude, event.latLng.longitude], {
          icon: L.icon({
            iconUrl: '../../assets/svg/marker.svg',
            iconSize: [40, 40]
          }),
          draggable: false
        }).addTo(this.mapService.map).bindPopup(event.title).openPopup();
        this.mapService.map.setView([event.latLng.latitude, event.latLng.longitude]);
      }

    }

    ngOnInit() {

      let map = new L.Map('map', {
        zoomControl: false,
        center: new L.LatLng(60.175264, 24.940692),
        zoom: 14,
        minZoom: 4,
        maxZoom: 18,
        layers: [this.mapService.baseMaps.OpenStreetMap]
      });


      var drawnItems = new L.FeatureGroup();
      map.addLayer(drawnItems);

      var drawControl = new L.Control.Draw({
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
      map.addControl(drawControl);

      map.on('draw:created', function (e) {
        var type = e.layerType,
            layer = e.layer;

        if (type === 'marker') {
          layer.bindPopup('A popup!');
        }

        console.log(e.layer);

        drawnItems.addLayer(layer);
      });

      // Add zoom controls
      L.control.zoom({ position: 'topright' }).addTo(map);
      L.control.layers(this.mapService.baseMaps).addTo(map);
      L.control.scale().addTo(map);

      this.mapService.map = map;


        // this.geocoder.getCurrentLocation()
        // .subscribe(
        //     location => map.panTo([location.latitude, location.longitude]),
        //     err => console.error(err)
        // );
    }

    // Comment out markerComponent from map
    // ngAfterViewInit() {
    //     this.markerComponent.Initialize();
    // }
}
