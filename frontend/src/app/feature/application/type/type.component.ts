import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {
  ApplicationType,
  getAvailableKinds,
  getAvailableSpecifiers,
  hasMultipleKinds
} from '../../../model/application/type/application-type';
import {ApplicationStore} from '../../../service/application/application-store';
import {EnumUtil} from '../../../util/enum.util';
import {ArrayUtil} from '../../../util/array-util';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {map, take, takeUntil} from 'rxjs/internal/operators';
import {Store} from '@ngrx/store';
import * as fromRoot from '../../allu/reducers';
import * as fromAuth from '../../auth/reducers';
import * as fromApplication from '../reducers';
import {
  fromKindsWithSpecifiers,
  hasSpecifiers,
  KindsWithSpecifiers,
  SpecifierEntry,
  toKindsWithSpecifiers
} from '../../../model/application/type/application-specifier';
import {SetKindsWithSpecifiers, SetType} from '../actions/application-actions';
import {InformationAcceptanceModalEvents} from '../../information-request/acceptance/information-acceptance-modal-events';

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
              private store: Store<fromRoot.State>,
              private fb: FormBuilder,
              private modalEvents: InformationAcceptanceModalEvents) {
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
      this.availableKinds = getAvailableKinds(typeName);
      this.multipleKinds = hasMultipleKinds(typeName);

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

  typeChange(type: string) {
    this.typeCtrl.patchValue(type, {emitEvent: false});
    this.kindsCtrl.reset([]);
    this.availableKinds = getAvailableKinds(type);
    this.multipleKinds = hasMultipleKinds(type);
  }

  kindsWithSpecifierChange(kindsWithSpecifiers: KindsWithSpecifiers) {
    const kinds = kindsWithSpecifiers ? Object.keys(kindsWithSpecifiers) : [];
    const selectedKinds = this.multipleKinds ? kinds : ArrayUtil.first(kinds);
    const selectedSpecifiers = fromKindsWithSpecifiers(kindsWithSpecifiers);

    this.availableKindsWithSpecifiers = getAvailableSpecifiers(this.typeCtrl.value, kinds);
    this.form.patchValue({
      kinds: selectedKinds,
      specifiers: selectedSpecifiers
    }, {emitEvent: false});
  }

  kindSelection(kinds: string | Array<string>) {
    const selectedKinds = Array.isArray(kinds) ? kinds : [kinds];
    const remaining = this.getRemainingKindsWithSpecifiers();
    const selected = this.createSelection(selectedKinds, remaining);
    this.store.dispatch(new SetKindsWithSpecifiers(selected));
  }

  specifierSelection(specifierKeys: Array<string>) {
    const kinds = Array.isArray(this.kindsCtrl.value) ? this.kindsCtrl.value : [this.kindsCtrl.value];
    const kindsWithSpecifiers = toKindsWithSpecifiers(specifierKeys.map(key => SpecifierEntry.fromKey(key)));
    const selected = this.createSelection(kinds, kindsWithSpecifiers);
    this.store.dispatch(new SetKindsWithSpecifiers(selected));
  }

  showSpecifierSelection(): boolean {
    return hasSpecifiers(this.availableKindsWithSpecifiers);
  }

  showPending(): void {
    this.modalEvents.open();
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

    this.typeCtrl.valueChanges.pipe(
      takeUntil(this.destroy),
      map((type: string) => ApplicationType[type]),
    ).subscribe(type => this.store.dispatch(new SetType(type)));

    this.store.select(fromApplication.getType).pipe(
      takeUntil(this.destroy),
      map((type: ApplicationType) => ApplicationType[type])
    ).subscribe(type => this.typeChange(type));

    this.kindsCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(kinds => this.kindSelection(kinds));

    this.specifiersCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(specifiers => this.specifierSelection(specifiers));

    this.store.select(fromApplication.getKindsWithSpecifiers).pipe(
      takeUntil(this.destroy)
    ).subscribe(kws => this.kindsWithSpecifierChange(kws));

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

  private getRemainingKindsWithSpecifiers() {
    const remaining =  this.specifiersCtrl.value
      .map(key => SpecifierEntry.fromKey(key))
      .filter(se => this.availableKinds.indexOf(se.kind) >= 0);
    return toKindsWithSpecifiers(remaining);
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

  private createSelection(kinds: string[], specifiers: KindsWithSpecifiers): KindsWithSpecifiers {
    return kinds.reduce((kws, kind) => {
      kws[kind] = specifiers[kind] || [];
      return kws;
    }, {});
  }
}
