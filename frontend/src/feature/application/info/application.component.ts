import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {ProgressStep} from '../../progressbar/progressbar.component.ts';
import {UrlUtil} from '../../../util/url.util.ts';
import {ApplicationType} from '../../../model/application/type/application-type';
import {Application} from '../../../model/application/application';
import {ApplicationState} from '../../../service/application/application-state';

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

  constructor(private route: ActivatedRoute, private router: Router, private applicationState: ApplicationState) {
  }

  ngOnInit(): void {
    this.application = this.applicationState.application;
    this.verifyTypeExists(ApplicationType[this.application.type]);

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
