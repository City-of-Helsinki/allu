import {AbstractControl, UntypedFormArray, UntypedFormGroup} from '@angular/forms';
import {Some} from '@util/option';

export class FormUtil {
  public static clearArray(formArray: UntypedFormArray) {
    while (formArray.length) {
      formArray.at(0).reset();
      formArray.removeAt(0);
    }
  }

  public static removeByValue(formArray: UntypedFormArray, condition: (val: any) => boolean) {
    for (let index = 0; index < formArray.length; ++index) {
      if (condition(formArray.at(index).value)) {
        formArray.removeAt(index);
      }
    }
  }

  public static contains(formArray: UntypedFormArray, condition: (val: any) => boolean) {
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

  public static getValue(form: UntypedFormGroup, path: string): any {
    return Some(form)
      .map(f => f.get(path))
      .map(ctrl => ctrl.value)
      .orElse(undefined);
  }

  public static addControls(form: UntypedFormGroup, controls: {[name: string]: AbstractControl}): void {
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
    if (control instanceof UntypedFormGroup) {
      Object.keys(control.controls).forEach(key => {
        const formControl = control.get(key);
        this.validateFormFields(formControl);
      });
    } else if (control instanceof UntypedFormArray) {
      control.controls.forEach(arrayControl => this.validateFormFields(arrayControl));
    } else {
      control.markAsTouched({onlySelf: true});
    }
  }

  public static errorCount(control: AbstractControl): number {
    if (control instanceof UntypedFormGroup) {
      return Object.keys(control.controls)
        .map(key => control.get(key))
        .reduce((prev, cur) => prev + this.errorCount(cur), 0);
    } else if (control instanceof UntypedFormArray) {
      return control.controls.reduce((prev, cur) => prev + this.errorCount(cur), 0);
    } else {
      return control.errors ? Object.keys(control.errors).length : 0;
    }
  }
}
