import {Page} from '../../model/common/page';
import {BackendPage} from '../backend-model/backend-page';

export class PageMapper {
  static mapBackend<FROM, TO>(backendPage: BackendPage<FROM>, mappingFn: (from: FROM) => TO): Page<TO> {
    if (backendPage) {
      return new Page<TO>(
        backendPage.content.map(item => mappingFn(item)),
        backendPage.first,
        backendPage.last,
        backendPage.number,
        backendPage.numberOfElements,
        backendPage.size,
        backendPage.sort,
        backendPage.totalElements,
        backendPage.totalPages
      );
    } else {
      return new Page<TO>();
    }
  }
}
