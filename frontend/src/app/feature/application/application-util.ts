import {Application} from '@model/application/application';
import {ApplicationStatus} from '@model/application/application-status';
import {NumberUtil} from '@util/number.util';

export class ApplicationUtil {
  public static validForInformationRequest(app: Application): boolean {
    const status = app.status;
    const validStatus = status === ApplicationStatus.PENDING || status === ApplicationStatus.HANDLING;
    const external =  NumberUtil.isDefined(app.externalOwnerId);
    return validStatus && external;
  }
}
