import {Area} from '../location/area';
export class Application {

  constructor(
    public id: number,
    public title: string,
    public name: string,
    public type: string,
    public time: string,
    public latitude: number,
    public longitude: number,
    public area: Area) {
    // ALL APPLICATIONS DO NOT HAVE A SINGLE LATLNG COORDINATE, E.G. AREA
  }

}
