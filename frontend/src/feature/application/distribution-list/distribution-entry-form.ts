import {PostalAddress} from '../../../model/common/postal-address';
import {DistributionEntry} from '../../../model/common/distribution-entry';
import {Some} from '../../../util/option';

export class DistributionEntryForm {
  constructor(
    public id?: number,
    public name?: string,
    public type?: string,
    public email?: string,
    public streetAddress?: string,
    public postalCode?: string,
    public city?: string
  ) {}

  static to(form: DistributionEntryForm): DistributionEntry {
    let entry = new DistributionEntry(form.id, form.name);
    entry.uiType = form.type;
    entry.email = form.email;
    if (form.streetAddress) {
      entry.postalAddress = new PostalAddress(form.streetAddress, form.postalCode, form.city);
    }
    return entry;
  }

  static from(entry: DistributionEntry): DistributionEntryForm {
    let form = new DistributionEntryForm(entry.id, entry.name, entry.uiType, entry.email);
    Some(entry.postalAddress).do(address => {
      form.streetAddress = address.streetAddress;
      form.postalCode = address.postalCode;
      form.city = address.city;
    });
    return form;
  }
}

