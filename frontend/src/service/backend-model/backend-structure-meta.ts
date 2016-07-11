import {BackendAttributeMeta} from './backend-attribute-meta';

export interface BackendStructureMeta {
  applicationType: string;
  version: number;
  attributes: Array<BackendAttributeMeta>;
}
