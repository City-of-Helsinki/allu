import {AbstractControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {Some} from './option';
import {TimeUtil} from './time.util';
import {NumberUtil} from './number.util';
import * as finnishSsn from 'finnish-ssn';

/**
 * Implements more complex validations than angular2 provides out of the box
 * Single field validators return validation function as is.
 * Multi field validators return {validator: validationFn} objects.
 */
export class ComplexValidator {
  constructor(public validator: (validated: AbstractControl) => ValidationResult) {}

  static greaterThanOrEqual(value: number): ValidatorFn {
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

  static betweenOrEmpty(min, max) {
    let validationFn = (fc: AbstractControl) => {
      if (fc.dirty && NumberUtil.isDefined(fc.value)) {
        let val = Number(fc.value);
        if (!NumberUtil.isBetween(val, min, max)) {
          return {between: {val}};
        }
      }
      return undefined;
    };
    return validationFn;
  }

  /**
   * Validator which adds warning to field if it fails validation
   * @returns always a valid result (undefined) so form field is not invalidated
   */
  static invalidSsnWarning(fc: AbstractControlWarn) {
    const ssn = fc.value;
    fc.warnings = fc.warnings || {};
    if (fc.dirty && !finnishSsn.validate(ssn)) {
      fc.warnings.invalidSsn = {ssn};
    } else {
      fc.warnings.invalidSsn = undefined;
    }
    return undefined;
  }

  static inWinterTime(fc: AbstractControlWarn) {
    const date = fc.value;

    fc.warnings = fc.warnings || {};
    if (fc.dirty && TimeUtil.isInWinterTime(date)) {
      fc.warnings.inWinterTime = {date};
    } else {
      fc.warnings.inWinterTime = undefined;
    }
    return undefined;
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

  static inThePast(fc: AbstractControl): ValidationErrors {
    let now =  new Date();
    now.setHours(0, 0, 0, 0); // start of the day
    const inThePast = fc.value && TimeUtil.isBefore(fc.value, now);
    return inThePast ? { inThePast: fc.value } : undefined;
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

export interface AbstractControlWarn extends AbstractControl {
  warnings: { [key: string]: any; };
}

export const emailValidator = Validators.pattern('.+@.+\\..+');

export const postalCodeValidator = Validators.pattern(/^[0-9]{5}$/);
