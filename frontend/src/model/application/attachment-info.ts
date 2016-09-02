export class AttachmentInfo {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public size: number,
    public creationTime: Date,
    public file: any
  ) {};
}
