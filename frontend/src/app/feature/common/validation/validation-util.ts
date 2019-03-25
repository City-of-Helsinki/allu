import {AbstractControl, ValidatorFn} from '@angular/forms';

export function setValidatorsAndValidate(ctrl: AbstractControl, validators: ValidatorFn | ValidatorFn[], opts?: {
  onlySelf?: boolean;
  emitEvent?: boolean;
}) {
  if (ctrl) {
    ctrl.setValidators(validators);
    ctrl.updateValueAndValidity(opts);
  }
}
