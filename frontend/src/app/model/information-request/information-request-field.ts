import {InformationRequestFieldKey} from './information-request-field-key';

export class InformationRequestField {
  constructor(
    public fieldKey?: InformationRequestFieldKey,
    public description?: string) {}
}
