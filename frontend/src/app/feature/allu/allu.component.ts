import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';

@Component({
  selector: 'allu',
  templateUrl: './allu.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: []
})
export class AlluComponent implements OnInit, OnDestroy {

  private routeEventSub: Subscription;

  constructor(private router: Router) {
  }

  ngOnInit(): void {
    this.routeEventSub = this.router.events.subscribe(evt => {
      if (evt instanceof NavigationEnd) {
        window.scrollTo(0, 0);
      }
    });
  }

  ngOnDestroy(): void {
    this.routeEventSub.unsubscribe();
  }
}
