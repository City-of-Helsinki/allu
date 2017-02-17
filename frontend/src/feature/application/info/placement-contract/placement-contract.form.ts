import {TimePeriod} from '../time-period';
import {ApplicantForm} from '../applicant/applicant.form';
import {Contact} from '../../../../model/application/contact';
import {Some} from '../../../../util/option';
import {Application} from '../../../../model/application/application';
import {PlacementContract} from '../../../../model/application/placement-contract/placement-contract';
import {ApplicationForm} from '../application-form';

export class PlacementContractForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public applicant?: ApplicantForm,
    public contacts?: Array<Contact>,
    public representative?: ApplicantForm,
    public contact?: Array<Contact>,
    public diaryNumber?: string,
    public additionalInfo?: string,
    public generalTerms?: string
  ) {}

  static to(form: PlacementContractForm): PlacementContract {
    let pc = new PlacementContract();
    pc.representative = Some(form.representative).map(representative => ApplicantForm.toApplicant(representative)).orElse(undefined);
    pc.contact = Some(form.contact).filter(contacts => contacts.length > 0).map(c => c[0]).orElse(undefined);
    pc.diaryNumber = form.diaryNumber;
    pc.additionalInfo = form.additionalInfo;
    pc.generalTerms = form.generalTerms;
    return pc;
  }

  static from(application: Application, contract: PlacementContract) {
    return new PlacementContractForm(
      new TimePeriod(application.uiStartTime, application.uiEndTime),
      undefined, // these are added by subcomponents (application and contact)
      undefined,
      undefined,
      undefined,
      contract.diaryNumber,
      contract.additionalInfo,
      contract.generalTerms
    );
  }
}
