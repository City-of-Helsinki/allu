import {AttributeMeta} from './attribute-meta';
export class StructureMeta {
  constructor(public applicationType: string, public version: number, public attributes: Array<AttributeMeta>) {}

  public getUiName(path: string): string {
    return this.getPath(this, path).uiName;
  }

  private getPath(structureMeta: StructureMeta, path: string) {
    let pathComponents = path.split('\.');
    let currentAttributeName = pathComponents[0];
    let attributeMeta = structureMeta.attributes.filter(attr => attr.name === currentAttributeName)[0];
    if (attributeMeta && attributeMeta.structureMeta && pathComponents.length > 1) {
      attributeMeta = this.getPath(attributeMeta.structureMeta, pathComponents.slice(1).join('.'));
    } else if (!attributeMeta || pathComponents.length > 1) {
      console.error('Attribute not found for path ' + path, structureMeta);
      throw new Error('Attribute not found for path ' + path);
    }
    return attributeMeta;
  }
}
