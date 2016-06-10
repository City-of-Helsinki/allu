import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {URLSearchParams} from '@angular/http';
import {Application} from '../model/application/application';
import {ApplicationMapper} from './mapper/application-mapper';
import {ApplicationLoadFilter} from '../event/load/application-load-filter';

@Injectable()
export class ApplicationService {

  static APPLICATIONS_URL = '/api/applications';

  constructor(private authHttp: AuthHttp) {}

  public listApplications(filter: ApplicationLoadFilter): Promise<Array<Application>> {
    let searchUrl = ApplicationService.APPLICATIONS_URL;
    let params = new URLSearchParams();

    console.log('ApplicationService.listApplications', filter);
    if (filter.applicationId) {
      searchUrl = searchUrl + '/' + filter.applicationId;
      return new Promise<Array<Application>>((resolve, reject) =>
        this.authHttp.get(searchUrl, { search: params }).subscribe(
          data => {
            console.log('ApplicationService.listApplications', data);
            let applications: Array<Application> = [ ApplicationMapper.mapBackend(data.json()) ];
            console.log('Listing applications', applications);
            resolve(applications);
          },
          err => console.log('ApplicationService.listApplications', err),
          () => console.log('Request Complete')
        )
      );
    } else if (filter.handler) {
      params.set('handler', filter.handler);
      return new Promise<Array<Application>>((resolve, reject) =>
        this.authHttp.get(searchUrl, { search: params }).subscribe(
          data => {
            console.log('ApplicationService.listApplications', data);
            let json = data.json();
            if (json.applicationList) {
              let applications: Array<Application> = data.json().applicationList.map((p) => ApplicationMapper.mapBackend(p));
              console.log('Listing applications', applications);
              resolve(applications);
            } else {
              resolve([]);
            }
          },
          err => console.log('ApplicationService.listApplications', err),
          () => console.log('Request Complete')
        )
      );
    } else {
      return new Promise<Array<Application>>(
        (resolve, reject) => reject('Unexpected filter parameters for ApplicationService.listApplications ' + filter));
    }
  }

  public addApplication(application: Application): Promise<Application> {
    console.log('ApplicationService.addApplication', application);
    return new Promise<Application>((resolve, reject) =>
      this.authHttp.post(
        ApplicationService.APPLICATIONS_URL,
        JSON.stringify({ 'applicationList': [ApplicationMapper.mapFrontend(application)] }))
        .subscribe(
          data => { 
            console.log('ApplicationService.addApplication processing', data.json());
            console.log('ApplicationService.addApplication map', data.json()[0]);
            let appl = ApplicationMapper.mapBackend(data.json().applicationList[0]);
            console.log('Created application', appl);
            resolve(appl);
          },
          err => {
            console.log('ApplicationService.addApplication error', err);
            reject(err);
          },
          () => console.log('Request Complete')
        )
    );
  }

  public updateApplication(application: Application): Promise<Application> {
    console.log('ApplicationService.updateApplication', application);
    return new Promise<Application>((resolve, reject) =>
      this.authHttp.put(
        ApplicationService.APPLICATIONS_URL + '/' + application.id,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .subscribe(
          data => {
            console.log('ApplicationService.updateApplication processing', data.json());
            let appl = ApplicationMapper.mapBackend(data.json());
            console.log('Created application', appl);
            resolve(appl);
          },
          err => {
            console.log('ApplicationService.updateApplication error', err);
            reject(err);
          },
          () => console.log('Request Complete')
        )
    );
  }

}
