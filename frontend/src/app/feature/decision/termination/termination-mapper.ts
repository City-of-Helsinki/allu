import {TerminationInfo} from '@feature/decision/termination/termination-info';
import {TimeUtil} from '@util/time.util';

export interface BackendTerminationInfo {
  id: number;
  applicationId: number;
  creationTime: string;
  terminationTime: string;
  reason: string;
  terminator: number;
}

export class TerminationInfoMapper {
  static mapBackend(backendTerminationInfo: BackendTerminationInfo): TerminationInfo {
    if (!backendTerminationInfo) {
      return undefined;
    }
    return new TerminationInfo(
      false,
      backendTerminationInfo.id,
      backendTerminationInfo.applicationId,
      null,
      backendTerminationInfo.terminator,
      TimeUtil.dateFromBackend(backendTerminationInfo.creationTime),
      TimeUtil.dateFromBackend(backendTerminationInfo.terminationTime),
      backendTerminationInfo.reason
    );
  }

  static mapFrontEnd(terminationInfo: TerminationInfo): BackendTerminationInfo {
    if (!terminationInfo) {
      return undefined;
    }
    return {
      id: terminationInfo.id,
      applicationId: terminationInfo.applicationId,
      creationTime: TimeUtil.dateToBackend(terminationInfo.creationTime),
      terminationTime: TimeUtil.dateToBackend(terminationInfo.terminationTime),
      reason: terminationInfo.comment,
      terminator: terminationInfo.owner
    };
  }
}
