import {TimePeriod} from '../time-period';
import {Application} from '../../../../model/application/application';
import {PlacementContract} from '../../../../model/application/placement-contract/placement-contract';
import {ApplicationForm} from '../application-form';
import {NumberUtil} from '../../../../util/number.util';

export class PlacementContractForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public propertyIdentificationNumber?: string,
    public calculatedPrice?: number,
    public additionalInfo?: string,
    public contractText?: string,
    public TERMS?: string,
    public terminationDate?: Date
  ) {}

  static to(form: PlacementContractForm): PlacementContract {
    const placementContract = new PlacementContract();
    placementContract.propertyIdentificationNumber = form.propertyIdentificationNumber,
    placementContract.additionalInfo = form.additionalInfo;
    placementContract.contractText = form.contractText;
    placementContract.terms = form.TERMS;
    placementContract.terminationDate = form.terminationDate;
    return placementContract;
  }

  static from(application: Application, contract: PlacementContract) {
    return new PlacementContractForm(
      new TimePeriod(application.startTime, application.endTime),
      contract.propertyIdentificationNumber,
      NumberUtil.toEuros(application.calculatedPrice),
      contract.additionalInfo,
      contract.contractText,
      contract.terms,
      contract.terminationDate);
  }
}
