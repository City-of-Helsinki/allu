import {HostBinding, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {takeUntil} from 'rxjs/internal/operators';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FieldLabels, FieldSelectComponent, FieldValues} from '../field-select/field-select.component';
import {Subject} from 'rxjs';

export abstract class InfoAcceptanceComponent<T> implements OnInit, OnDestroy {
  @Input() form: FormGroup;
  @Input() readonly: boolean;

  @HostBinding('class') cssClasses = 'info-acceptance';

  @ViewChild('oldValuesSelect') oldValuesSelect: FieldSelectComponent;
  @ViewChild('newValuesSelect') newValuesSelect: FieldSelectComponent;

  selectionForm: FormGroup;
  fieldLabels: FieldLabels;
  oldValues: FieldValues;
  oldDisplayValues: FieldValues;
  newValues: FieldValues;
  newDisplayValues: FieldValues;

  private destroy: Subject<boolean> = new Subject<boolean>();

  protected constructor(protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.initSelectionForm();
    this.initResultForm();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onOldValuesSelected(fields: string[]): void {
    fields.forEach(f => {
      this.form.get(f).patchValue(this.oldValues[f]);
      this.newValuesSelect.deselect(f);
    });
  }

  onNewValuesSelected(fields: string[] = []): void {
    fields.forEach(f => {
      this.form.get(f).patchValue(this.newValues[f]);
      this.oldValuesSelect.deselect(f);
    });
  }

  protected initSelectionForm(): void {
    this.selectionForm = this.fb.group({
      oldValues: [{value: [], disabled: this.readonly}],
      newValues: [{value: [], disabled: this.readonly}]
    });

    this.selectionForm.get('oldValues').valueChanges.pipe(
      takeUntil(this.destroy)
    ).subscribe(oldValues => this.onOldValuesSelected(oldValues));

    this.selectionForm.get('newValues').valueChanges.pipe(
      takeUntil(this.destroy)
    ).subscribe(newValues => this.onNewValuesSelected(newValues));
  }

  protected initResultForm(): void {
    Object.keys(this.fieldLabels).forEach(field => {
      const validators = this.isRequired(field) ? [Validators.required] : [];
      const ctrl = this.fb.control(undefined, validators);
      this.form.addControl(field, ctrl);
    });

    this.form.valueChanges.pipe(
      takeUntil(this.destroy),
    ).subscribe((selectedValues) => this.resultChanges(selectedValues));
  }

  protected isRequired(field: string): boolean {
    return false;
  }

  protected abstract resultChanges(result: FieldValues): void;
}
