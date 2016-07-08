import {BackendStructureMeta} from './backend-structure-meta';

export interface BackendAttributeMeta {
  name: string;
  uiName: string;
  dataType: string;
  listType: string;
  structureMeta: BackendStructureMeta;
  validationRule: string;
}
