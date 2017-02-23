import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {
  ApplicationType,
  typeStructureByType,
  kindStructureByTypeAndKind
} from '../../../model/application/type/application-type';
import {ApplicationSpecifier} from '../../../model/application/type/application-specifier';
import {Some} from '../../../util/option';
import {ApplicationKind} from '../../../model/application/type/application-kind';
import {ApplicationState} from '../../../service/application/application-state';
import {EnumUtil} from '../../../util/enum.util';

@Component({
  selector: 'application-type',
  template: require('./type.component.html'),
  styles: [
    require('./type.component.scss')
  ]
})
export class TypeComponent implements OnInit {
  @Input() typeChangeDisabled = false;
  @Output() onTypeChange = new EventEmitter<ApplicationType>();
  @Output() onKindChange = new EventEmitter<ApplicationKind>();
  @Output() onSpecifierChange = new EventEmitter<Array<ApplicationSpecifier>>();

  applicationTypes = EnumUtil.enumValues(ApplicationType);
  selectableKinds = [];
  selectableSpecifiers = [];
  form: FormGroup;

  constructor(private applicationState: ApplicationState, private fb: FormBuilder) {
  };

  ngOnInit(): any {
    let application = this.applicationState.application;
    this.form = this.fb.group({
      type: [application.type],
      kind: [application.kind],
      specifiers: [Some(application.extension).map(ext => ext.specifiers).orElse([])]
    });

    this.selectableKinds = this.getSelectableKinds();
    this.selectableSpecifiers = this.getSelectableSpecifiers();

    if (this.typeChangeDisabled) {
      this.form.disable();
    }

    this.form.get('type').valueChanges.subscribe(type => this.typeSelection(type));
    this.form.get('kind').valueChanges.subscribe(kind => this.kindSelection(kind));
    this.form.get('specifiers').valueChanges.subscribe(specifiers => {
      this.onSpecifierChange.emit(specifiers.map(s => ApplicationSpecifier[s]));
    });
  };

  typeSelection(value: string) {
    let appType = ApplicationType[value];
    this.form.patchValue({kind: undefined}, {onlySelf: true});
    this.selectableKinds = this.getSelectableKinds();
    this.onTypeChange.emit(appType);
  };

  kindSelection(value: string) {
    let kind = ApplicationKind[value];
    this.form.patchValue({specifiers: []}, {onlySelf: true});
    this.selectableSpecifiers = this.getSelectableSpecifiers();
    this.onKindChange.emit(kind);
  };

  showSpecifierSelection(): boolean {
    let applicationKindSelected = this.form.value.kind !== undefined;
    let show = (appType: string) => this.specifierSelectionShownForType(ApplicationType[appType])
      && applicationKindSelected;
    return Some(this.form.value.type).map(show).orElse(false);
  }

  getSelectableKinds(): Array<string> {
    return typeStructureByType(this.form.value.type)
      .map(ts => ts.applicationKindNamesSortedByTranslation)
      .orElse([]);
  }

  getSelectableSpecifiers(): Array<string> {
    return kindStructureByTypeAndKind(this.form.value.type, this.form.value.kind)
      .map(ts => ts.applicationSpecifierNamesSortedByTranslation)
      .orElse([]);
  }

  private specifierSelectionShownForType(type: ApplicationType): boolean {
    return [
        ApplicationType.CABLE_REPORT,
        ApplicationType.EXCAVATION_ANNOUNCEMENT,
        ApplicationType.PLACEMENT_CONTRACT
      ].indexOf(type) >= 0;
  }
}
