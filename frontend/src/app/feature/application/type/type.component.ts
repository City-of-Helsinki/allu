import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {
  ApplicationType,
  hasMultipleKinds,
  kindEntryByTypeAndKind,
  typeEntryByType
} from '../../../model/application/type/application-type';
import {ApplicationKindEntry} from '../../../model/application/type/application-kind';
import {ApplicationStore} from '../../../service/application/application-store';
import {
  fromKindsWithSpecifiers,
  KindsWithSpecifiers,
  SpecifierEntry,
  toKindsWithSpecifiers
} from '../../../model/application/type/application-specifier';
import {EnumUtil} from '../../../util/enum.util';
import {ArrayUtil} from '../../../util/array-util';
import {Observable, of, Subject} from 'rxjs';
import {filter, map, takeUntil} from 'rxjs/internal/operators';
import {Store} from '@ngrx/store';
import * as fromAuth from '../../auth/reducers';

@Component({
  selector: 'application-type',
  templateUrl: './type.component.html',
  styleUrls: [
    './type.component.scss'
  ]
})
export class TypeComponent implements OnInit, OnDestroy {
  @Input() readonly  = false;
  @Input() typeChangeDisabled = false;
  @Input() showDraftSelection = false;
  @Output() onTypeChange = new EventEmitter<ApplicationType>();
  @Output() onKindSpecifierChange = new EventEmitter<KindsWithSpecifiers>();

  multipleKinds = false;
  applicationTypes: Observable<string[]>;
  availableKinds: string[] = [];
  availableKindsWithSpecifiers: ApplicationKindEntry[] = [];
  form: FormGroup;

  private typeCtrl: FormControl;
  private kindsCtrl: FormControl;
  private specifiersCtrl: FormControl;
  private draftCtrl: FormControl;
  private destroy = new Subject<boolean>();

  constructor(private applicationStore: ApplicationStore,
              private store: Store<fromAuth.State>,
              private fb: FormBuilder) {
  }

  ngOnInit(): any {
    this.initForm();
    this.applicationTypes = this.getAvailableTypes()
      .pipe(map(types => types.sort(ArrayUtil.naturalSortTranslated(['application.type'], (type: string) => type))));

    this.kindsCtrl.updateValueAndValidity();

    if (this.readonly) {
      this.form.disable();
    }

    this.typeCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(type => this.typeSelection(type));

    this.kindsCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(kinds => this.kindSelection(kinds));

    this.specifiersCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(specifiers => this.onSpecifierSelection(specifiers));

    this.draftCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(draft => this.applicationStore.changeDraft(draft));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  typeSelection(type: string) {
    this.kindsCtrl.reset([]);
    this.availableKinds = this.getAvailableKinds(type);
    this.multipleKinds = hasMultipleKinds(ApplicationType[type]);
    this.onTypeChange.emit(ApplicationType[type]);
  }

  kindSelection(kinds: string | Array<string>) {
    const selectedKinds = Array.isArray(kinds) ? kinds : [kinds];
    this.availableKindsWithSpecifiers = this.getAvailableSpecifiers(this.typeCtrl.value, selectedKinds);

    if (this.availableKindsWithSpecifiers.length > 0) {
      this.updateSelectedSpecifiers();
    } else {
      const kindsWithSpecifiers = toKindsWithSpecifiers(selectedKinds.map(kind => new SpecifierEntry(undefined, kind)));
      this.onKindSpecifierChange.emit(kindsWithSpecifiers);
    }
  }

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
    const application = this.applicationStore.snapshot.application;
    const selectedKinds = application.uiKinds;
    const selectedSpecifiers = fromKindsWithSpecifiers(application.kindsWithSpecifiers);

    this.typeCtrl = this.fb.control({ value: application.type, disabled: this.typeChangeDisabled });
    this.availableKinds = this.getAvailableKinds(application.type);
    this.availableKindsWithSpecifiers = this.getAvailableSpecifiers(application.type, selectedKinds);

    this.multipleKinds = hasMultipleKinds(application.typeEnum);
    const kinds = this.multipleKinds ? selectedKinds : ArrayUtil.first(selectedKinds);
    this.kindsCtrl = this.fb.control({ value: kinds, disabled: this.typeChangeDisabled });
    this.specifiersCtrl = this.fb.control({ value: selectedSpecifiers, disabled: this.typeChangeDisabled });

    this.draftCtrl = this.fb.control(this.applicationStore.snapshot.draft);

    this.form = this.fb.group({
      type: this.typeCtrl,
      kinds: this.kindsCtrl,
      specifiers: this.specifiersCtrl,
      draft: this.draftCtrl
    });
  }

  private getAvailableTypes() {
    if (this.typeChangeDisabled) {
      return of(EnumUtil.enumValues(ApplicationType));
    } else {
      return this.store.select(fromAuth.getAllowedApplicationTypes);
    }
  }

  private updateSelectedSpecifiers() {
    const remainingSpecifiers = this.specifiersCtrl.value
      .map(key => SpecifierEntry.fromKey(key))
      .filter(se => this.availableKindsWithSpecifiers.some(kindEntry => kindEntry.uiKind === se.kind))
      .map(specifierEntry => specifierEntry.key);

    this.specifiersCtrl.patchValue(remainingSpecifiers);
  }
}
