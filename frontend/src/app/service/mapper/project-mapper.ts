import {BackendProject} from '../backend-model/backend-project';
import {Project} from '../../model/project/project';
import {CustomerMapper} from './customer-mapper';
import {ContactMapper} from './contact-mapper';
import {Some} from '../../util/option';

export class ProjectMapper {

  public static mapBackend(backendProject: BackendProject): Project {
    return (backendProject) ?
      new Project(
        backendProject.id,
        backendProject.name,
        backendProject.identifier,
        Some(backendProject.startTime).map(start => new Date(start)).orElse(undefined),
        Some(backendProject.endTime).map(end => new Date(end)).orElse(undefined),
        backendProject.cityDistricts,
        CustomerMapper.mapBackend(backendProject.customer),
        ContactMapper.mapBackend(backendProject.contact),
        backendProject.customerReference,
        backendProject.additionalInfo,
        backendProject.parentId
      ) : undefined;
  }

  public static mapFrontend(project: Project): BackendProject {
    return (project) ? {
      id: project.id,
      name: project.name,
      identifier: project.identifier,
      startTime: (project.startTime) ? project.startTime.toISOString() : undefined,
      endTime: (project.endTime) ? project.endTime.toISOString() : undefined,
      cityDistricts: project.cityDistricts,
      customer: CustomerMapper.mapFrontend(project.customer),
      contact: ContactMapper.mapFrontend(project.contact),
      customerReference: project.customerReference,
      additionalInfo: project.additionalInfo,
      parentId: project.parentId
    } : undefined;
  }
}
