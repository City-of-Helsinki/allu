import {AttributeMeta} from './attribute-meta';
import {AttributeDataType} from './attribute-data-type';
import {StringUtil} from '../../../util/string.util';
import {ArrayUtil} from '../../../util/array-util';
export class StructureMeta {
  constructor(public typeName: string, public version: number, public attributes: Array<AttributeMeta>) {
  }

  public uiName(...path: string[]): string {
    return this.getAttribute(StringUtil.toPath(path, '/')).uiName;
  }

  public dataType(...path: string[]): string {
    return this.getAttribute(StringUtil.toPath(path, '/')).dataType;
  }

  private getAttribute(...path: string[]): AttributeMeta {
    let fullPath = StringUtil.toPath(path, '/');
    let currentAttributeName = fullPath.replace( /\d+/g, '*');
    let attributeMeta = ArrayUtil.first(this.attributes, this.attributeMatcher(currentAttributeName));
    if (!attributeMeta) {
      attributeMeta = new AttributeMeta('unknown', fullPath, AttributeDataType[AttributeDataType.STRING], undefined);
    }
    return attributeMeta;
  }

  private attributeMatcher(matches: string): (attr: AttributeMeta) => boolean {
    return (a) => matches === a.name;
  }
}
