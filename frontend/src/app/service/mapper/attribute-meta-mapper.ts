import {BackendAttributeMeta} from '../backend-model/backend-attribute-meta';
import {AttributeMeta} from '../../model/application/meta/attribute-meta';

export class AttributeMetaMapper {
  public static mapBackend(backendAttributeMetas: Array<BackendAttributeMeta>): Array<AttributeMeta> {
    const mappedAttributes: Array<AttributeMeta> = [];

    for (const attribute of backendAttributeMetas) {
      mappedAttributes.push(new AttributeMeta(
        attribute.name, attribute.uiName, attribute.dataType, attribute.listType));
    }
    return mappedAttributes;
  }
}
