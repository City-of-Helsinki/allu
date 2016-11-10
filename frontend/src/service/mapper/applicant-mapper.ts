import {BackendApplicant} from '../backend-model/backend-applicant';
import {Applicant} from '../../model/application/applicant';
import {PostalAddress} from '../../model/common/postal-address';

export class ApplicantMapper {

  public static mapBackend(backendApplicant: BackendApplicant): Applicant {
    let postalAddress = undefined;
    if (backendApplicant.postalAddress) {
      postalAddress = new PostalAddress(
        backendApplicant.postalAddress.streetAddress, backendApplicant.postalAddress.postalCode, backendApplicant.postalAddress.city);
    }

    return (backendApplicant) ?
      new Applicant(
        backendApplicant.id,
        backendApplicant.type,
        backendApplicant.representative,
        backendApplicant.name,
        backendApplicant.registryKey,
        postalAddress,
        backendApplicant.email,
        backendApplicant.phone
      ) : undefined;
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
      phone: applicant.phone
    } : undefined;

  }
}
