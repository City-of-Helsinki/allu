import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {
  ApplicationType,
  applicationTypeTree,
  getAvailableSpecifiers,
  hasMultipleKinds,
} from '../../../model/application/type/application-type';
import {ApplicationStore} from '../../../service/application/application-store';
import {EnumUtil} from '../../../util/enum.util';
import {ArrayUtil} from '../../../util/array-util';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {map, take, takeUntil} from 'rxjs/internal/operators';
import {Store} from '@ngrx/store';
import * as fromAuth from '../../auth/reducers';
import * as fromApplication from '../reducers';
import {
  fromKindsWithSpecifiers,
  hasSpecifiers,
  KindsWithSpecifiers,
  SpecifierEntry,
  toKindsWithSpecifiers
} from '../../../model/application/type/application-specifier';
import {Some} from '../../../util/option';
import {SetKindsWithSpecifiers, SetType} from '../actions/application-actions';

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

  multipleKinds = false;
  applicationTypes: Observable<string[]>;
  availableKinds: string[] = [];
  availableKindsWithSpecifiers: KindsWithSpecifiers = {};
  pendingKind$: Observable<string>;
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
    combineLatest(
      this.store.select(fromApplication.getType),
      this.store.select(fromApplication.getKindsWithSpecifiers)
    ).pipe(
      take(1)
    ).subscribe(([type, kindsWithSpecifiers]) => {
      const typeName = ApplicationType[type];
      const kinds = kindsWithSpecifiers ? Object.keys(kindsWithSpecifiers) : [];
      this.availableKinds = this.getAvailableKinds(typeName);
      this.multipleKinds = hasMultipleKinds(typeName);
      this.availableKindsWithSpecifiers = getAvailableSpecifiers(typeName, kinds);

      this.initForm(typeName, kinds, kindsWithSpecifiers);
      this.applicationTypes = this.getAvailableTypes()
        .pipe(map(types => types.sort(ArrayUtil.naturalSortTranslated(['application.type'], (t: string) => t))));

      this.initEvents();
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  typeSelection(type: string) {
    this.kindsCtrl.reset([]);
    this.availableKinds = this.getAvailableKinds(type);
    this.multipleKinds = hasMultipleKinds(type);
    this.store.dispatch(new SetType(ApplicationType[type]));
  }

  kindSelection(kinds: string | Array<string>) {
    const selectedKinds = Array.isArray(kinds) ? kinds : [kinds];
    const kindsWithSpecifiers = getAvailableSpecifiers(this.typeCtrl.value, selectedKinds);
    this.availableKindsWithSpecifiers = kindsWithSpecifiers;

    if (hasSpecifiers(kindsWithSpecifiers)) {
      this.updateSelectedSpecifiers();
    }
    this.store.dispatch(new SetKindsWithSpecifiers(kindsWithSpecifiers));
  }

  onSpecifierSelection(specifierKeys: Array<string>) {
    const kindsWithSpecifiers = toKindsWithSpecifiers(specifierKeys.map(key => SpecifierEntry.fromKey(key)));
    this.store.dispatch(new SetKindsWithSpecifiers(kindsWithSpecifiers));
  }

  showSpecifierSelection(): boolean {
    return hasSpecifiers(this.availableKindsWithSpecifiers);
  }

  getAvailableKinds(type: string): Array<string> {
    return Some(applicationTypeTree[type])
      .map(typeTree => Object.keys(typeTree))
      .map(kinds => kinds.sort(ArrayUtil.naturalSortTranslated(['application.kind'], (kind: string) => kind)))
      .orElse([]);
  }

  private initForm(type: string, selectedKinds: string[], kindsWithSpecifiers: KindsWithSpecifiers) {
    const selectedSpecifiers = fromKindsWithSpecifiers(kindsWithSpecifiers);

    const kinds = this.multipleKinds ? selectedKinds : ArrayUtil.first(selectedKinds);
    this.typeCtrl = this.fb.control({ value: type, disabled: this.typeChangeDisabled });
    this.kindsCtrl = this.fb.control(kinds, Validators.required);
    this.specifiersCtrl = this.fb.control(selectedSpecifiers);
    this.draftCtrl = this.fb.control(this.applicationStore.snapshot.draft);

    this.form = this.fb.group({
      type: this.typeCtrl,
      kinds: this.kindsCtrl,
      specifiers: this.specifiersCtrl,
      draft: this.draftCtrl
    });

    this.kindsCtrl.updateValueAndValidity();
    this.setFormMode(ApplicationType[type]);
  }

  private initEvents(): void {
    this.pendingKind$ = this.store.select(fromApplication.getPendingKind);

    this.typeCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(type => this.typeSelection(type));

    this.kindsCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(kinds => this.kindSelection(kinds));

    this.specifiersCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(specifiers => this.onSpecifierSelection(specifiers));

    this.draftCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(draft => this.applicationStore.changeDraft(draft));
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
      .filter(se => this.availableKinds.indexOf(se.kind) >= 0)
      .map(specifierEntry => specifierEntry.key);

    this.specifiersCtrl.patchValue(remainingSpecifiers);
  }

  private setFormMode(type: ApplicationType): void {
    // Disallow kind change afterwards for events
    // since they have different forms for different kinds
    if (ApplicationType.EVENT === type) {
      this.kindsCtrl.disable();
    }

    if (this.readonly) {
      this.form.disable();
    }
  }
}
