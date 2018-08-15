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

  displayedFields: string[];

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
}
