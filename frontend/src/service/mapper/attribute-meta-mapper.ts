import {BackendAttributeMeta} from '../backend-model/backend-attribute-meta';
import {StructureMetaMapper} from './structure-meta-mapper';
import {AttributeMeta} from '../../model/application/meta/attribute-meta';
import {StructureMeta} from '../../model/application/meta/structure-meta';

export class AttributeMetaMapper {
  public static mapBackend(backendAttributeMetas: Array<BackendAttributeMeta>): Array<AttributeMeta> {
    let mappedAttributes: Array<AttributeMeta> = [];

    for (let attribute of backendAttributeMetas) {
      let structureMeta: StructureMeta = undefined;
      if (attribute.structureMeta) {
        structureMeta = StructureMetaMapper.mapBackend(attribute.structureMeta);
      }
      mappedAttributes.push(new AttributeMeta(
        attribute.name, attribute.uiName, attribute.dataType, attribute.listType, structureMeta, attribute.validationRule));
    }
    return mappedAttributes;
  }
}
