import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute, UrlSegment} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {ProgressStep} from '../progressbar/progressbar.component.ts';
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
  application: Application;
  private applicationTypes: any;
  private type: any;
  private subtypes: any;
  private subtype: any;
  private typeChangeDisabled = false;
  private progressStep: number;

  constructor(public router: Router, private route: ActivatedRoute) {
    this.applicationTypes = applicationTypes;
    this.subtypes = undefined;
    this.subtype = undefined;
  };

  ngOnInit(): any {
    this.route.data
      .map((data: {application: Application}) => data.application)
      .filter(application => application.id !== undefined)
      .subscribe(application => {
        this.application = application;
        let type = applicationTypes.find(appType =>
          appType.subtypes.some(subtype => ApplicationType[subtype.type] === application.type));

        this.type = type.value;
        this.subtypes = type.subtypes;
        this.subtype = type.subtypes.find(subtype => ApplicationType[subtype.type] === application.type).value;
        this.typeChangeDisabled = true;
        this.eventSelection(this.subtype);
      });

    UrlUtil.urlPathContains(this.route, 'summary').forEach(summary => {
      this.progressStep = summary ? ProgressStep.SUMMARY : ProgressStep.INFORMATION;
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
