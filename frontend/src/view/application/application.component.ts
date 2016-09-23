import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import {ProgressStep, ProgressMode, ProgressbarComponent} from '../../component/progressbar/progressbar.component';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {Event} from '../../event/event';

@Component({
  selector: 'application',
  viewProviders: [],
  template: require('./application.component.html'),
  styles: [
    require('./application.component.scss')
  ],
  directives: [
    ProgressbarComponent
  ]
})
export class ApplicationComponent implements OnInit {
  public applications: any;
  private types: string;
  private subtypes: any;
  private subtype: string;
  private progressStep: number;
  private progressMode: number;

  constructor(public router: Router, private route: ActivatedRoute) {
    this.applications = [
      {
        name: 'Katutyö',
        value: 'Street',
        subtypes: [
          {name: 'Kaivuilmoitus', value: 'promotion-event'},
          {name: 'Aluevuokraus', value: 'promotion-event'},
          {name: 'Tilapäiset liikennejärjestelyt', value: 'promotion-event'}
        ]
      },
      {
        name: 'Tapahtuma',
        value: 'Event',
        subtypes: [
          {name: 'Promootio', value: 'promotion-event'},
          {name: 'Ulkoilmatapahtuma', value: 'outdoor-event'},
          {name: 'Vaalit', value: 'promotion-event'}
        ]
      }];

    this.types = undefined;
    this.subtypes = undefined;
    this.subtype = undefined;

    this.progressStep = ProgressStep.INFORMATION;
    this.progressMode = ProgressMode.NEW;
  };

  ngOnInit(): any {
    console.log(this.route.url);

    let routeName = 'Type';
    if (routeName !== 'Type') {
      this.applications
        .filter(application => application.subtypes.some(subtype => subtype.value === routeName))
        .forEach(application => {
          this.types = application.value;
          this.subtypes = application.subtypes;
          this.subtype = routeName;
        });
    }
  };

  typeSelection(value) {
    this.subtype = undefined;
    this.subtypes = this.applications
      .filter(application => value === application.value)
      .map(application => application.subtypes)
      .shift();
  };

  eventSelection(value) {
    this.router.navigate(['/applications/' + value]);
  };
}
