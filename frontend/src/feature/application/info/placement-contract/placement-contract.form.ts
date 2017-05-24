import {TimePeriod} from '../time-period';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';
import {Some} from '../../../../util/option';
import {Application} from '../../../../model/application/application';
import {PlacementContract} from '../../../../model/application/placement-contract/placement-contract';
import {ApplicationForm} from '../application-form';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';
import {ArrayUtil} from '../../../../util/array-util';

export class PlacementContractForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public applicant?: CustomerWithContactsForm,
    public representative?: CustomerWithContactsForm,
    public diaryNumber?: string,
    public calculatedPrice?: number,
    public priceOverride?: number,
    public priceOverrideReason?: string,
    public additionalInfo?: string,
    public generalTerms?: string
  ) {}

  static to(form: PlacementContractForm, specifiers: Array<string>): PlacementContract {
    let placementContract = new PlacementContract();
    Some(form.representative).do(pc => {
      placementContract.representative = CustomerForm.toCustomer(pc.customer);
      placementContract.contact = ArrayUtil.first(pc.contacts);
    });
    placementContract.diaryNumber = form.diaryNumber;
    placementContract.additionalInfo = form.additionalInfo;
    placementContract.generalTerms = form.generalTerms;
    placementContract.specifiers = specifiers;
    return placementContract;
  }

  static from(application: Application, contract: PlacementContract) {
    return new PlacementContractForm(
      new TimePeriod(application.startTime, application.endTime),
      undefined, // these are added by subcomponents (application and contact)
      undefined,
      contract.diaryNumber,
      application.calculatedPriceEuro,
      application.priceOverrideEuro,
      application.priceOverrideReason,
      contract.additionalInfo,
      contract.generalTerms);
  }
}
