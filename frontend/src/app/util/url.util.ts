import {ActivatedRoute} from '@angular/router';
import {Some} from './option';
import {Observable} from 'rxjs/internal/Observable';
import {filter, map} from 'rxjs/operators';
import {NumberUtil} from '@util/number.util';

export class UrlUtil {
  static urlPathContains(route: ActivatedRoute, containsPart: string ): boolean {
    const routeSnapshot = route.snapshot;
    return Some(routeSnapshot.url)
      .map(urlSegments => urlSegments.find(segment => segment.path === containsPart))
      .map(segment => !!segment)
      .orElse(false);
  }
}

export function numberFromQueryParams(route: ActivatedRoute, name: string): Observable<number> {
  return route.queryParamMap.pipe(
    filter(params => params.has(name)),
    map(params => params.get(name)),
    filter(id => NumberUtil.isNumeric(id)),
    map(id => Number(id))
  );
}
