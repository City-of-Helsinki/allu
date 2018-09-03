import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Selected} from './field-acceptance.component';
import {BehaviorSubject, combineLatest, Subject} from 'rxjs/index';
import {takeUntil} from 'rxjs/internal/operators';
import isEqual from 'lodash/isEqual';

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

    combineLatest(
      this.oldValues$,
      this.newValues$
    ).pipe(
      takeUntil(this.destroy)
    ).subscribe(() => this.updateSelections());
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  @Input() set oldValues(oldValues: FieldValues) {
    this.emptyOldValues = this.noValues(oldValues);
    this.oldValues$.next(oldValues);
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
      const oldValue = this.oldValues[field];
      const newValue = this.newValues[field];
      return isEqual(oldValue, newValue);
    } else {
      return false;
    }
  }

  private updateSelections(): void {
    const displayedFields = [];
    Object.keys(this.fieldLabels).forEach(field => {
      if (this.emptyOldValues) {
        this.updateFieldSelection(field, 'new');
      } else {
        const selected = this.selectedValue(field);
        this.updateFieldSelection(field, selected);
      }

      if (!this.valuesEqual(field)) {
        displayedFields.push(field);
      }
    });

    this.displayedFields = displayedFields;
    this.form.updateValueAndValidity();
  }

  private updateFieldSelection(field: string, selected: Selected): void {
    const ctrl = this.form.get(field);
    if (ctrl) {
      ctrl.patchValue(selected, {emitEvent: false});
    }
  }

  private selectedValue(field: string): Selected {
    return this.valuesEqual(field) ? 'new' : undefined;
  }

  private noValues(fieldValues: FieldValues): boolean {
    if (fieldValues) {
      return Object.keys(fieldValues).every(field => fieldValues[field] === undefined);
    } else {
      return true;
    }
  }
}
