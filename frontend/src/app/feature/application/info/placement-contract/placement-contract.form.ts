import {TimePeriod} from '../time-period';
import {Application} from '../../../../model/application/application';
import {PlacementContract} from '../../../../model/application/placement-contract/placement-contract';
import {ApplicationForm} from '../application-form';

export class PlacementContractForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public identificationNumber?: string,
    public propertyIdentificationNumber?: string,
    public calculatedPrice?: number,
    public additionalInfo?: string,
    public contractText?: string,
    public terms?: string,
    public terminationDate?: Date
  ) {}

  static to(form: PlacementContractForm): PlacementContract {
    const placementContract = new PlacementContract();
    placementContract.identificationNumber = form.identificationNumber;
    placementContract.propertyIdentificationNumber = form.propertyIdentificationNumber,
    placementContract.additionalInfo = form.additionalInfo;
    placementContract.contractText = form.contractText;
    placementContract.terms = form.terms;
    placementContract.terminationDate = form.terminationDate;
    return placementContract;
  }

  static from(application: Application, contract: PlacementContract) {
    return new PlacementContractForm(
      new TimePeriod(application.startTime, application.endTime),
      contract.identificationNumber,
      contract.propertyIdentificationNumber,
      application.calculatedPriceEuro,
      contract.additionalInfo,
      contract.contractText,
      contract.terms,
      contract.terminationDate);
  }
}
