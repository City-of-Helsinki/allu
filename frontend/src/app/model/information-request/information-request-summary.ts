import {InformationRequestStatus} from './information-request-status';
import {InformationRequestField} from '@model/information-request/information-request-field';

export class InformationRequestSummary {
  constructor(
    public informationRequestId?: number,
    public applicationId?: number,
    public status?: InformationRequestStatus,
    public creationTime?: Date,
    public responseReceived?: Date,
    public creator?: string,
    public respondent?: string,
    public updateWithoutRequest?: boolean,
    public requestedFields: InformationRequestField[] = [],
    public responseFields: InformationRequestField[] = []) {}
}
