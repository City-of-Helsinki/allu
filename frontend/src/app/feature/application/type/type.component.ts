import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {ApplicationType, getAvailableKinds, getAvailableSpecifiers, hasMultipleKinds} from '@model/application/type/application-type';
import {ApplicationStore} from '@service/application/application-store';
import {ArrayUtil} from '@util/array-util';
import {Observable, of, Subject} from 'rxjs';
import {map, take, takeUntil} from 'rxjs/internal/operators';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers/index';
import * as fromAuth from '@feature/auth/reducers';
import * as fromApplication from '../reducers';
import {
  fromKindsWithSpecifiers,
  hasSpecifiers,
  KindsWithSpecifiers,
  SpecifierEntry,
  toKindsWithSpecifiers
} from '@model/application/type/application-specifier';
import {SetKindsWithSpecifiers, SetType} from '@feature/application/actions/application-actions';
import {Application} from '@model/application/application';
import {ComplexValidator} from '@util/complex-validator';
import {FormUtil} from '@util/form.util';

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
  @Input() receivedTime: Date = new Date();

  @Output() receivedTimeChange: EventEmitter<Date> = new EventEmitter<Date>();
  valid: boolean;

  multipleKinds = false;
  applicationTypes: Observable<string[]>;
  availableKinds: string[] = [];
  availableKindsWithSpecifiers: KindsWithSpecifiers = {};
  pendingKind$: Observable<string>;
  form: UntypedFormGroup;
  today: Date = new Date();

  private typeCtrl: UntypedFormControl;
  private kindsCtrl: UntypedFormControl;
  private specifiersCtrl: UntypedFormControl;
  private draftCtrl: UntypedFormControl;
  private destroy = new Subject<boolean>();

  constructor(private applicationStore: ApplicationStore,
              private store: Store<fromRoot.State>,
              private fb: UntypedFormBuilder) {
  }

  ngOnInit(): any {
    this.store.pipe(
      select(fromApplication.getCurrentApplication),
      take(1)
    ).subscribe(app => {
      this.availableKinds = getAvailableKinds(app.type, !this.readonly);
      this.multipleKinds = hasMultipleKinds(app.type);
      this.initForm(app);
      this.applicationTypes = this.getAvailableTypes().pipe(
        map(types => types.sort(ArrayUtil.naturalSortTranslated(['application.type'], (t: string) => t)))
      );

      this.initEvents();
    });
    this.valid = this.form.valid;
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  typeChange(type: string) {
    this.typeCtrl.patchValue(type, {emitEvent: false});
    this.kindsCtrl.reset([]);
    this.availableKinds = getAvailableKinds(type, !this.readonly);
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

  validate(): void {
    FormUtil.validateFormFields(this.form);
  }

  private initForm(app: Application) {
    const selectedKinds = app.kindsWithSpecifiers ? Object.keys(app.kindsWithSpecifiers) : [];
    const selectedSpecifiers = fromKindsWithSpecifiers(app.kindsWithSpecifiers);

    const kinds = this.multipleKinds ? selectedKinds : ArrayUtil.first(selectedKinds);
    this.typeCtrl = this.fb.control({ value: app.type, disabled: this.typeChangeDisabled });
    this.kindsCtrl = this.fb.control(kinds, Validators.required);
    this.specifiersCtrl = this.fb.control(selectedSpecifiers);
    this.draftCtrl = this.fb.control(this.applicationStore.snapshot.draft);

    this.form = this.fb.group({
      type: this.typeCtrl,
      kinds: this.kindsCtrl,
      specifiers: this.specifiersCtrl,
      draft: this.draftCtrl,
      receivedTime: [this.receivedTime, [Validators.required, ComplexValidator.inTheFuture]]
    });

    this.kindsCtrl.updateValueAndValidity();
    this.setFormMode(app.type);
  }

  private initEvents(): void {
    this.pendingKind$ = this.store.select(fromApplication.getPendingKind);

    this.typeCtrl.valueChanges.pipe(
      takeUntil(this.destroy),
      map((type: string) => ApplicationType[type]),
    ).subscribe(type => this.store.dispatch(new SetType(type)));

    this.store.pipe(
      select(fromApplication.getType),
      takeUntil(this.destroy),
      map((type: ApplicationType) => ApplicationType[type])
    ).subscribe(type => this.typeChange(type));

    this.kindsCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(kinds => this.kindSelection(kinds));

    this.specifiersCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(specifiers => this.specifierSelection(specifiers));

    this.store.pipe(
      select(fromApplication.getKindsWithSpecifiers),
      takeUntil(this.destroy)
    ).subscribe(kws => this.kindsWithSpecifierChange(kws));

    this.draftCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(draft => this.applicationStore.changeDraft(draft));

    this.form.get('receivedTime').valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(date => this.receivedTimeChange.emit(date));

    this.form.statusChanges.pipe(takeUntil(this.destroy))
      .subscribe(status => this.valid = status === 'VALID');
  }

  private getAvailableTypes() {
    if (this.typeChangeDisabled) {
      return of(Object.keys(ApplicationType));
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
