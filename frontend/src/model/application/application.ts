import {Area} from '../location/area';
import {Customer} from '../customer/customer';
import {Person} from './person';
import {Billing} from './billing';
import {Applicant} from './applicant';
import {Project} from './project';
import {BackendApplication} from '../../service/backend-model/backend-application';

export class Application {

  constructor(
    public id: number,
    public name: string,
    public type: string,
    public status: string,
    public latitude: number,
    public longitude: number,
    public handler: string,
    public area: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>,
    public customer: Customer,
    public contact: Person,
    public billing: Billing,
    public applicant: Applicant,
    public project: Project) {
    // ALL APPLICATIONS DO NOT HAVE A SINGLE LATLNG COORDINATE, E.G. AREA
  }
}
