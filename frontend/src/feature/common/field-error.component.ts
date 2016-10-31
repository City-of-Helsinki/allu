import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'field-error',
  template: '<small *ngIf="showError()"><ng-content></ng-content></small>'
})
export class FieldErrorComponent {
  @Input() form: FormGroup;
  @Input() field: string;
  @Input() hasError: string;

  showError(): boolean {
    if (this.form) {
      let formField = this.form.get(this.field);
      return (formField.touched || formField.dirty) && formField.hasError(this.hasError);
    } else {
      return false;
    }
  }
}
