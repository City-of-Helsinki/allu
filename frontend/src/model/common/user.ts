export class User {

  constructor()
  constructor(
    id: number,
    userName: string,
    realName: string,
    emailAddress: string,
    title: string,
    isActive: boolean,
    allowedApplicationTypes: Array<string>,
    assignedRoles: Array<string>,
    cityDistrictIds: Array<number>
  )
  constructor(
    public id?: number,
    public userName?: string,
    public realName?: string,
    public emailAddress?: string,
    public title?: string,
    public isActive?: boolean,
    public allowedApplicationTypes?: Array<string>,
    public assignedRoles?: Array<string>,
    public cityDistrictIds?: Array<number>) {};
}
