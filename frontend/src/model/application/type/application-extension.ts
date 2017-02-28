export abstract class ApplicationExtension {
  constructor(public applicationType?: string,
              public specifiers?: Array<string>,
              public terms?: string) {
    this.specifiers = this.specifiers || [];
  }
}
