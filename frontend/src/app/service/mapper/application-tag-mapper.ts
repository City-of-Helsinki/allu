import {ApplicationTag} from '../../model/application/tag/application-tag';
import {TimeUtil} from '../../util/time.util';
import {Some} from '../../util/option';

export class ApplicationTagMapper {

  public static mapBackendList(tags: Array<BackendApplicationTag>): Array<ApplicationTag> {
    return (tags)
      ? tags.map(tag => ApplicationTagMapper.mapBackend(tag))
      : [];
  }

  public static mapBackend(tag: BackendApplicationTag): ApplicationTag {
    return new ApplicationTag(
      tag.type,
      tag.addedBy,
      TimeUtil.dateFromBackend(tag.creationTime)
    );
  }

  public static mapSearchResultList(tags: Array<string>): Array<ApplicationTag> {
    return (tags)
      ? tags.map(tag => new ApplicationTag(tag))
      : [];
  }

  public static mapFrontendList(tags: Array<ApplicationTag>): Array<BackendApplicationTag> {
    return (tags)
      ? tags.map(tag => ApplicationTagMapper.mapFrontend(tag))
      : [];
  }

  public static mapFrontend(tag: ApplicationTag): BackendApplicationTag {
    return {
      type: tag.type,
      addedBy: tag.addedBy,
      creationTime: Some(tag.creationTime).map(creationTime => creationTime.toISOString()).orElse(undefined)
    };
  }
}

export interface BackendApplicationTag {
  type: string;
  addedBy: number;
  creationTime: string;
}
