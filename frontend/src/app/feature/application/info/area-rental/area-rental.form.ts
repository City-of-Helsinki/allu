import {TimePeriod} from '@feature/application/info/time-period';
import {Application} from '@model/application/application';
import {ApplicationForm} from '@feature/application/info/application-form';
import {AreaRental} from '@model/application/area-rental/area-rental';

export interface AreaRentalForm extends ApplicationForm {
  validityTimes?: TimePeriod;
  pksCard?: boolean;
  majorDisturbance?: boolean;
  workFinished?: Date;
  customerWorkFinished?: Date;
  trafficArrangements?: string;
  trafficArrangementImpedimentType?: string;
  workPurpose?: string;
  additionalInfo?: string;
  terms?: string;
}

export function to(form: AreaRentalForm): AreaRental {
  const areaRental = new AreaRental();
  areaRental.pksCard = form.pksCard;
  areaRental.majorDisturbance = form.majorDisturbance;
  areaRental.workFinished = form.workFinished;
  areaRental.customerWorkFinished = form.customerWorkFinished;
  areaRental.trafficArrangements = form.trafficArrangements;
  areaRental.trafficArrangementImpedimentType = form.trafficArrangementImpedimentType;
  areaRental.workPurpose = form.workPurpose;
  areaRental.additionalInfo = form.additionalInfo;
  areaRental.terms = form.terms;
  return areaRental;
}

export function from(application: Application, areaRental: AreaRental) {
  return {
    name: application.name || 'Aluevuokraus', // Area rentals have no name so set default
    validityTimes: new TimePeriod(application.startTime, application.endTime),
    pksCard: areaRental.pksCard,
    majorDisturbance: areaRental.majorDisturbance,
    workFinished: areaRental.workFinished,
    customerWorkFinished: areaRental.customerWorkFinished,
    trafficArrangements: areaRental.trafficArrangements,
    trafficArrangementImpedimentType: areaRental.trafficArrangementImpedimentType,
    workPurpose: areaRental.workPurpose,
    additionalInfo: areaRental.additionalInfo,
    terms: areaRental.terms
  };
}
