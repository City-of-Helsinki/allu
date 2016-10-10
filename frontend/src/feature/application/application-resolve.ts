import {Injectable} from '@angular/core';
import {Router, Resolve, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Application} from '../../model/application/application';
import {ApplicationHub} from '../../service/application/application-hub';

@Injectable()
export class ApplicationResolve implements Resolve<Application> {
  constructor(private applicationHub: ApplicationHub) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Application> {
    let id = Number(route.firstChild.params['id']);

    if (id) {
      return this.applicationHub.getApplication(id);
    } else {
      return Observable.of(Application.prefilledApplication());
    }
  }
}
