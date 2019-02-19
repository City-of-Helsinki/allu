import {Component, OnInit} from '@angular/core';
import {AuthService} from '@service/authorization/auth.service';
import {ConfigService} from '@service/config/config.service';
import {Observable} from 'rxjs';
import {EnvironmentType} from '@model/config/environment-type';
import {Store} from '@ngrx/store';
import * as fromRoot from '../allu/reducers';
import * as fromProject from '../project/reducers';
import {map} from 'rxjs/internal/operators';
import {ResetLayers} from '@feature/map/actions/map-layer-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Router} from '@angular/router';
import {ApplicationStore} from '@service/application/application-store';

@Component({
  selector: 'toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: [
    './toolbar.component.scss'
  ]
})
export class ToolbarComponent implements OnInit {
  version: Observable<string>;
  logo: string;

  applicationBasketSize$: Observable<number>;

  constructor(private router: Router,
              private authService: AuthService,
              private config: ConfigService,
              private store: Store<fromRoot.State>,
              private applicationStore: ApplicationStore) {
  }

  ngOnInit(): void {
    this.version = this.config.getConfiguration().pipe(
      map(config => config.versionNumber)
    );

    this.config.getConfiguration().subscribe(config =>
      this.logo = config.environment === EnvironmentType.PRODUCTION
        ? 'assets/svg/allu-logo.svg'
        : 'assets/svg/allu-testi-logo.svg');

    this.applicationBasketSize$ = this.store.select(fromProject.getApplicationCountInBasket);
  }

  get authenticated() {
    return this.authService.authenticated();
  }

  newApplication() {
    this.applicationStore.reset();
    this.store.dispatch(new ResetLayers(ActionTargetType.Location));
    this.router.navigate(['/applications/location']);
  }
}
