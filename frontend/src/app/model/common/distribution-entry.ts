import {DistributionType} from './distribution-type';
import {PostalAddress} from './postal-address';

export class DistributionEntry {
  constructor(
    public id?: number,
    public name?: string,
    public distributionType?: DistributionType,
    public email?: string,
    public postalAddress?: PostalAddress
  ) {
    this.postalAddress = postalAddress || new PostalAddress();
  }

  get uiType(): string {
    return DistributionType[this.distributionType];
  }

  set uiType(type: string) {
    this.distributionType = DistributionType[type];
  }
}
