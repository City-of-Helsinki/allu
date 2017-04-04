import {TimePeriod} from '../time-period';
import {ApplicantForm} from '../applicant/applicant.form';
import {Contact} from '../../../../model/application/contact';
import {Some} from '../../../../util/option';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';
import {AreaRental} from '../../../../model/application/area-rental/area-rental';

export class AreaRentalForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public applicant?: ApplicantForm,
    public contacts?: Array<Contact>,
    public contractor?: ApplicantForm,
    public responsiblePerson?: Array<Contact>,
    public workFinished?: string,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public additionalInfo?: string
  ) {}

  static to(form: AreaRentalForm): AreaRental {
    let areaRental = new AreaRental();
    areaRental.contractor = Some(form.contractor).map(contractor => ApplicantForm.toApplicant(contractor)).orElse(undefined);
    areaRental.responsiblePerson = Some(form.responsiblePerson).filter(persons => persons.length > 0).map(c => c[0]).orElse(undefined);
    areaRental.uiWorkFinished = form.workFinished;
    areaRental.trafficArrangements = form.trafficArrangements;
    areaRental.trafficArrangementImpedimentType = form.trafficArrangementImpedimentType;
    areaRental.additionalInfo = form.additionalInfo;
    return areaRental;
  }

  static from(application: Application, areaRental: AreaRental) {
    return new AreaRentalForm(
      new TimePeriod(application.uiStartTime, application.uiEndTime),
      undefined, // these are added by subcomponents (application and contact)
      undefined,
      undefined,
      undefined,
      areaRental.uiWorkFinished,
      areaRental.trafficArrangements,
      areaRental.trafficArrangementImpedimentType,
      areaRental.additionalInfo
    );
  }
}
