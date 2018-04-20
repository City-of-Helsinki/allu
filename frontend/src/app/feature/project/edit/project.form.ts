import {Project} from '../../../model/project/project';
import {Customer} from '../../../model/customer/customer';
import {Contact} from '../../../model/customer/contact';

export class ProjectForm {
  constructor()
  constructor(
    id: number,
    name: string,
    customer: Customer,
    contact: Contact,
    customerReference: string,
    additionalInfo: string)
  constructor(
    public id?: number,
    public name?: string,
    public customer?: Customer,
    public contact?: Contact,
    public customerReference?: string,
    public additionalInfo?: string) {}

  static fromProject(project: Project): ProjectForm {
    return new ProjectForm(
      project.id,
      project.name,
      project.customer,
      project.contact,
      project.customerReference,
      project.additionalInfo);
  }

  static toProject(form: ProjectForm): Project {
    const project =  new Project();
    project.id = form.id;
    project.name = form.name;
    project.customer = form.customer;
    project.contact = form.contact;
    project.customerReference = form.customerReference;
    project.additionalInfo = form.additionalInfo;
    return project;
  }
}
