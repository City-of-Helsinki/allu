import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {
  hasSpecifiers,
  KindsWithSpecifiers,
  SpecifierEntry,
  toKindsWithSpecifiers
} from '../../../model/application/type/application-specifier';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {getAvailableKinds, getAvailableSpecifiers, hasMultipleKinds} from '../../../model/application/type/application-type';
import {takeUntil} from 'rxjs/internal/operators';
import {Subject} from 'rxjs/index';
import {Store} from '@ngrx/store';
import * as fromApplication from '../../application/reducers';
import {SetKindsWithSpecifiers} from '../actions/information-request-result-actions';

@Component({
  selector: 'kind-acceptance',
  templateUrl: './kind-acceptance.component.html',
  styleUrls: ['./field-acceptance.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class KindAcceptanceComponent implements OnInit {
  @Input() applicationType: string;
  @Input() parentForm: FormGroup;
  @Input() oldValues: KindsWithSpecifiers;
  @Input() newValues: string;
  @Input() readonly: boolean;

  multipleKinds = false;
  availableKinds: string[] = [];
  availableKindsWithSpecifiers: KindsWithSpecifiers = {};
  form: FormGroup;

  private kindsCtrl: FormControl;
  private specifiersCtrl: FormControl;
  private destroy = new Subject<boolean>();

  constructor(private fb: FormBuilder,
              private store: Store<fromApplication.State>) {
  }

  ngOnInit(): void {
    this.multipleKinds = hasMultipleKinds(this.applicationType);
    this.availableKinds = getAvailableKinds(this.applicationType);
    this.kindsCtrl = this.fb.control(undefined, Validators.required);
    this.specifiersCtrl = this.fb.control([]);

    this.form = this.fb.group({
      kinds: this.kindsCtrl,
      specifiers: this.specifiersCtrl
    });

    this.parentForm.addControl('kinds', this.form);

    this.initEvents();
  }

  showSpecifierSelection(): boolean {
    return hasSpecifiers(this.availableKindsWithSpecifiers);
  }

  kindSelection(kinds: string | Array<string>) {
    const selectedKinds = Array.isArray(kinds) ? kinds : [kinds];
    const kindsWithSpecifiers = getAvailableSpecifiers(this.applicationType, selectedKinds);
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

  private initEvents(): void {
    this.kindsCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(kinds => this.kindSelection(kinds));

    this.specifiersCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(specifiers => this.onSpecifierSelection(specifiers));
  }

  private updateSelectedSpecifiers() {
    const remainingSpecifiers = this.specifiersCtrl.value
      .map(key => SpecifierEntry.fromKey(key))
      .filter(se => this.availableKinds.indexOf(se.kind) >= 0)
      .map(specifierEntry => specifierEntry.key);

    this.specifiersCtrl.patchValue(remainingSpecifiers);
  }
}
