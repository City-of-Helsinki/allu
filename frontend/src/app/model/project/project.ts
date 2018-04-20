import {TimeUtil} from '../../util/time.util';
import {Customer} from '../customer/customer';
import {Contact} from '../customer/contact';

export class Project {
  public active: boolean;

  constructor(
    public id?: number,
    public name?: string,
    public identifier?: string,
    public startTime?: Date,
    public endTime?: Date,
    public cityDistricts: Array<number> = [],
    public customer?: Customer,
    public contact?: Contact,
    public customerReference?: string,
    public additionalInfo?: string,
    public parentId?: number) {
    this.active = !!startTime && !!endTime && TimeUtil.isBetweenInclusive(new Date(), this.startTime, this.endTime);
  }
}
