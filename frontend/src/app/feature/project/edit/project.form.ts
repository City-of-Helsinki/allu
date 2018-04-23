import {Project} from '../../../model/project/project';
import {Some} from '../../../util/option';
import {Customer} from '../../../model/customer/customer';
import {Contact} from '../../../model/customer/contact';

export class ProjectForm {
  constructor(
    public id?: number,
    public name?: string,
    public identifier?: string,
    public customerType?: string,
    public customer?: Customer,
    public contact?: Contact,
    public contactPhone?: string,
    public contactEmail?: string,
    public customerReference?: string,
    public additionalInfo?: string) {}

  static fromProject(project: Project): ProjectForm {
    return new ProjectForm(
      project.id,
      project.name,
      project.identifier,
      Some(project.customer).map(c => c.type).orElse(undefined),
      project.customer,
      project.contact,
      Some(project.contact).map(c => c.phone).orElse(undefined),
      Some(project.contact).map(c => c.email).orElse(undefined),
      project.customerReference,
      project.additionalInfo);
  }

  static toProject(form: ProjectForm): Project {
    const project =  new Project();
    project.id = form.id;
    project.name = form.name;
    project.identifier = form.identifier;
    project.customer = form.customer;
    project.contact = form.contact;
    project.customerReference = form.customerReference;
    project.additionalInfo = form.additionalInfo;
    return project;
  }
}
