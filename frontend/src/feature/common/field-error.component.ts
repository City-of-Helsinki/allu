import {Component, Input} from '@angular/core';
import {FormGroup, AbstractControl} from '@angular/forms';

const validatedAlways = [
  'startBeforeEnd'
];

@Component({
  selector: 'field-error',
  template: '<span *ngIf="showError()"><ng-content></ng-content></span>',
  styles: [require('./field-error.component.scss')]
})
export class FieldErrorComponent {
  @Input() form: FormGroup;
  @Input() field: string;
  @Input() hasError: string;

  showError(): boolean {
    if (this.form) {
      let formField = this.form.get(this.field);
      return this.shouldValidate(formField) && formField.hasError(this.hasError);
    } else {
      return false;
    }
  }

  private shouldValidate = (formField: AbstractControl) => this.validateAlways() || this.fieldChanged(formField);

  private fieldChanged = (formField: AbstractControl) => formField.touched || formField.dirty;

  private validateAlways = () => validatedAlways.indexOf(this.hasError) >= 0;
}
