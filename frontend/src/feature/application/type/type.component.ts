import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {Application} from '../../../model/application/application';
import {ApplicationType, applicationTypes, ApplicationTypeStructure} from '../../../model/application/type/application-type';
import {ApplicationSpecifier} from '../../../model/application/type/application-specifier';
import {Some} from '../../../util/option';
import {ApplicationKind} from '../../../model/application/type/application-kind';

@Component({
  selector: 'application-type',
  template: require('./type.component.html')
})
export class TypeComponent implements OnInit {
  @Input() typeChangeDisabled = false;
  @Output() onTypeChange = new EventEmitter<ApplicationType>();
  @Output() onKindChange = new EventEmitter<ApplicationKind>();
  @Output() onSpecifierChange = new EventEmitter<Array<ApplicationSpecifier>>();

  applicationTypes = applicationTypes;
  type: ApplicationTypeStructure;
  kindNames = [];
  applicationKind: string;
  specifierNames = [];
  selectedSpecifiers: Array<string> = [];

  constructor(private route: ActivatedRoute) {};

  ngOnInit(): any {
    this.route.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.type = this.applicationTypes.find(types => types.containsKind(ApplicationKind[application.kind]));
        this.kindNames = this.type ? this.type.applicationKindNames : [];
        this.applicationKind = application.kind;
        this.kindSelection(application.kind);
        this.selectedSpecifiers = Some(application.extension).map(ext => ext.specifiers).orElse([]);
      });
  };

  typeSelection(value: string) {
    let appType = ApplicationType[value];
    this.type = applicationTypes.find(types => types.type === appType);
    this.kindNames = this.type.applicationKindNames;
    this.onTypeChange.emit(appType);
  };

  kindSelection(value: string) {
    if (value !== undefined) {
      this.applicationKind = value;
      let kind = ApplicationKind[value];
      this.specifierNames = this.type.structureByKind(kind).applicationSpecifierNames;
      this.onKindChange.emit(kind);
    }
  };

  showSpecifierSelection(): boolean {
    let applicationKindSelected = this.applicationKind !== undefined;
    let show = (appType: ApplicationTypeStructure) => appType.typeName === 'CABLE_REPORT' && applicationKindSelected;
    return Some(this.type).map(show).orElse(false);
  }

  set specifiers(values: Array<string>) {
    this.selectedSpecifiers = values;
    this.onSpecifierChange.emit(values.map(v => ApplicationSpecifier[v]));
  }

  get specifiers(): Array<string> {
    return this.selectedSpecifiers;
  }
}
