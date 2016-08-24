import {LoadEvent} from './load-event';

export class GeocoordinatesLoadEvent extends LoadEvent {
    constructor(public address: string) {
        super();
    }
}
