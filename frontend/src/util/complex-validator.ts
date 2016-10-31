import {FormGroup} from '@angular/forms';
import {Some} from './option';
import {TimeUtil} from './time.util';

export class ComplexValidator {
  constructor(public validator: (fg: FormGroup) => any) {}

  static startBeforeEnd(startField: string, endField: string) {
    let validationFn = (fg: FormGroup) => {
      if (fg.touched) {
        let start = this.fieldValue(fg, startField);
        let end = this.fieldValue(fg, endField);

        if (start && end) {
          let valid = !TimeUtil.isBefore(end, start);

          // undefined means valid field
          return valid  ? undefined : {
            startBeforeEnd: {
              valid: false
            }
          };
        }
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
