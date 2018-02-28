import {ApplicationExtension} from '../type/application-extension';
import {TimeUtil} from '../../../util/time.util';

export class Event extends ApplicationExtension {
  constructor(public nature?: string,
              public description?: string,
              public url?: string,
              public applicationType?: string,
              public eventStartTime?: Date,
              public eventEndTime?: Date,
              public timeExceptions?: string,
              public attendees?: number,
              public entryFee?: number,
              public ecoCompass?: boolean,
              public foodSales?: boolean,
              public foodProviders?: string,
              public marketingProviders?: string,
              public structureArea?: number,
              public structureDescription?: string,
              public terms?: string) {
    super(applicationType, terms);
  }

  /*
   * Getters and setters for supporting pickadate editing in UI.
   */

  public get uiStartTime(): string {
    return TimeUtil.getUiDateString(this.eventStartTime);
  }

  public set uiStartTime(dateString: string) {
    this.eventStartTime = TimeUtil.getDateFromUi(dateString);
  }

  public get uiEndTime(): string {
    return TimeUtil.getUiDateString(this.eventEndTime);
  }

  public set uiEndTime(dateString: string) {
    this.eventEndTime = TimeUtil.getDateFromUi(dateString);
  }
}
