import {AbstractControl, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {Some} from './option';
import {TimeUtil} from './time.util';
import {NumberUtil} from './number.util';
import * as finnishSsn from 'finnish-ssn';
import {unitOfTime} from 'moment';
import {StringUtil} from '@util/string.util';

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
    if (ssn && fc.dirty && !finnishSsn.validate(ssn)) {
      fc.warnings.invalidSsn = {ssn};
    } else {
      fc.warnings.invalidSsn = undefined;
    }
    return undefined;
  }

  static inWinterTimeWarn(winterStart: Date, winterEnd: Date): ValidatorFn {
    const validationFn = (fc: AbstractControlWarn) => {
      const date = fc.value;
      if (!date) {
        return undefined;
      }

      fc.warnings = fc.warnings || {};
      if (TimeUtil.isInTimePeriod(date, winterStart, winterEnd)) {
        fc.warnings.inWinterTime = {date};
      } else {
        delete fc.warnings.inWinterTime;
      }
      return undefined;
    };
    return validationFn;
  }

  static inTimePeriod(periodStart: Date, periodEnd: Date): ValidatorFn {
    return (fc: AbstractControl) => {
      const inTimePeriod = fc.value && !TimeUtil.isInTimePeriod(fc.value, periodStart, periodEnd);
      return inTimePeriod ? {inTimePeriod: fc.value} : undefined;
    };
  }

  static maxDate(date: Date): ValidatorFn {
    return (fc: AbstractControl) => {
      const afterMax = fc.value && fc.value > date;
      return afterMax ? {maxDate: fc.value} : undefined;
    };
  }

  static minDate(date: Date): ValidatorFn {
    return (fc: AbstractControl) => {
      const earlierThanMin = fc.value && fc.value < date;
      return earlierThanMin ? {minDate: fc.value} : undefined;
    };
  }

  static startBeforeEnd(startField: string, endField: string): ValidatorFn {
    return (fg: UntypedFormGroup) => {
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

  static durationAtMax(startField: string, endField: string, maxDuration: number, unit: unitOfTime.DurationConstructor): ValidatorFn {
    return (fg: UntypedFormGroup) => {
      const start = this.fieldValue(fg, startField);
      const end = this.fieldValue(fg, endField);

      if (start && end) {
        const maxEnd = TimeUtil.add(new Date(start), maxDuration, unit);
        const valid = TimeUtil.isBefore(end, maxEnd);

        // undefined means valid field
        return valid ? undefined : {durationAtMax: true};
      }
      return undefined;
    };
  }

  static maxRows(maxRows: number): ValidatorFn {
    return (fc: AbstractControl) => {
      const texts = fc.value;
      if (texts) {
        const rows = texts.split('\n');
        const valid = rows.length <= maxRows;
        return valid ? undefined : {tooManyRows: true};
      }
      return undefined;
    };
  }

  static maxRowLength(maxLength: number): ValidatorFn {
    return (fc: AbstractControl) => {
      const texts = fc.value;
      if (texts) {
        const rows = texts.split('\n');
        const valid = rows.reduce(
            (validity: boolean, text: string) => text.length <= maxLength && validity, true);
        return valid ? undefined : {tooLongRows: true};
      }
      return undefined;
    };
  }

  static before(dateCtrl: AbstractControl): ValidatorFn {
    return (fg: AbstractControl) => {
      const first = fg.value;
      const second = dateCtrl.value;

      if (first && second) {
        const invalid = !TimeUtil.isBefore(first, second);
        return invalid ? {before: `${first} not before ${second}`} : undefined;
      }
      return undefined;
    };
  }

  static after(dateCtrl: AbstractControl): ValidatorFn {
    return (fg: AbstractControl) => {
      const first = fg.value;
      const second = dateCtrl.value;

      if (first && second) {
        const invalid = !TimeUtil.isAfter(first, second);
        return invalid ? {after: `${first} not after ${second}`} : undefined;
      }
      return undefined;
    };
  }

  static afterOrSameDay(date: Date): ValidatorFn {
    return (fg: AbstractControl) => {
      const first = fg.value;
      const second = date;

      if (first && second) {
        const invalid = TimeUtil.isBefore(first, second, 'day');
        return invalid ? {afterOrSameDay: `${first} not after or same day as ${second}`} : undefined;
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

  static inTheFuture(fc: AbstractControl): ValidationErrors {
    const now = new Date();
    now.setHours(23, 59, 59, 99); // end of the day
    const inTheFuture = fc.value && TimeUtil.isAfter(fc.value, now);
    return inTheFuture ? {inTheFuture: fc.value} : undefined;
  }

  static idRequired(control: AbstractControl): ValidationErrors | undefined {
    const id = control.value ? control.value.id : undefined;
    return NumberUtil.isDefined(id)
      ? undefined
      : {'idRequired': {value: control.value}};
  }

  static idValid(control: AbstractControl): ValidationErrors | undefined {
    return StringUtil.isEmpty(control.value) || NumberUtil.isExisting(control.value)
      ? undefined
      : {'idValid': {value: control.value}};
  }

  static requiredLength(length: number): ValidatorFn {
    return (control: AbstractControl) => {
      if (control.value) {
        return control.value.length === length
          ? undefined
          : {'requiredLength': {value: control.value.length}};
      } else {
        return undefined;
      }
    };
  }

  static isNumber(control: AbstractControl): ValidationErrors | undefined {
    if (control.value) {
      const patternValidator = Validators.pattern('^[0-9]*$');
      return patternValidator(control)
        ? {'isNumber': {value: control.value}}
        : undefined;
    }
  }

  private static fieldValue(group: UntypedFormGroup, fieldName: string) {
    return Some(group.get(fieldName))
      .filter(control => !!control)
      .map(control => control.value)
      .orElse(undefined);
  }
}

export interface AbstractControlWarn extends AbstractControl {
  warnings: { [key: string]: any; };
}

export const postalCodeValidator = Validators.pattern(/^[0-9]{5}$/);
