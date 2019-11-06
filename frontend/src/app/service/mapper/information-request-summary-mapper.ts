import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';
import {TimeUtil} from '@util/time.util';
import {InformationRequestField} from '@model/information-request/information-request-field';

export interface BackendInformationRequestSummary {
  informationRequestId: number;
  applicationId: number;
  status: InformationRequestStatus;
  creationTime: string;
  responseReceived: string;
  creator: string;
  respondent: string;
  updateWithoutRequest: boolean;
  requestedFields: InformationRequestField[];
  responseFields: InformationRequestField[];
}

export class InformationRequestSummaryMapper {
  public static mapBackendList(summaries: BackendInformationRequestSummary[] = []): InformationRequestSummary[] {
    return summaries.map(s => this.mapBackend(s));
  }

  public static mapBackend(summary: BackendInformationRequestSummary): any {
    return new InformationRequestSummary(
      summary.informationRequestId,
      summary.applicationId,
      summary.status,
      TimeUtil.dateFromBackend(summary.creationTime),
      TimeUtil.dateFromBackend(summary.responseReceived),
      summary.creator,
      summary.respondent,
      summary.updateWithoutRequest,
      summary.requestedFields,
      summary.responseFields
    );
  }
}
