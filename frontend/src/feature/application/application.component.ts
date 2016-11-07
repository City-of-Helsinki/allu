import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {ProgressStep} from '../progressbar/progressbar.component.ts';
import {UrlUtil} from '../../util/url.util';

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

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    UrlUtil.urlPathContains(this.route, 'summary').forEach(summary => {
      this.progressStep = summary ? ProgressStep.SUMMARY : ProgressStep.INFORMATION;
    });
  }
}
