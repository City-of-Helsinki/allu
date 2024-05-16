import {Component, Input, OnInit} from '@angular/core';
import {UntypedFormGroup} from '@angular/forms';
import {AbstractControlWarn} from '../../../util/complex-validator';

const validatedAlways = [
  'startBeforeEnd'
];

@Component({
  selector: 'field-error',
  templateUrl: './field-error.component.html',
  styleUrls: ['./field-error.component.scss']
})
export class FieldErrorComponent implements OnInit {
  @Input() form: UntypedFormGroup;
  @Input() field: string;
  @Input() hasError: string;

  private control: AbstractControlWarn;

  ngOnInit(): void {
    this.control = this.getField();
  }

  get visible(): boolean {
    return this.showError || this.showWarning;
  }

  get showError(): boolean {
    return this.shouldValidate() && this._hasError();
  }

  get showWarning(): boolean {
    // Warning is only shown when there are no errors
    return this.shouldValidate() && !this.control.errors && this._hasWarning();
  }

  private getField(): AbstractControlWarn {
    if (this.form) {
      const field = <AbstractControlWarn>this.form.get(this.field);
      if (field) {
        return field;
      }
    }
    throw new Error('No error field found ' + this.field);
  }

  private _hasError(): boolean {
    return this.control.hasError(this.hasError);
  }

  private _hasWarning(): boolean {
    return this.control.warnings && !!this.control.warnings[this.hasError];
  }

  private shouldValidate = () => this.validateAlways() || this.fieldChanged();

  private fieldChanged = () => this.control.touched || this.control.dirty;

  private validateAlways = () => validatedAlways.indexOf(this.hasError) >= 0;
}
