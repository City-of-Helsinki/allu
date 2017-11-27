export class BlobFile {
  constructor(
    public content: Blob,
    public name: string,
    public lastModified = new Date()) {
  }
}
