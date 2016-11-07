import {Component, OnDestroy, OnInit} from '@angular/core';

import {Application} from '../../../../model/application/application';
import {LocationState} from '../../../../service/application/location-state';


@Component({
  selector: 'promotion-event',
  viewProviders: [],
  template: require('./promotion-event.component.html'),
  styles: []
})

export class PromotionEventComponent implements OnInit, OnDestroy {
  private application: Application;
  private events: Array<any>;
  private applicantType: Array<string>;
  private countries: Array<any>;
  private billingTypes: Array<any>;
  private noPriceReasons: Array<any>;

  constructor(locationState: LocationState) {
    this.application = Application.prefilledApplication();
    this.application.location = locationState.location;
  };

  ngOnInit(): any {
  }

  ngOnDestroy(): any {
  }
}
