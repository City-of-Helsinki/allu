import {Injectable} from '@angular/core';
import {Location} from '../../model/common/location';
import {ApplicationCategoryType} from '../../feature/application/type/application-category';
import {ApplicationType} from '../../model/application/type/application-type';
import {ApplicationSpecifier} from '../../model/application/type/application-specifier';

@Injectable()
export class LocationState {
  public location = new Location();
  public startDate: Date;
  public endDate: Date;
  public category: ApplicationCategoryType;
  public applicationType: ApplicationType;
  public specifiers: Array<ApplicationSpecifier> = [];

  public clear() {
    this.location = new Location();
    this.startDate = undefined;
    this.endDate = undefined;
    this.category = undefined;
    this.applicationType = undefined;
    this.specifiers = [];
  }
}
