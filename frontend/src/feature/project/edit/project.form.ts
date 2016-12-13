import {Project} from '../../../model/project/project';

export class ProjectForm {
  constructor()
  constructor(
    id: number,
    name: string,
    ownerName: string,
    contactName: string,
    email: string,
    phone: string,
    customerReference: string,
    additionalInfo: string)
  constructor(
    public id?: number,
    public name?: string,
    public ownerName?: string,
    public contactName?: string,
    public email?: string,
    public phone?: string,
    public customerReference?: string,
    public additionalInfo?: string) {}

  static fromProject(project: Project): ProjectForm {
    return new ProjectForm(
      project.id,
      project.name,
      project.ownerName,
      project.contactName,
      project.email,
      project.phone,
      project.customerReference,
      project.additionalInfo);
  }

  static toProject(form: ProjectForm): Project {
    let project =  new Project();
    project.id = form.id;
    project.name = form.name;
    project.ownerName = form.ownerName;
    project.contactName = form.contactName;
    project.email = form.email;
    project.phone = form.phone;
    project.customerReference = form.customerReference;
    project.additionalInfo = form.additionalInfo;
    return project;
  }
}
