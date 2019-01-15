import {HostBinding, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {takeUntil} from 'rxjs/internal/operators';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FieldSelectComponent, FieldValues} from '../field-select/field-select.component';
import {Subject} from 'rxjs';
import {FieldDescription} from '@feature/information-request/acceptance/field-select/field-description';

export abstract class InfoAcceptanceComponent<T> implements OnInit, OnDestroy {
  @Input() form: FormGroup;
  @Input() id: string;

  @HostBinding('class') cssClasses = 'info-acceptance';

  @ViewChild('oldValuesSelect') oldValuesSelect: FieldSelectComponent;
  @ViewChild('newValuesSelect') newValuesSelect: FieldSelectComponent;

  selectionForm: FormGroup;
  fieldDescriptions: FieldDescription[];
  oldValues: FieldValues;
  oldDisplayValues: FieldValues;
  newValues: FieldValues;
  newDisplayValues: FieldValues;

  private _readonly: boolean;
  private destroy: Subject<boolean> = new Subject<boolean>();

  protected constructor(protected fb: FormBuilder) {
    this.selectionForm = this.fb.group({
      oldValues: [{value: [], disabled: this.readonly}],
      newValues: [{value: [], disabled: this.readonly}]
    });
  }

  ngOnInit(): void {
    this.initSelectionForm();
    this.initResultForm();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  @Input() set readonly(isReadOnly: boolean) {
    this._readonly = isReadOnly;
    if (isReadOnly && this.selectionForm) {
      this.selectionForm.disable();
    } else {
      this.selectionForm.enable();
    }
  }

  get readonly() {
    return this._readonly;
  }

  selectAllOld(): void {
    if (this.oldValues) {
      this.oldValuesSelect.selectAll();
      this.onOldValuesSelected(Object.keys(this.oldValues));
    }
  }

  clearSelections(): void {
    this.oldValuesSelect.deselectAll();
    this.newValuesSelect.deselectAll();
    this.form.reset({}, {emitEvent: false});
  }

  onOldValuesSelected(fields: string[]): void {
    fields.forEach(f => {
      this.patchField(f, this.oldValues);
      this.newValuesSelect.deselect(f);
    });
    this.form.updateValueAndValidity();
  }

  onNewValuesSelected(fields: string[] = []): void {
    fields.forEach(f => {
      this.form.get(f).patchValue(this.newValues[f], {emitEvent: false});
      this.oldValuesSelect.deselect(f);
    });
    this.form.updateValueAndValidity();
  }

  protected initSelectionForm(): void {
    this.selectionForm.get('oldValues').valueChanges.pipe(
      takeUntil(this.destroy)
    ).subscribe(oldValues => this.onOldValuesSelected(oldValues));

    this.selectionForm.get('newValues').valueChanges.pipe(
      takeUntil(this.destroy)
    ).subscribe(newValues => this.onNewValuesSelected(newValues));
  }

  protected initResultForm(): void {
    this.fieldDescriptions.forEach(desc => {
      const validators = this.isRequired(desc.field) ? [Validators.required] : [];
      const ctrl = this.fb.control(undefined, validators);
      this.form.addControl(desc.field, ctrl);
    });

    this.form.valueChanges.pipe(
      takeUntil(this.destroy),
    ).subscribe((selectedValues) => this.resultChanges(selectedValues));
  }

  protected isRequired(field: string): boolean {
    return false;
  }

  protected abstract resultChanges(result: FieldValues): void;

  private patchField(field: string, valuesFrom: FieldValues): void {
    if (this.form.contains(field)) {
      this.form.get(field).patchValue(valuesFrom[field], {emitEvent: false});
    }
  }
}
