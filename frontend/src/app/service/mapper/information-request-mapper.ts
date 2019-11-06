import {InformationRequest} from '@model/information-request/information-request';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {InformationRequestField} from '@model/information-request/information-request-field';

export interface BackendInformationRequestField {
  fieldKey: InformationRequestFieldKey;
  description: string;
}

export interface BackendInformationRequest {
  id: number;
  applicationId: number;
  fields: BackendInformationRequestField[];
  status: InformationRequestStatus;
}

export class InformationRequestMapper {
  public static mapBackend(backendRequest: BackendInformationRequest): InformationRequest {
    return backendRequest
      ? new InformationRequest(
        backendRequest.id,
        backendRequest.applicationId,
        backendRequest.fields.map(field => new InformationRequestField(field.fieldKey, field.description)),
        backendRequest.status)
      : undefined;
  }

  public static mapFrontend(frontendRequest: InformationRequest): BackendInformationRequest {
    return {id: frontendRequest.informationRequestId,
            applicationId: frontendRequest.applicationId,
            fields: this.mapFields(frontendRequest.fields),
            status: frontendRequest.status};
  }

  public static mapFields(fields: InformationRequestField[]): BackendInformationRequestField[] {
    return fields.map(field => ({fieldKey: field.fieldKey, description: field.description}));
  }
}
