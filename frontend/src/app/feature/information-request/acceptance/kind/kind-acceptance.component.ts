import {ChangeDetectionStrategy, Component, HostBinding, Input, OnInit} from '@angular/core';
import {hasSpecifiers, KindsWithSpecifiers, SpecifierEntry} from '@model/application/type/application-specifier';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {getAvailableKinds, getAvailableSpecifiers, hasMultipleKinds} from '@model/application/type/application-type';
import {takeUntil} from 'rxjs/internal/operators';
import {Subject} from 'rxjs/index';
import {Store} from '@ngrx/store';
import * as fromApplication from '@feature/application/reducers/index';
import {SetKindsWithSpecifiers} from '@feature/information-request/actions/information-request-result-actions';
import {ApplicationKind, mergeKindsWithSpecifiers} from '@model/application/type/application-kind';

@Component({
  selector: 'kind-acceptance',
  templateUrl: './kind-acceptance.component.html',
  styleUrls: [
    './kind-acceptance.component.scss',
    '../info-acceptance/info-acceptance.component.scss'
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class KindAcceptanceComponent implements OnInit {
  @Input() applicationType: string;
  @Input() parentForm: UntypedFormGroup;
  @Input() oldValues: KindsWithSpecifiers;
  @Input() newValues: string;
  @Input() readonly: boolean;
  @Input() hideExisting = false;

  @HostBinding('class') cssClasses = 'info-acceptance';

  multipleKinds = false;
  availableKinds: ApplicationKind[] = [];
  availableKindsWithSpecifiers: KindsWithSpecifiers = {};
  form: UntypedFormGroup;

  private kindsCtrl: UntypedFormControl;
  private specifiersCtrl: UntypedFormControl;
  private destroy = new Subject<boolean>();

  constructor(private fb: UntypedFormBuilder,
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

  kindSelection(kinds: string | Array<string>) {
    const selectedKinds = Array.isArray(kinds) ? kinds : [kinds];
    const availableSpecifiers = getAvailableSpecifiers(this.applicationType, selectedKinds);
    this.availableKindsWithSpecifiers = availableSpecifiers;

    if (hasSpecifiers(availableSpecifiers)) {
      this.updateSelectedSpecifiers(selectedKinds);
    }
    this.store.dispatch(new SetKindsWithSpecifiers(this.getSelectedKindsWithSpecifiers()));
  }

  onSpecifierSelection() {
    this.store.dispatch(new SetKindsWithSpecifiers(this.getSelectedKindsWithSpecifiers()));
  }

  private initEvents(): void {
    this.kindsCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(kinds => this.kindSelection(kinds));

    this.specifiersCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(specifiers => this.onSpecifierSelection());
  }

  private updateSelectedSpecifiers(selectedKinds: string[]) {
    const remainingSpecifiers = this.getSelectedSpecifierEntries()
      .filter(se => selectedKinds.indexOf(ApplicationKind[se.kind]) >= 0)
      .map(specifierEntry => specifierEntry.key);
    this.specifiersCtrl.patchValue(remainingSpecifiers);
  }

  private getSelectedKindsWithSpecifiers(): KindsWithSpecifiers {
    const kinds: ApplicationKind[] = this.getSelectedKinds();
    const specifiers: SpecifierEntry[] = this.getSelectedSpecifierEntries();
    return mergeKindsWithSpecifiers(kinds, specifiers);
  }

  private getSelectedKinds(): ApplicationKind[] {
    const kinds = this.kindsCtrl.value;
    return Array.isArray(kinds) ? kinds : [kinds];
  }

  private getSelectedSpecifierEntries(): SpecifierEntry[] {
    return this.specifiersCtrl.value.map(key => SpecifierEntry.fromKey(key));
  }
}
