import {BackendStructureMeta} from '../backend-model/backend-structure-meta';
import {StructureMeta} from '../../model/application/meta/structure-meta';
import {AttributeMetaMapper} from './attribute-meta-mapper';

export class StructureMetaMapper {
  public static mapBackend(backendStructureMeta: BackendStructureMeta): StructureMeta {
    return new StructureMeta(
      backendStructureMeta.typeName,
      backendStructureMeta.version,
      AttributeMetaMapper.mapBackend(backendStructureMeta.attributes));
  }

  public static mapFrontend(frontendStructureMeta: StructureMeta): BackendStructureMeta {
    return {
      typeName: frontendStructureMeta.typeName,
      version: frontendStructureMeta.version,
      attributes: [] // attributes are not mapped at the moment, because backend does not need them. To be done later, if needed
    };
  }
}
