import {AttributeMeta} from './attribute-meta';
import {AttributeDataType} from './attribute-data-type';
import {StringUtil} from '../../../util/string.util';
export class StructureMeta {
  constructor(public typeName: string, public version: number, public attributes: Array<AttributeMeta>) {}

  public uiName(path: string | string[]): string {
    return this.getPath(this, StringUtil.toPath(path, '/')).uiName;
  }

  public dataType(path: string | string[]): string {
    return this.getPath(this, StringUtil.toPath(path, '/')).dataType;
  }

  private getPath(structureMeta: StructureMeta, path: string): AttributeMeta {
    let pathComponents = path.split('\.');
    let currentAttributeName = pathComponents[0];
    let attributeMeta = structureMeta.attributes.filter(attr => attr.name === currentAttributeName)[0];
    if (!attributeMeta || pathComponents.length > 1) {
      attributeMeta = new AttributeMeta('unknown', path, AttributeDataType[AttributeDataType.STRING], undefined);
    }
    return attributeMeta;
  }
}
