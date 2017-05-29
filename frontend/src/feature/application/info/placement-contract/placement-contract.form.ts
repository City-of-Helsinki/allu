import {TimePeriod} from '../time-period';
import {Application} from '../../../../model/application/application';
import {PlacementContract} from '../../../../model/application/placement-contract/placement-contract';
import {ApplicationForm} from '../application-form';

export class PlacementContractForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public diaryNumber?: string,
    public calculatedPrice?: number,
    public priceOverride?: number,
    public priceOverrideReason?: string,
    public additionalInfo?: string,
    public generalTerms?: string
  ) {}

  static to(form: PlacementContractForm, specifiers: Array<string>): PlacementContract {
    let placementContract = new PlacementContract();
    placementContract.diaryNumber = form.diaryNumber;
    placementContract.additionalInfo = form.additionalInfo;
    placementContract.generalTerms = form.generalTerms;
    placementContract.specifiers = specifiers;
    return placementContract;
  }

  static from(application: Application, contract: PlacementContract) {
    return new PlacementContractForm(
      new TimePeriod(application.startTime, application.endTime),
      contract.diaryNumber,
      application.calculatedPriceEuro,
      application.priceOverrideEuro,
      application.priceOverrideReason,
      contract.additionalInfo,
      contract.generalTerms);
  }
}
