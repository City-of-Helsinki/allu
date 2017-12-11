import {TimePeriod} from '../time-period';
import {Application} from '../../../../model/application/application';
import {PlacementContract} from '../../../../model/application/placement-contract/placement-contract';
import {ApplicationForm} from '../application-form';

export class PlacementContractForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public diaryNumber?: string,
    public calculatedPrice?: number,
    public additionalInfo?: string,
    public generalTerms?: string,
    public terms?: string
  ) {}

  static to(form: PlacementContractForm): PlacementContract {
    const placementContract = new PlacementContract();
    placementContract.diaryNumber = form.diaryNumber;
    placementContract.additionalInfo = form.additionalInfo;
    placementContract.generalTerms = form.generalTerms;
    placementContract.terms = form.terms;
    return placementContract;
  }

  static from(application: Application, contract: PlacementContract) {
    return new PlacementContractForm(
      new TimePeriod(application.startTime, application.endTime),
      contract.diaryNumber,
      application.calculatedPriceEuro,
      contract.additionalInfo,
      contract.generalTerms,
      contract.terms);
  }
}
