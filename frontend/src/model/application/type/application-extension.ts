export abstract class ApplicationExtension {
  constructor(public applicationType?: string, public specifiers?: Array<string>) {
    this.specifiers = this.specifiers || [];
  }
}
