import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute, UrlSegment} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {ProgressStep, ProgressMode} from '../progressbar/progressbar.component.ts';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {Event} from '../../event/event';
import {ApplicationHub} from '../../service/application/application-hub';
import {applicationTypes} from './application-types';
import {UrlUtil} from '../../util/url.util';
import {Application} from '../../model/application/application';
import {ApplicationType} from '../../model/application/type/application-type';

@Component({
  selector: 'application',
  viewProviders: [],
  template: require('./application.component.html'),
  styles: [
    require('./application.component.scss')
  ]
})
export class ApplicationComponent implements OnInit {
  private applicationTypes: any;
  private type: any;
  private subtypes: any;
  private subtype: any;
  private typeChangeDisabled = false;
  private progressStep: number;
  private progressMode: number;

  constructor(public router: Router, private route: ActivatedRoute) {
    this.applicationTypes = applicationTypes;
    this.subtypes = undefined;
    this.subtype = undefined;
    this.progressStep = ProgressStep.INFORMATION;
    this.progressMode = ProgressMode.NEW;
  };

  ngOnInit(): any {
    this.route.data
      .map((data: {application: Application}) => data.application)
      .filter(application => application.id !== undefined)
      .subscribe(application => {
        let type = applicationTypes.find(appType =>
          appType.subtypes.some(subtype => ApplicationType[subtype.type] === application.type));

        this.type = type.value;
        this.subtypes = type.subtypes;
        this.subtype = type.subtypes.find(subtype => ApplicationType[subtype.type] === application.type).value;
        this.typeChangeDisabled = true;
        this.progressMode = ProgressMode.EDIT;
        this.eventSelection(this.subtype);
      });
  };

  typeSelection(value) {
    this.subtype = undefined;
    this.subtypes = applicationTypes.find(type => type.value === value).subtypes;
  };

  eventSelection(value) {
    this.router.navigate([value], {skipLocationChange: true, relativeTo: this.route});
  };
}
