import {ActivatedRoute, UrlSegment} from '@angular/router';
import {Observable} from 'rxjs/Observable';

export class UrlUtil {
  static urlPathBy(route: ActivatedRoute, predicate: (segment: UrlSegment) => boolean): Observable<string> {
    return route.url
      .map(urtSegments => urtSegments.find(predicate))
      .filter(segment => segment !== undefined)
      .map(segment => segment.path)
      .first();
  }

  static urlPathContains(route: ActivatedRoute, containsPart: string ): Observable<boolean> {
    return UrlUtil.filterUrlPath(route, segment => segment.path === containsPart);
  }

  static filterUrlPath(route: ActivatedRoute, predicate: (segment: UrlSegment) => boolean): Observable<boolean> {
    return route.url
      .map(urlSegments => urlSegments.some(predicate))
      .first();
  }
}
