import {BackendApplicant} from '../backend-model/backend-applicant';
import {Applicant} from '../../model/application/applicant/applicant';
import {PostalAddress} from '../../model/common/postal-address';
import {BackendApplicantWithContacts} from '../backend-model/BackendApplicantWithContacts';
import {ApplicantWithContacts} from '../../model/application/applicant/applicant-with-contacts';
import {ContactMapper} from './contact-mapper';

export class ApplicantMapper {

  public static mapBackend(backendApplicant: BackendApplicant): Applicant {
    if (backendApplicant) {
      let postalAddress = undefined;
      if (backendApplicant.postalAddress) {
        postalAddress = new PostalAddress(
          backendApplicant.postalAddress.streetAddress, backendApplicant.postalAddress.postalCode, backendApplicant.postalAddress.city);
      }

      return new Applicant(
        backendApplicant.id,
        backendApplicant.type,
        backendApplicant.representative,
        backendApplicant.name,
        backendApplicant.registryKey,
        postalAddress,
        backendApplicant.email,
        backendApplicant.phone,
        backendApplicant.active);
    } else {
      return undefined;
    }
  }

  public static mapFrontend(applicant: Applicant): BackendApplicant {
    return (applicant) ?
    {
      id: applicant.id,
      type: applicant.type,
      representative: applicant.representative,
      name: applicant.name,
      registryKey: applicant.registryKey,
      postalAddress: (applicant.postalAddress) ?
        { streetAddress: applicant.postalAddress.streetAddress,
          postalCode: applicant.postalAddress.postalCode,
          city: applicant.postalAddress.city } : undefined,
      email: applicant.email,
      phone: applicant.phone,
      active: applicant.active
    } : undefined;

  }

  public static mapBackendWithContacts(applicant: BackendApplicantWithContacts): ApplicantWithContacts {
    return new ApplicantWithContacts(
      ApplicantMapper.mapBackend(applicant.applicant),
      applicant.contacts.map(contact => ContactMapper.mapBackend(contact))
    );
  }

  public static mapFrontendWithContacts(applicant: ApplicantWithContacts): BackendApplicantWithContacts {
    return {
      applicant: applicant.applicant ? ApplicantMapper.mapFrontend(applicant.applicant) : undefined,
      contacts: applicant.contacts ? applicant.contacts.map(contact => ContactMapper.mapFrontend(contact)) : []
    };
  }
}
