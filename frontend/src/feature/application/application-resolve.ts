import {Injectable} from '@angular/core';
import {Router, Resolve, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Application} from '../../model/application/application';
import {ApplicationHub} from '../../service/application/application-hub';
import {LocationState} from '../../service/application/location-state';

@Injectable()
export class ApplicationResolve implements Resolve<Application> {
  constructor(private applicationHub: ApplicationHub, private locationState: LocationState) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Application> {
    let id = Number(route.params['id']);

    if (id) {
      return this.applicationHub.getApplication(id);
    } else {
      return Observable.of(this.locationState.createApplication());
    }
  }
}
