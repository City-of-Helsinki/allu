import {Input, OnInit} from '@angular/core';
import {FieldLabels, FieldValues} from '@feature/information-request/acceptance/field-group-acceptance.component';
import {FieldSelection, Selected} from '@feature/information-request/acceptance/field-acceptance.component';
import {map} from 'rxjs/internal/operators';
import {FormGroup} from '@angular/forms';

export abstract class InfoAcceptance<T> implements OnInit {
  @Input() form: FormGroup;

  fieldLabels: FieldLabels;
  oldValues: FieldValues;
  oldDisplayValues: FieldValues;
  newValues: FieldValues;
  newDisplayValues: FieldValues;

  ngOnInit(): void {
    this.form.valueChanges.pipe(
      map((selections: FieldSelection) => this.selectionsToValues(selections))
    ).subscribe((selectedValues) => this.resultChanges(selectedValues));
  }

  protected abstract resultChanges(result: FieldValues): void;

  protected selectionsToValues(selections: FieldSelection): FieldValues {
    return Object.keys(selections).reduce((prev: FieldValues, field: string) => {
      const selection = selections[field];
      prev[field] = this.getValue(field, selection);
      return prev;
    }, {});
  }

  protected getValue(fieldName: string, selection: Selected): any {
    if (selection === 'old') {
      return this.oldValues[fieldName];
    } else if (selection === 'new') {
      return this.newValues[fieldName];
    } else {
      return undefined;
    }
  }
}
