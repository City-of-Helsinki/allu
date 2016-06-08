
import {BackendProject} from '../backend-model/backend-project';
import {Project} from '../../model/application/project';
export class ProjectMapper {

  public static mapBackend(backendProject: BackendProject): Project {
    return (backendProject) ?
      new Project(backendProject.id, backendProject.name, backendProject.type, backendProject.information) : undefined;
  }

  public static mapFrontend(project: Project): BackendProject {
    return (project) ? {
      id: project.id,
      name: project.name,
      type: project.type,
      information: project.information
    } : undefined;
  }
}
