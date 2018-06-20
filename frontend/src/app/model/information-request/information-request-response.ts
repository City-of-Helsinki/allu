import {Application} from '../application/application';
import {InformationRequestFieldKey} from './information-request-field-key';

export class InformationRequestResponse {
  constructor(
    public informationRequestId?: number,
    public applicationId?: number,
    public responseData?: Application,
    public updatedFiedls: InformationRequestFieldKey[] = []) {}
}
