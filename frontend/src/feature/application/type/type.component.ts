import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {Router, ActivatedRoute, UrlSegment} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {ProgressStep} from '../../progressbar/progressbar.component.ts';
import {ApplicationHub} from '../../../service/application/application-hub';
import {UrlUtil} from '../../../util/url.util';
import {Application} from '../../../model/application/application';
import {ApplicationType} from '../../../model/application/type/application-type';
import {applicationCategories, ApplicationCategory, ApplicationCategoryType} from './application-category';
import {translations} from '../../../util/translations';

@Component({
  selector: 'application-type',
  template: require('./type.component.html')
})
export class TypeComponent implements OnInit {
  @Input() typeChangeDisabled = false;
  @Output() onCategoryChange = new EventEmitter<ApplicationCategoryType>();
  @Output() onTypeChange = new EventEmitter<ApplicationType>();

  applicationCategories = applicationCategories;
  category: ApplicationCategory;
  typeNames = [];
  applicationType: string;
  translations = translations;

  constructor(private route: ActivatedRoute) {};

  ngOnInit(): any {
    this.route.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.category = this.applicationCategories.find(categories => categories.containsType(ApplicationType[application.type]));
        this.typeNames = this.category ? this.category.applicationTypeNames : [];
        this.applicationType = application.type;
        this.eventSelection(application.type);
      });
  };

  typeSelection(value: string) {
    let categoryType = ApplicationCategoryType[value];
    this.category = applicationCategories.find(c => c.categoryType === categoryType);
    this.typeNames = this.category.applicationTypeNames;
    this.onCategoryChange.emit(categoryType);
  };

  eventSelection(value: string) {
    this.applicationType = value;
    this.onTypeChange.emit(ApplicationType[value]);
  };
}
