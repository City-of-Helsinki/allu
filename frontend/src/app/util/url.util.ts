import {ActivatedRoute, UrlSegment} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {Some} from './option';

export class UrlUtil {
  static urlPathContains(route: ActivatedRoute, containsPart: string ): boolean {
    const routeSnapshot = route.snapshot;
    return Some(routeSnapshot.url)
      .map(urlSegments => urlSegments.find(segment => segment.path === containsPart))
      .map(segment => !!segment)
      .orElse(false);
  }
}
