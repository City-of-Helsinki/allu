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

  public static addControls(form: FormGroup, controls: {[name: string]: AbstractControl}): void {
    if (form && controls) {
      Object.keys(controls).map(name => form.addControl(name, controls[name]));
    }
  }

  /**
   * Runs field validation on all form fields.
   * This can be used to mark missing fields and show errors when user
   * is trying to save form with missing values.
   */
  public static validateFormFields(control: AbstractControl): void {
    if (control instanceof FormGroup) {
      Object.keys(control.controls).forEach(key => {
        const formControl = control.get(key);
        this.validateFormFields(formControl);
      });
    } else if (control instanceof FormArray) {
      control.controls.forEach(arrayControl => this.validateFormFields(arrayControl));
    } else {
      control.markAsTouched({onlySelf: true});
    }
  }
}
