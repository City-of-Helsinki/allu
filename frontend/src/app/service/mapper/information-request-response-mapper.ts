import {InformationRequestResponse} from '@model/information-request/information-request-response';
import {ApplicationMapper} from './application-mapper';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {BackendApplication} from '../backend-model/backend-application';

export interface BackendInformationRequestResponse {
  informationRequestId: number;
  applicationId: number;
  responseData: BackendApplication;
  updatedFields: InformationRequestFieldKey[];
}

export class InformationRequestResponseMapper {
  public static mapBackend(backendResponse: BackendInformationRequestResponse): InformationRequestResponse {
    return backendResponse
      ? new InformationRequestResponse(
        backendResponse.informationRequestId,
        backendResponse.applicationId,
        ApplicationMapper.mapBackend(backendResponse.responseData),
        backendResponse.updatedFields)
      : undefined;
  }
}
