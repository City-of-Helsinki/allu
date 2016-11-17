import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {Application} from '../../../model/application/application';
import {ApplicationType} from '../../../model/application/type/application-type';
import {applicationCategories, ApplicationCategory, ApplicationCategoryType} from './application-category';
import {translations} from '../../../util/translations';
import {ApplicationSpecifier} from '../../../model/application/type/application-specifier';
import {Some} from '../../../util/option';

@Component({
  selector: 'application-type',
  template: require('./type.component.html')
})
export class TypeComponent implements OnInit {
  @Input() typeChangeDisabled = false;
  @Output() onCategoryChange = new EventEmitter<ApplicationCategoryType>();
  @Output() onTypeChange = new EventEmitter<ApplicationType>();
  @Output() onSpecifierChange = new EventEmitter<Array<ApplicationSpecifier>>();

  applicationCategories = applicationCategories;
  category: ApplicationCategory;
  typeNames = [];
  applicationType: string;
  specifierNames = [];
  selectedSpecifiers: Array<string> = [];
  translations = translations;

  constructor(private route: ActivatedRoute) {};

  ngOnInit(): any {
    this.route.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.category = this.applicationCategories.find(categories => categories.containsType(ApplicationType[application.type]));
        this.typeNames = this.category ? this.category.applicationTypeNames : [];
        this.applicationType = application.type;
        this.selectedSpecifiers = application.specifiers;
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
    if (value !== undefined) {
      this.applicationType = value;
      let type = ApplicationType[value];
      this.specifierNames = this.category.structureByType(type).applicationSpecifierNames;
      this.onTypeChange.emit(type);
    }
  };

  showSpecifierSelection(): boolean {
    let applicationTypeSelected = this.applicationType !== undefined;
    let show = (c: ApplicationCategory) => c.categoryTypeName === 'CABLE_REPORT' && applicationTypeSelected;
    return Some(this.category).map(show).orElse(false);
  }

  set specifiers(values: Array<string>) {
    this.selectedSpecifiers = values;
    this.onSpecifierChange.emit(values.map(v => ApplicationSpecifier[v]));
  }

  get specifiers(): Array<string> {
    return this.selectedSpecifiers;
  }
}
