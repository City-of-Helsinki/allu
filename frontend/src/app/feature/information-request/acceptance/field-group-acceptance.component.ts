import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Selected} from './field-acceptance.component';
import {BehaviorSubject, merge, Subject} from 'rxjs/index';
import {takeUntil} from 'rxjs/internal/operators';

export interface FieldLabels {
  [field: string]: string;
}

export interface FieldValues {
  [field: string]: any;
}

@Component({
  selector: 'field-group-acceptance',
  templateUrl: './field-group-acceptance.component.html',
  styleUrls: ['./field-group-acceptance.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldGroupAcceptanceComponent implements OnInit, OnDestroy {
  @Input() fieldLabels: FieldLabels;
  @Input() form: FormGroup;
  @Input() readonly: boolean;

  displayedFields: string[] = [];
  emptyOldValues: boolean;

  private oldValues$: BehaviorSubject<FieldValues> = new BehaviorSubject<FieldValues>(undefined);
  private newValues$: BehaviorSubject<FieldValues> = new BehaviorSubject<FieldValues>(undefined);
  private destroy = new Subject<boolean>();

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    Object.keys(this.fieldLabels).forEach(field => {
      const ctrl = this.fb.control(undefined, Validators.required);
      this.form.addControl(field, ctrl);
    });

    merge(
      this.oldValues$,
      this.newValues$
    ).pipe(
      takeUntil(this.destroy),
    ).subscribe(() => this.updateSelections());
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  @Input() set oldValues(oldValues: FieldValues) {
    this.oldValues$.next(oldValues);
    this.emptyOldValues = this.noValues(oldValues);
    if (this.emptyOldValues) {
      this.setAllValuesAs('new');
    }
  }

  get oldValues() {
    return this.oldValues$.getValue();
  }

  @Input() set newValues(newValues: FieldValues) {
    this.newValues$.next(newValues);
  }

  get newValues() {
    return this.newValues$.getValue();
  }

  get showControls() {
    const displayedFields = this.displayedFields.length > 0;
    const notReadonly = !this.readonly;
    const hasOldValues = !this.emptyOldValues;
    return displayedFields && notReadonly && hasOldValues;
  }

  valuesEqual(field: string): boolean {
    if (this.oldValues && this.newValues) {
      return this.oldValues[field] === this.newValues[field];
    } else {
      return false;
    }
  }

  private updateSelections(): void {
    const displayedFields = [];
    Object.keys(this.fieldLabels).forEach(field => {
      this.updateFieldSelection(field);

      if (!this.valuesEqual(field)) {
        displayedFields.push(field);
      }
    });

    this.displayedFields = displayedFields;
    this.form.updateValueAndValidity();
  }

  private updateFieldSelection(field: string): void {
    const ctrl = this.form.get(field);
    if (ctrl) {
      const selected = this.selectedValue(field);
      ctrl.patchValue(selected);
    }
  }

  private selectedValue(field: string): Selected {
    return this.valuesEqual(field) ? 'new' : undefined;
  }

  private setAllValuesAs(selected: Selected): void {
    Object.keys(this.form.controls).forEach(field => {
      const ctrl = this.form.get(field);
      ctrl.patchValue(selected);
    });
  }

  private noValues(fieldValues: FieldValues): boolean {
    if (fieldValues) {
      return Object.keys(fieldValues).every(field => fieldValues[field] === undefined);
    } else {
      return true;
    }
  }
}
