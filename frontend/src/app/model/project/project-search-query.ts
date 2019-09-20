export class ProjectSearchQuery {
  public id?: number;
  public identifier?: string;
  public startTime?: Date;
  public endTime?: Date;
  public ownerName?: string;
  public onlyActive?: boolean;
  public districts?: Array<string>;
  public creator?: number;
}

export function fromForm(form: ProjectSearchQueryForm): ProjectSearchQuery {
  const query = new ProjectSearchQuery();
  query.id = form.id;
  query.identifier = form.identifier;
  query.startTime = form.startTime;
  query.endTime = form.endTime;
  query.ownerName = form.ownerName;
  query.onlyActive = form.onlyActive;
  query.districts = form.districts;
  query.creator = form.creator;
  return query;
}

interface ProjectSearchQueryForm {
  id: number;
  identifier: string;
  startTime: Date;
  endTime: Date;
  ownerName: string;
  onlyActive: boolean;
  districts: Array<string>;
  creator: number;
}
