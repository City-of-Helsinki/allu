import 'leaflet';
import {Component, ViewChild} from 'angular2/core';
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
    templateUrl: './component/map/map.component.html',
    styles: [
        require('./map.component.scss')
    ],
    directives: [NavigatorComponent]
})
export class MapComponent implements EventListener{
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

      if (event instanceof ApplicationSelectionEvent) {
        if (this.marker) {
          this.mapService.map.removeLayer(this.marker);
        }

        this.marker = L.marker([event.latLng.latitude, event.latLng.longitude], {
          icon: L.icon({
            iconUrl: '../../assets/svg/marker.svg',
            iconSize: [40, 40]
          }),
          draggable: false,
        }).addTo(this.mapService.map).bindPopup(event.title).openPopup();
      }

    }

    ngOnInit() {
        var map = new L.Map('map', {
          zoomControl: false,
          center: new L.LatLng(60.175264, 24.940692),
          zoom: 14,
          minZoom: 4,
          maxZoom: 19,
          layers: [this.mapService.baseMaps.OpenStreetMap]
        });

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
