import {Component} from 'angular2/core';
import {GeocodingService} from '../../service/geocoding.service';
import {MapService} from '../../service/map.service';
import {Location} from '../../service/location.class';
import {Map} from 'leaflet';

@Component({
    selector: 'navigator',
    moduleId: module.id,
    template: require('./navigator.component.html'),
    styles: [
        // './component/navigator/navigator.component.scss',
        // '../../main.scss'
        require('./navigator.component.scss')
    ]
})
export class NavigatorComponent {
    address: string;

    private geocoder: GeocodingService;
    private map: Map;
    private mapService: MapService;

    constructor(geocoder: GeocodingService, mapService: MapService) {
        this.address = '';
        this.geocoder = geocoder;
        this.mapService = mapService;
    }

    ngOnInit() {
        this.mapService.disableMouseEvent('goto');
        this.mapService.disableMouseEvent('place-input');
        this.map = this.mapService.map;
    }

    goto() {
        if (!this.address) { return; }

        this.geocoder.geocode(this.address)
        .subscribe(location => {
            this.map.fitBounds(location.viewBounds);
            this.address = location.address;
        }, error => console.error(error));
    }
}
