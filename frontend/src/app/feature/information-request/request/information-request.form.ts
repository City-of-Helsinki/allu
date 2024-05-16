import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {InformationRequest} from '@model/information-request/information-request';
import {InformationRequestField} from '@model/information-request/information-request-field';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {StringUtil} from '@util/string.util';

export interface InformationRequestForm {
  [key: string]: string;
}

export function toFormGroup(fb: UntypedFormBuilder,
                            fields: string[] = [],
                            request: InformationRequest = new InformationRequest()): UntypedFormGroup {
  const fieldValues = request.fields.reduce((acc, field) => {
    acc[field.fieldKey] = field.description;
    return acc;
  }, {});

  const formGroup = fields.reduce((group, field) => {
      const value = fieldValues[field];
      const control = value ? [value, Validators.required] : [value];
      group[field] = control;
      return group;
    }, {});
  return fb.group(formGroup);
}

export function toInformationRequestFields(requestForm: InformationRequestForm = {}): InformationRequestField[] {
  return Object.keys(requestForm)
    .filter(field => !StringUtil.isEmpty(requestForm[field]))
    .map((field: InformationRequestFieldKey) => new InformationRequestField(field, requestForm[field]));
}
