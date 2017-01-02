import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {ProgressStep} from '../../progressbar/progressbar.component.ts';
import {UrlUtil} from '../../../util/url.util.ts';
import {ApplicationType} from '../../../model/application/type/application-type';
import {Application} from '../../../model/application/application';

@Component({
  selector: 'application',
  viewProviders: [],
  template: require('./application.component.html'),
  styles: [
    require('./application.component.scss')
  ]
})
export class ApplicationComponent implements OnInit {
  progressStep: ProgressStep;
  application: Application;

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.route.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.application = application;
        this.verifyTypeExists(ApplicationType[application.type]);
      });

    UrlUtil.urlPathContains(this.route, 'summary').forEach(summary => {
      this.progressStep = summary ? ProgressStep.SUMMARY : ProgressStep.INFORMATION;
    });
  }

  verifyTypeExists(type: ApplicationType) {
    if (type === undefined) {
      // No known type so navigate back to type selection
      this.router.navigateByUrl('applications/location');
    }
  }
}
