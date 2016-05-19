import {Component} from 'angular2/core';
import {FORM_DIRECTIVES} from 'angular2/common';

import {MATERIAL_DIRECTIVES, MATERIAL_PROVIDERS} from 'ng2-material/all';

import {ToolbarComponent} from '../../component/toolbar/toolbar.component';

import {MapComponent} from '../../component/map/map.component';
import {WorkqueueComponent} from '../../component/workqueue/workqueue.component';
import {InfobarComponent} from '../../component/infobar/infobar.component';

@Component({
  selector: 'application',
  viewProviders: [],
  moduleId: module.id,
  template: require('./application.component.html'),
  styles: [
    require('./application.component.scss')
  ],
  directives: [MATERIAL_DIRECTIVES, InfobarComponent, MapComponent, WorkqueueComponent]
})

export class ApplicationComponent {
  
  user = {
    title: 'Developer',
    email: 'ipsum@lorem.com',
    firstName: '',
    lastName: '',
    company: 'Google',
    address: '1600 Amphitheatre Pkwy',
    address2: '',
    city: 'Mountain View',
    state: 'CA',
    biography: 'Loves kittens, snowboarding, and can type at 130 WPM.\n\nAnd rumor has it she bouldered up Castle Craig!',
    postalCode: '94043'
  };
  states = [
    'AL', 'AK', 'AZ', 'AR', 'CA', 'CO', 'CT', 'DE', 'FL', 'GA', 'HI', 'ID', 'IL', 'IN', 'IA', 'KS', 'KY', 'LA', 'ME',
    'MD', 'MA', 'MI', 'MN', 'MS', 'MO', 'MT', 'NE', 'NV', 'NH', 'NJ', 'NM', 'NY', 'NC', 'ND', 'OH', 'OK', 'OR', 'PA',
    'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VT', 'VA', 'WA', 'WV', 'WI', 'WY'
  ].map(function (state) {
    return {abbrev: state};
  })
}
