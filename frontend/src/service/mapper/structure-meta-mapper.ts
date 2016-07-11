import {BackendStructureMeta} from '../backend-model/backend-structure-meta';
import {StructureMeta} from '../../model/application/structure-meta';
import {AttributeMetaMapper} from './attribute-meta-mapper';

export class StructureMetaMapper {
  public static mapBackend(backendStructureMeta: BackendStructureMeta): StructureMeta {
    return new StructureMeta(
      backendStructureMeta.applicationType,
      backendStructureMeta.version,
      AttributeMetaMapper.mapBackend(backendStructureMeta.attributes));
  }
}
