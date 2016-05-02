import 'leaflet';
import {Component, ViewChild} from 'angular2/core';
import {NavigatorComponent} from '../navigator/navigator.component';
import {MarkerComponent} from '../marker/marker.component';
import {MapService} from '../../service/map.service';
import {GeocodingService} from '../../service/geocoding.service';
// import {Location} from '../core/location.class';

@Component({
    selector: 'map',
    moduleId: module.id,
    templateUrl: './component/map/map.component.html',
    styles: [
        require('./map.component.scss')
    ],
    directives: [NavigatorComponent, MarkerComponent]
})
export class MapComponent {
    @ViewChild(MarkerComponent) markerComponent:MarkerComponent;

    private mapService: MapService;
    private geocoder: GeocodingService;


    constructor(mapService: MapService, geocoder: GeocodingService) {
        this.mapService = mapService;
        this.geocoder = geocoder;
    }

    ngOnInit() {
        var map = new L.Map('map', {
          zoomControl: false,
          center: new L.LatLng(60.192059, 24.945831),
          zoom: 12,
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

    ngAfterViewInit() {
        this.markerComponent.Initialize();
    }
}
