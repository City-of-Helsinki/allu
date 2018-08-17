import {InformationRequest} from '@model/information-request/information-request';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {InformationRequestField} from '@model/information-request/information-request-field';

export interface BackendInformationRequestField {
  fieldKey: string;
  description: string;
}

export interface BackendInformationRequest {
  id: number;
  applicationId: number;
  fields: BackendInformationRequestField[];
  status: string;
}

export class InformationRequestMapper {
  public static mapBackend(backendRequest: BackendInformationRequest): InformationRequest {
    return backendRequest
      ? new InformationRequest(
        backendRequest.id,
        backendRequest.applicationId,
        backendRequest.fields.map(field => new InformationRequestField(InformationRequestFieldKey[field.fieldKey], field.description)),
        InformationRequestStatus[backendRequest.status])
      : undefined;
  }

  public static mapFrontend(frontendRequest: InformationRequest): BackendInformationRequest {
    return {id: frontendRequest.informationRequestId,
            applicationId: frontendRequest.applicationId,
            fields: this.mapFields(frontendRequest.fields),
            status: InformationRequestStatus[frontendRequest.status]};
  }

  private static mapFields(fields: InformationRequestField[]): BackendInformationRequestField[] {
    return fields.map(field => { return {fieldKey: InformationRequestFieldKey[field.fieldKey], description: field.description}; });
  }

}
