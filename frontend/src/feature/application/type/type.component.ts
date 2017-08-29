import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';

import {
  ApplicationType,
  hasMultipleKinds,
  kindEntryByTypeAndKind,
  typeEntryByType
} from '../../../model/application/type/application-type';
import {ApplicationKindEntry} from '../../../model/application/type/application-kind';
import {ApplicationState} from '../../../service/application/application-state';
import {EnumUtil} from '../../../util/enum.util';
import {
  fromKindsWithSpecifiers,
  KindsWithSpecifiers,
  SpecifierEntry,
  toKindsWithSpecifiers
} from '../../../model/application/type/application-specifier';

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
  @Output() onKindSpecifierChange = new EventEmitter<KindsWithSpecifiers>();

  multipleKinds = false;
  applicationTypes = EnumUtil.enumValues(ApplicationType);
  availableKinds: string[] = [];
  availableKindsWithSpecifiers: ApplicationKindEntry[] = [];
  form: FormGroup;

  private typeCtrl: FormControl;
  private kindsCtrl: FormControl;
  private specifiersCtrl: FormControl;

  constructor(private applicationState: ApplicationState, private fb: FormBuilder) {
  };

  ngOnInit(): any {
    this.initForm();

    if (this.typeChangeDisabled) {
      this.form.disable();
    }

    this.typeCtrl.valueChanges.subscribe(type => this.typeSelection(type));
    this.kindsCtrl.valueChanges.subscribe(kinds => this.kindSelection(kinds));
    this.specifiersCtrl.valueChanges.subscribe(specifiers => this.onSpecifierSelection(specifiers));
  };

  typeSelection(type: string) {
    this.kindsCtrl.reset([]);
    this.availableKinds = this.getAvailableKinds(type);
    this.multipleKinds = hasMultipleKinds(ApplicationType[type]);
    this.onTypeChange.emit(ApplicationType[type]);
  };

  kindSelection(kinds: string | Array<string>) {
    const selectedKinds = Array.isArray(kinds) ? kinds : [kinds];
    this.availableKindsWithSpecifiers = this.getAvailableSpecifiers(this.typeCtrl.value, selectedKinds);

    if (this.availableKindsWithSpecifiers.length > 0) {
      this.updateSelectedSpecifiers();
    } else {
      const kindsWithSpecifiers = toKindsWithSpecifiers(selectedKinds.map(kind => new SpecifierEntry(undefined, kind)));
      this.onKindSpecifierChange.emit(kindsWithSpecifiers);
    }
  };

  onSpecifierSelection(specifierKeys: Array<string>) {
    const kindsWithSpecifiers = toKindsWithSpecifiers(specifierKeys.map(key => SpecifierEntry.fromKey(key)));
    this.onKindSpecifierChange.emit(kindsWithSpecifiers);
  }

  showSpecifierSelection(): boolean {
    return this.availableKindsWithSpecifiers.length > 0;
  }

  getAvailableKinds(type: string): Array<string> {
    return typeEntryByType(type)
      .map(ts => ts.applicationKindNamesSortedByTranslation)
      .orElse([]);
  }

  getAvailableSpecifiers(applicationType: string, kinds: Array<string>): Array<ApplicationKindEntry> {
    return kinds.map(k => kindEntryByTypeAndKind(applicationType, k))
      .filter(entry => entry.isDefined())
      .map(entry => entry.value())
      .filter(kindEntry => kindEntry.hasSpecifiers());
  }

  private initForm() {
    const application = this.applicationState.application;
    const selectedKinds = application.uiKinds;
    const selectedSpecifiers = fromKindsWithSpecifiers(application.kindsWithSpecifiers);

    this.typeCtrl = this.fb.control(application.type);
    this.availableKinds = this.getAvailableKinds(application.type);
    this.availableKindsWithSpecifiers = this.getAvailableSpecifiers(application.type, selectedKinds);

    this.multipleKinds = hasMultipleKinds(application.typeEnum);
    this.kindsCtrl = this.fb.control(selectedKinds);
    this.specifiersCtrl = this.fb.control(selectedSpecifiers);

    this.form = this.fb.group({
      type: this.typeCtrl,
      kinds: this.kindsCtrl,
      specifiers: this.specifiersCtrl
    });
  }

  private updateSelectedSpecifiers() {
    const remainingSpecifiers = this.specifiersCtrl.value
      .map(key => SpecifierEntry.fromKey(key))
      .filter(se => this.availableKindsWithSpecifiers.some(kindEntry => kindEntry.uiKind === se.kind))
      .map(specifierEntry => specifierEntry.key);

    this.specifiersCtrl.patchValue(remainingSpecifiers);
  }
}
