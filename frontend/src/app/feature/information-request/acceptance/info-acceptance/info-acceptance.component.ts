import { HostBinding, Input, OnDestroy, OnInit, ViewChild, Directive } from '@angular/core';
import {takeUntil} from 'rxjs/internal/operators';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FieldSelectComponent, FieldValues} from '../field-select/field-select.component';
import {Subject} from 'rxjs';
import {FieldDescription} from '@feature/information-request/acceptance/field-select/field-description';
import {StructureMeta} from '@model/application/meta/structure-meta';
import { Some } from '@app/util/option';

@Directive()
export abstract class InfoAcceptanceDirective<T> implements OnInit, OnDestroy {
  @Input() form: FormGroup;
  @Input() id: string;
  @Input() meta: StructureMeta;
  @Input() hideExisting = false;

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
      // Timeout forces selectAll function to be called on next angular update cycle
      // which allows view to update with selected values. Without timeout it seems
      // that values update but the view does not.
      setTimeout(() => Some(this.oldValuesSelect).do(select => select.selectAll()), 0);
      this.onOldValuesSelected(Object.keys(this.oldValues));
    }
  }

  clearSelections(): void {
    Some(this.oldValuesSelect).do(select => select.deselectAll());
    Some(this.newValuesSelect).do(select => select.deselectAll());
    this.form.reset({}, {emitEvent: false});
  }

  onOldValuesSelected(fields: string[]): void {
    fields.forEach(f => {
      this.patchField(f, this.oldValues);
      Some(this.newValuesSelect).do(select => select.deselect(f));
    });
    this.form.updateValueAndValidity();
  }

  onNewValuesSelected(fields: string[] = []): void {
    fields.forEach(f => {
      this.patchField(f, this.newValues);
      Some(this.oldValuesSelect).do(select => select.deselect(f));
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
      const ctrl = this.fb.control(undefined, [Validators.required]);
      this.form.addControl(desc.field, ctrl);
    });

    this.form.valueChanges.pipe(
      takeUntil(this.destroy),
    ).subscribe((selectedValues) => this.resultChanges(selectedValues));
  }

  protected abstract resultChanges(result: FieldValues): void;

  private patchField(field: string, valuesFrom: FieldValues): void {
    if (this.form.contains(field)) {
      this.form.get([field]).patchValue(valuesFrom[field], {emitEvent: false});
      // Required validator need to be cleared so that undefined value can be selected
      // Required validator can be cleared because user cannot deselect row whole row selection once selected
      this.form.get([field]).clearValidators();
      this.form.get([field]).updateValueAndValidity();
    }
  }
}
