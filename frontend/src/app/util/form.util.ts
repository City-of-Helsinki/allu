import {FormArray} from '@angular/forms';

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
}
