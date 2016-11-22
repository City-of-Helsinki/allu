export class Project {
  constructor()
  constructor(
    id: number,
    name: string,
    startTime: Date,
    endTime: Date,
    ownerName: string,
    contactName: string,
    email: string,
    phone: string,
    customerReference: string,
    additionalInfo: string,
    parentId: number)
  constructor(
    public id?: number,
    public name?: string,
    public startTime?: Date,
    public endTime?: Date,
    public ownerName?: string,
    public contactName?: string,
    public email?: string,
    public phone?: string,
    public customerReference?: string,
    public additionalInfo?: string,
    public parentId?: number) {}
}
