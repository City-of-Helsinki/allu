import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';
import {ConfigService} from '../../service/config/config.service';
import {EnvironmentType} from '../../model/config/environment-type';

@Component({
  selector: 'allu',
  templateUrl: './allu.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: []
})
export class AlluComponent implements OnInit, OnDestroy {

  toolbarClass: string;
  private routeEventSub: Subscription;

  constructor(private router: Router, private config: ConfigService) {
  }

  ngOnInit(): void {
    this.routeEventSub = this.router.events.subscribe(evt => {
      if (evt instanceof NavigationEnd) {
        window.scrollTo(0, 0);
      }
    });
    this.config.getConfiguration().subscribe(config => {
      return config.environment === EnvironmentType.PRODUCTION
        ? this.toolbarClass = undefined
        : this.toolbarClass = 'stripes';
    });
  }

  ngOnDestroy(): void {
    this.routeEventSub.unsubscribe();
  }
}
