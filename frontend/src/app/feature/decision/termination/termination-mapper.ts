import {TerminationInfo} from '@feature/decision/termination/termination-info';
import {TimeUtil} from '@util/time.util';

export interface BackendTerminationInfo {
  id: number;
  applicationId: number;
  creationTime: string;
  terminationTime: string;
  reason: string;
}

export class TerminationInfoMapper {
  static mapBackend(backendTerminationInfo: BackendTerminationInfo): TerminationInfo {
    return new TerminationInfo(
      false,
      backendTerminationInfo.id,
      backendTerminationInfo.applicationId,
      null,
      null,
      TimeUtil.dateFromBackend(backendTerminationInfo.creationTime),
      TimeUtil.dateFromBackend(backendTerminationInfo.terminationTime),
      backendTerminationInfo.reason
    );
  }

  static mapFrontEnd(terminationInfo: TerminationInfo): BackendTerminationInfo {
    return {
      id: terminationInfo.id,
      applicationId: terminationInfo.applicationId,
      creationTime: TimeUtil.dateToBackend(terminationInfo.creationTime),
      terminationTime: TimeUtil.dateToBackend(terminationInfo.terminationTime),
      reason: terminationInfo.comment
    };
  }
}
