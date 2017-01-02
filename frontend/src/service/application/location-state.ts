import {Injectable} from '@angular/core';
import {Location} from '../../model/common/location';
import {ApplicationType} from '../../model/application/type/application-type';
import {ApplicationSpecifier} from '../../model/application/type/application-specifier';
import {ApplicationKind} from '../../model/application/type/application-kind';
import {Application} from '../../model/application/application';
import {TimeUtil} from '../../util/time.util';
import {Some} from '../../util/option';
import {ApplicationExtension} from '../../model/application/type/application-extension';
import {Event} from '../../model/application/event/event';
import {CableReport} from '../../model/application/cable-report/cable-report';
import {ShortTermRental} from '../../model/application/short-term-rental/short-term-rental';
import {ExcavationAnnouncement} from '../../model/application/excavation-announcement/excavation-announcement';
import {Note} from '../../model/application/note/note';

@Injectable()
export class LocationState {
  public location = new Location();
  public startDate: Date;
  public endDate: Date;
  public applicationType: ApplicationType;
  public applicationKind: ApplicationKind;
  public specifiers: Array<ApplicationSpecifier> = [];
  public relatedProject: number;

  public clear() {
    this.location = new Location();
    this.startDate = undefined;
    this.endDate = undefined;
    this.applicationType = undefined;
    this.applicationKind = undefined;
    this.specifiers = [];
  }

  public createApplication(): Application {
    let app = new Application();

    app.location = this.location;

    // TODO: mismatch here. Date+time should be used in location too.
    let defaultDate = new Date();
    app.startTime = this.startDate || defaultDate;
    app.endTime = TimeUtil.getEndOfDay(this.endDate || defaultDate);

    app.type = Some(this.applicationType).map(type => ApplicationType[type]).orElse(undefined);
    app.kind = Some(this.applicationKind).map(kind => ApplicationKind[kind]).orElse(undefined);
    app.extension = this.createExtension();
    return app;
  }

  private createExtension(): ApplicationExtension {
    return Some(this.applicationType)
      .map(type => this.createEmptyExtension(type))
      .map(ext => {
        ext.specifiers = this.specifiers.map(s => ApplicationSpecifier[s]);
        ext.applicationType = ApplicationType[this.applicationType];
        return ext;
      }).orElse(undefined);
  }

  private createEmptyExtension(type: ApplicationType): ApplicationExtension {
    switch (type) {
      case ApplicationType.CABLE_REPORT:
        return new CableReport();
      case ApplicationType.EVENT:
        return new Event();
      case ApplicationType.SHORT_TERM_RENTAL:
        return new ShortTermRental();
      case ApplicationType.EXCAVATION_ANNOUNCEMENT:
        return new ExcavationAnnouncement();
      case ApplicationType.NOTE:
        return new Note();
      default:
        throw new Error('Extension for ' + ApplicationType[this.applicationType] + ' not implemented yet');
    }
  }
}
