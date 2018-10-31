import {TimePeriod} from '@feature/application/info/time-period';
import {Application} from '@model/application/application';
import {TrafficArrangement} from '@model/application/traffic-arrangement/traffic-arrangement';
import {ApplicationForm} from '@feature/application/info/application-form';

export interface TrafficArrangementForm extends ApplicationForm {
   validityTimes?: TimePeriod;
   trafficArrangements?: string;
   trafficArrangementImpedimentType?: string;
   workPurpose?: string;
   terms?: string;
}

export function to(form: TrafficArrangementForm): TrafficArrangement {
  const arrangement = new TrafficArrangement();
  arrangement.trafficArrangements = form.trafficArrangements;
  arrangement.trafficArrangementImpedimentType = form.trafficArrangementImpedimentType;
  arrangement.workPurpose = form.workPurpose;
  arrangement.terms = form.terms;
  return arrangement;
}

export function from(application: Application, arrangement: TrafficArrangement) {
  return {
    name: application.name || 'Liikennejärjestely', // Traffic arrangements have no name so set default
    validityTimes: new TimePeriod(application.startTime, application.endTime),
    trafficArrangements: arrangement.trafficArrangements,
    trafficArrangementImpedimentType: arrangement.trafficArrangementImpedimentType,
    workPurpose: arrangement.workPurpose,
    terms: arrangement.terms
  };
}
