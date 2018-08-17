import {InformationRequestField} from './information-request-field';
import {InformationRequestStatus} from './information-request-status';

export class InformationRequest {
  constructor(
    public informationRequestId?: number,
    public applicationId?: number,
    public fields: InformationRequestField[] = [],
    public status: InformationRequestStatus = InformationRequestStatus.OPEN) {}
}
