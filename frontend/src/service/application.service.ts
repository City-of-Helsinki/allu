import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {URLSearchParams} from '@angular/http';
import {Application} from '../model/application/application';
import {ApplicationMapper} from './mapper/application-mapper';

@Injectable()
export class ApplicationService {

  constructor(private authHttp: AuthHttp) {}

  public listApplications(handler: string): Promise<Array<Application>> {
    let params = new URLSearchParams();
    params.set('handler', handler);

    return new Promise<Array<Application>>((resolve, reject) =>
      this.authHttp.get('/api/applications', { search: params }).subscribe(
        data => {
          let applications: Array<Application> = data.json().applicationList.map((p) => ApplicationMapper.mapBackend(p));
          console.log('Listing applications', applications);
          resolve(applications);
        },
        err => console.log(err),
        () => console.log('Request Complete')
      )
    );
  }

  public addApplication(application: Application): Promise<Application> {
    return new Promise<Application>((resolve, reject) =>
      this.authHttp.post('/api/applications', JSON.stringify({ 'applicationList': [ApplicationMapper.mapFrontend(application)] }))
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
}
