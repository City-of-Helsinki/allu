import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute, UrlSegment} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {ProgressStep} from '../progressbar/progressbar.component.ts';
import {ApplicationHub} from '../../service/application/application-hub';
import {UrlUtil} from '../../util/url.util';
import {Application} from '../../model/application/application';
import {ApplicationType} from '../../model/application/type/application-type';
import {applicationCategories, ApplicationCategory, ApplicationCategoryType} from './application-category';
import {translations} from '../../util/translations';

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
  applicationCategories = applicationCategories;
  category: ApplicationCategory;
  typeNames = [];
  translations = translations;
  private typeChangeDisabled = false;
  private progressStep: number;

  constructor(public router: Router, private route: ActivatedRoute) {};

  ngOnInit(): any {
    this.route.data
      .map((data: {application: Application}) => data.application)
      .filter(application => application.id !== undefined)
      .subscribe(application => {
        this.application = application;
        this.category = this.applicationCategories.find(categories => categories.containsType(ApplicationType[application.type]));
        this.typeNames = this.category.applicationTypeNames;
        this.typeChangeDisabled = true;
        this.eventSelection(application.type);
      });

    UrlUtil.urlPathContains(this.route, 'summary').forEach(summary => {
      this.progressStep = summary ? ProgressStep.SUMMARY : ProgressStep.INFORMATION;
    });
  };

  typeSelection(value: string) {
    this.category = applicationCategories.find(c => c.categoryType === ApplicationCategoryType[value]);
    this.typeNames = this.category.applicationTypeNames;
  };

  eventSelection(value) {
    this.router.navigate([value], {skipLocationChange: true, relativeTo: this.route});
  };

}
