import {BackendProject} from '../backend-model/backend-project';
import {Project} from '../../model/project/project';

export class ProjectMapper {

  public static mapBackend(backendProject: BackendProject): Project {
    return (backendProject) ?
      new Project(
        backendProject.id,
        backendProject.name,
        new Date(backendProject.startTime),
        new Date(backendProject.endTime),
        backendProject.ownerName,
        backendProject.contactName,
        backendProject.email,
        backendProject.phone,
        backendProject.customerReference,
        backendProject.additionalInfo,
        backendProject.parentId
      ) : undefined;
  }

  public static mapFrontend(project: Project): BackendProject {
    return (project) ? {
      id: project.id,
      name: project.name,
      startTime: (project.startTime) ? project.startTime.toISOString() : undefined,
      endTime: (project.endTime) ? project.endTime.toISOString() : undefined,
      ownerName: project.ownerName,
      contactName: project.contactName,
      email: project.email,
      phone: project.phone,
      customerReference: project.customerReference,
      additionalInfo: project.additionalInfo,
      parentId: project.parentId
    } : undefined;
  }
}
