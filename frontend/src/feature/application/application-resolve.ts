import {Injectable} from '@angular/core';
import {Router, Resolve, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Application} from '../../model/application/application';
import {ApplicationHub} from '../../service/application/application-hub';
import {LocationState} from '../../service/application/location-state';
import {Some} from '../../util/option';

@Injectable()
export class ApplicationResolve implements Resolve<Application> {
  constructor(private applicationHub: ApplicationHub, private locationState: LocationState) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Application> {
    let appId = Some(route.params['id']).orElse(route.parent.params['id']);

    return Some(appId)
      .map(id => Number(id))
      .map(id => this.applicationHub.getApplication(id))
      .orElse(Observable.of(this.locationState.createApplication()));
  }
}
