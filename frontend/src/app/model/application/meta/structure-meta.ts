import {AttributeMeta} from './attribute-meta';
import {AttributeDataType} from './attribute-data-type';
import {StringUtil} from '@util/string.util';
import {ArrayUtil} from '@util/array-util';
import {TimeUtil} from '@util/time.util';
import {findTranslation} from '@util/translations';
import {Some} from '@util/option';

const META_PATH_PREFIX = '/';

export class StructureMeta {
  constructor(public typeName: string, public version: number, public attributes: Array<AttributeMeta>) {
  }

  public uiName(path: string, separator = '.'): string {
    const pathParts = path.split(separator);
    return this.getAttribute(StringUtil.toPath(pathParts, '/')).uiName;
  }

  public dataType(path: string, separator = '.'): string {
    const pathParts = path.split(separator);
    return this.getAttribute(StringUtil.toPath(pathParts, '/')).dataType;
  }

  public contains(path: string, separator = '.'): boolean {
    const pathParts = path.split(separator);
    return !!this.getFirstMatching(...pathParts);
  }

  private getAttribute(...path: string[]): AttributeMeta {
    const fullPath = StringUtil.toPath(path, '/');
    let attributeMeta = this.getFirstMatching(...path);
    if (!attributeMeta) {
      attributeMeta = new AttributeMeta('unknown', fullPath, AttributeDataType[AttributeDataType.STRING], undefined);
    }
    return attributeMeta;
  }

  private getFirstMatching(...path: string[]): AttributeMeta {
    const fullPath = StringUtil.toPath(path, '/');
    const metaPath = `${META_PATH_PREFIX}${fullPath}`;
    const currentAttributeName = metaPath.replace( /\d+/g, '*');
    return ArrayUtil.first(this.attributes, this.attributeMatcher(currentAttributeName));
  }

  private attributeMatcher(matches: string): (attr: AttributeMeta) => boolean {
    return (a) => matches === a.name;
  }
}

export function formatValue(path: string, value: string, meta: StructureMeta): string {
  const dataType = AttributeDataType[meta.dataType(path)];
  switch (dataType) {
    case AttributeDataType.DATETIME:
      return TimeUtil.formatHistoryDateTimeString(value);
    case AttributeDataType.BOOLEAN:
      return formatAndTranslate('common.boolean', value);
    case AttributeDataType.ENUMERATION:
      return formatNonEmpty(path, value, meta);
    default:
      return value;
  }
}

function formatAndTranslate(prefix: string, value: string) {
  return !StringUtil.isEmpty(value)
    ? findTranslation([prefix, value])
    : '';
}

function formatNonEmpty(path: string, value: string, meta: StructureMeta) {
  const pathWithValue = `${path}.${value}`;
  return Some(value)
    .filter(v => !StringUtil.isEmpty(v))
    .map(v => meta.uiName(pathWithValue))
    .orElse('');
}
