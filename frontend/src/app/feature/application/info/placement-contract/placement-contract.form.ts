import {TimePeriod} from '@feature/application/info/time-period';
import {Application} from '@model/application/application';
import {PlacementContract} from '@model/application/placement-contract/placement-contract';
import {ApplicationForm} from '@feature/application/info/application-form';

export interface PlacementContractForm extends ApplicationForm {
  validityTimes?: TimePeriod;
  propertyIdentificationNumber?: string;
  additionalInfo?: string;
  contractText?: string;
  terms?: string;
  terminationDate?: Date;
  rationale?: string;
}

export function to(form: PlacementContractForm): PlacementContract {
  const placementContract = new PlacementContract();
  placementContract.propertyIdentificationNumber = form.propertyIdentificationNumber,
    placementContract.additionalInfo = form.additionalInfo;
  placementContract.contractText = form.contractText;
  placementContract.terms = form.terms;
  placementContract.terminationDate = form.terminationDate;
  placementContract.rationale = form.rationale;
  return placementContract;
}

export function from(application: Application, contract: PlacementContract) {
  return {
    name: application.name || 'Sijoitussopimus', // Placement contracts have no name so set default
    validityTimes: new TimePeriod(application.startTime, application.endTime),
    propertyIdentificationNumber: contract.propertyIdentificationNumber,
    additionalInfo: contract.additionalInfo,
    contractText: contract.contractText,
    terms: contract.terms,
    terminationDate: contract.terminationDate,
    rationale: contract.rationale
  };
}
