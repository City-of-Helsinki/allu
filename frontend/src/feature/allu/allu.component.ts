import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {ViewEncapsulation} from '@angular/core';
import {Login} from '../../feature/login/login.component';
import {SearchService} from '../../service/search.service';
import {GeolocationService} from '../../service/geolocation.service';
import {ApplicationService} from '../../service/application/application.service.ts';
import {DecisionService} from '../../service/decision/decision.service';
import {AuthGuard} from '../../feature/login/auth-guard.service';

@Component({
  selector: 'allu',
  viewProviders: [],
  template: require('./allu.component.html'),
  encapsulation: ViewEncapsulation.None,
  styles: [
    require('../../assets/main.scss')
  ]
})
export class AlluComponent {
  constructor(
    public router: Router,
    private geolocationService: GeolocationService,
    private applicationService: ApplicationService,
    private decisionService: DecisionService) {}
}
