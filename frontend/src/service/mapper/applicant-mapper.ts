import {BackendApplicant} from '../backend-model/backend-applicant';
import {Applicant} from '../../model/application/applicant';
import {PersonMapper} from './person-mapper';
import {OrganizationMapper} from './organization-mapper';

export class ApplicantMapper {

  public static mapBackend(backendApplicant: BackendApplicant): Applicant {
    return (backendApplicant) ?
      new Applicant(
        backendApplicant.id,
        backendApplicant.type,
        PersonMapper.mapBackend(backendApplicant.person),
        OrganizationMapper.mapBackend(backendApplicant.organization)) : undefined;
  }
  public static mapFrontend(applicant: Applicant): BackendApplicant {
    return (applicant) ?
    {
      id: applicant.id,
      type: applicant.type,
      person: PersonMapper.mapFrontend(applicant.person),
      organization: OrganizationMapper.mapFrontend(applicant.organization)
    } : undefined;
  }
}
