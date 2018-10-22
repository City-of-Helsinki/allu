import {AbstractControl, FormArray, FormGroup} from '@angular/forms';
import {Some} from '@util/option';

export class FormUtil {
  public static clearArray(formArray: FormArray) {
    while (formArray.length) {
      formArray.at(0).reset();
      formArray.removeAt(0);
    }
  }

  public static removeByValue(formArray: FormArray, condition: (val: any) => boolean) {
    for (let index = 0; index < formArray.length; ++index) {
      if (condition(formArray.at(index).value)) {
        formArray.removeAt(index);
      }
    }
  }

  public static contains(formArray: FormArray, condition: (val: any) => boolean) {
    const values = formArray.value;
    return values ? values.some(condition) : false;
  }

  public static required(abstractControl: AbstractControl): boolean {
    if (abstractControl.validator) {
      const validator = abstractControl.validator({} as AbstractControl);
      if (validator && validator.required) {
        return true;
      }
    }
    if (abstractControl['controls']) {
      for (const controlName in abstractControl['controls']) {
        if (abstractControl['controls'][controlName]) {
          if (FormUtil.required(abstractControl['controls'][controlName])) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public static getValue(form: FormGroup, path: string): any {
    return Some(form)
      .map(f => f.get(path))
      .map(ctrl => ctrl.value)
      .orElse(undefined);
  }
}
