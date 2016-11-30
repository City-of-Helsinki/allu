import {FormGroup, FormControl, AbstractControl} from '@angular/forms';
import {Some} from './option';
import {TimeUtil} from './time.util';

/**
 * Implements more complex validations than angular2 provides out of the box
 * Single field validators return validation function as is.
 * Multi field validators return {validator: validationFn} objects.
 */
export class ComplexValidator {
  constructor(public validator: (validated: AbstractControl) => ValidationResult) {}

  static greaterThanOrEqual(value: number) {
    let validationFn = (fc: AbstractControl) => {
      // Need to use dirty here since input[type="number"] does not set touched unless arrows are clicked
      if (fc.dirty) {
        if (Number(fc.value) < value) {
          return { greaterThanOrEqual: true };
          }
        }
      return undefined;
    };

    return validationFn;
  }

  static startBeforeEnd(startField: string, endField: string) {
    let validationFn = (fg: FormGroup) => {
      let start = this.fieldValue(fg, startField);
      let end = this.fieldValue(fg, endField);

      if (start && end) {
        let valid = !TimeUtil.isBefore(end, start);

        // undefined means valid field
        return valid  ? undefined : { startBeforeEnd: true };
      }
      return undefined;
    };

    return new ComplexValidator(validationFn);
  }

  private static fieldValue(group: FormGroup, fieldName: string) {
    return Some(group.get(fieldName))
      .filter(control => !!control)
      .map(control => control.value)
      .orElse(undefined);
  }
}

interface ValidationResult {
  [key: string]: boolean;
}
