import {Injectable} from '@angular/core';

import {Project} from '../../model/project/project';
import {ProjectService} from './project.service';
import {ProjectSearchQuery} from '../../model/project/project-search-query';

@Injectable()
export class ProjectHub {

  constructor(private projectService: ProjectService) {}

  /**
   * Fetch single project with given id
   */
  public getProject = (id: number) => this.projectService.getProject(id);

  /**
   * Search projects with given search query
   */
  public searchProjects = (search: ProjectSearchQuery) => this.projectService.searchProjects(search);

  /**
   * Saves given project (create / update)
   */
  public save = (project: Project) => this.projectService.save(project);

  /**
   * Remove project with given id
   */
  public remove = (id: number) => this.projectService.remove(id);

  /**
   * Sets projects applications as given list of applications (empty array of id's removes all applications from project)
   */
  public updateProjectApplications = (id: number, applicationIds: Array<number>) =>
    this.projectService.updateProjectApplications(id, applicationIds);

  /**
   * Adds single application to project
   */
  public addProjectApplication = (id: number, applicationId: number) =>
    this.projectService.addProjectApplication(id, applicationId);

  /**
   * Fetches projects applications
   */
  public getProjectApplications = (id: number) => this.projectService.getProjectApplications(id);

  /**
   * Fetches childprojects of given project
   */
  public getChildProjects = (id: number) => this.projectService.getChildProjects(id);

  /**
   * Fetches all parents (and grandparents and ...) of given project
   */
  public getParentProjects = (id: number) => this.projectService.getParentProjects(id);

  /**
   * Removes parent from given projects
   */
  public updateParent = (id: number, parentId: number) => this.projectService.updateParent(id, parentId);

  /**
   * Removes parent from given projects
   */
  public removeParent = (ids: Array<number>) => this.projectService.removeParent(ids);
}
