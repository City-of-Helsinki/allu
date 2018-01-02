export class BlobFile {
  constructor(
    public content: Blob,
    public name: string,
    public lastModified = new Date()) {
  }

  static empty() {
    const content = new Blob(['empty'], {});
    return new BlobFile(content, 'empty');
  }
}
