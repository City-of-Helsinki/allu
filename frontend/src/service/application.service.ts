import { Injectable } from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Application} from '../model/application/application';
import {Area} from '../model/location/area';
import {Customer} from '../model/customer/customer';
import {ApplicationMapper} from './mapper/application-mapper';

@Injectable()
export class ApplicationService {

  private applications: Array<Application> = [];

  constructor(private authHttp: AuthHttp) {}

  public listApplications(): Array<Application> {
    // this.applications = this.createMockApplications();

    // this.authHttp.get('/api/applications').subscribe(
    //   data => this.addBackendApplication(this.applications, data),
    //   err => console.log(err),
    //   () => console.log('Request Complete')
    // );
    // console.log('Listing applications', this.applications);
    return this.applications;
  }

  public addApplication(application: Application): Promise<Application> {

    return new Promise<Application>((resolve, reject) =>
      this.authHttp.post('/api/applications', JSON.stringify({ 'applicationList': [ApplicationMapper.mapFrontend(application)] }))
        // JSON.stringify({ 'applicationList': [{ 'name': 'namez', 'type': 'typez',
        //   'customer': { 'name': 'custo'} }]}))
        .subscribe(
          data => { 
            let appl = ApplicationMapper.mapBackend(data.json().applicationList[0]);
            console.log('Created application', appl);
            resolve(appl);
          },
          err => reject(err),
          () => console.log('Request Complete')
        )
    );
  }

/*
  private mapBackendApplication(anyApp: any): Application {
    let appJson = anyApp.json();
    console.log('anyApp.id', appJson);
    let app = appJson.applicationList[0];
    // let customer = new Customer(undefined, app.customer.name, undefined, undefined, undefined, undefined);
    // let newApplication = new Application(app.id, 'undefined title', app.name, app.type,
    //   'undefined time', 60.179884, 24.930865, undefined, customer);

    return newApplication;
  }
  */
}
