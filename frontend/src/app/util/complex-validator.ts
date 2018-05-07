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
  static greaterThanOrEqual(value: number): ValidatorFn {
    const validationFn = (fc: AbstractControl) => {
      // Need to use dirty here since input[type="number"] does not set touched unless arrows are clicked
      if (fc.dirty) {
        if (Number(fc.value) < value) {
          return {greaterThanOrEqual: true};
        }
      }
      return undefined;
    };

    return validationFn;
  }

  static betweenOrEmpty(min, max) {
    const validationFn = (fc: AbstractControl) => {
      if (fc.dirty && NumberUtil.isDefined(fc.value)) {
        const val = Number(fc.value);
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

  static startBeforeEnd(startField: string, endField: string): ValidatorFn {
    return (fg: FormGroup) => {
      const start = this.fieldValue(fg, startField);
      const end = this.fieldValue(fg, endField);

      if (start && end) {
        const valid = !TimeUtil.isBefore(end, start);

        // undefined means valid field
        return valid ? undefined : {startBeforeEnd: true};
      }
      return undefined;
    };
  }

  static durationAtMax(startField: string, endField: string, maxDurationAtDays: number): ValidatorFn {
    return (fg: FormGroup) => {
      const start = this.fieldValue(fg, startField);
      const end = this.fieldValue(fg, endField);

      if (start && end) {
        let maxEnd = new Date(start);
        maxEnd.setDate(maxEnd.getDate() + maxDurationAtDays);

        const valid = TimeUtil.isBefore(end, maxEnd);

        // undefined means valid field
        return valid ? undefined : {durationAtMax: true};
      }
      return undefined;
    };
  }

  static after(dateCtrl: AbstractControl): ValidatorFn {
    return (fg: AbstractControl) => {
      const first = fg.value;
      const second = dateCtrl.value;

      if (first && second) {
        const invalid = !TimeUtil.isBefore(first, second);
        return invalid ? {after: `${first} not after ${second}`} : undefined;
      }
      return undefined;
    };
  }

  static before(dateCtrl: AbstractControl): ValidatorFn {
    return (fg: AbstractControl) => {
      const first = fg.value;
      const second = dateCtrl.value;

      if (first && second) {
        const invalid = !TimeUtil.isAfter(first, second);
        return invalid ? {before: `${first} not before ${second}`} : undefined;
      }
      return undefined;
    };
  }

  static inThePast(fc: AbstractControl): ValidationErrors {
    const now = new Date();
    now.setHours(0, 0, 0, 0); // start of the day
    const inThePast = fc.value && TimeUtil.isBefore(fc.value, now);
    return inThePast ? {inThePast: fc.value} : undefined;
  }

  static idRequired(control: AbstractControl): ValidationErrors | undefined {
    const id = control.value ? control.value.id : undefined;
    return NumberUtil.isDefined(id)
      ? undefined
      : {'idRequired': {value: control.value}};
  }

  private static fieldValue(group: FormGroup, fieldName: string) {
    return Some(group.get(fieldName))
      .filter(control => !!control)
      .map(control => control.value)
      .orElse(undefined);
  }
}

export interface AbstractControlWarn extends AbstractControl {
  warnings: { [key: string]: any; };
}

export const emailValidator = Validators.pattern('.+@.+\\..+');

export const postalCodeValidator = Validators.pattern(/^[0-9]{5}$/);
