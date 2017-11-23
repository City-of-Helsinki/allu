import {BackendAttributeMeta} from './backend-attribute-meta';

export interface BackendStructureMeta {
  typeName: string;
  version: number;
  attributes: Array<BackendAttributeMeta>;
}
