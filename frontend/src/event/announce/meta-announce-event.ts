import {AnnounceEvent} from './announce-event';
import {StructureMeta} from '../../model/application/structure-meta';

export class MetaAnnounceEvent extends AnnounceEvent {
  constructor(public structureMeta: StructureMeta) {
    super();
  }
}
