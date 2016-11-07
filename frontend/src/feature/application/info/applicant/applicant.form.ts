import {Applicant} from '../../../../model/application/applicant';
import {ApplicantDetails} from '../../../../model/common/applicant-details';
import {Person} from '../../../../model/common/person';
import {Organization} from '../../../../model/common/organization';
import {PostalAddress} from '../../../../model/common/postal-address';
export class ApplicantForm {

  constructor(
    public id?: number,
    public type?: string,
    public representative?: boolean,
    public detailsId?: number,
    public name?: string,
    public identifier?: string,
    public country?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string
  ) {}

  static fromApplicant(applicant: Applicant): ApplicantForm {
    return new ApplicantForm(
      applicant.id,
      applicant.type,
      applicant.representative,
      applicant.details.id,
      applicant.details.name,
      applicant.details.identifier,
      'Suomi',
      applicant.details.postalAddress,
      applicant.details.email,
      applicant.details.phone
    );
  }

  static toApplicant(form: ApplicantForm): Applicant {
    let applicant = new Applicant();
    applicant.id = form.id;
    applicant.type = form.type;
    applicant.representative = form.representative;

    if (form.type === 'PERSON') {
      applicant.person = new Person(
        form.detailsId,
        form.name,
        form.identifier,
        form.postalAddress,
        form.email,
        form.phone);
    } else {
      applicant.organization = new Organization(
        form.detailsId,
        form.name,
        form.identifier,
        form.postalAddress,
        form.email,
        form.phone
      );
    }
    return applicant;
  }
}
