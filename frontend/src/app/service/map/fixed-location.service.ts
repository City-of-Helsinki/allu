import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {FixedLocationArea} from '../../model/common/fixed-location-area';
import {FixedLocationSection} from '../../model/common/fixed-location-section';
import {LocationService} from '../location.service';
import {NotificationService} from '../../feature/notification/notification.service';
import {distinctUntilChanged, filter, map} from 'rxjs/internal/operators';

@Injectable()
export class FixedLocationService {
  private fixedLocations$ = new BehaviorSubject<FixedLocationArea[]>([]);

  constructor(private service: LocationService, private notification: NotificationService) {
    this.service.getFixedLocations().subscribe(
      fxs => this.fixedLocations$.next(fxs),
      err => this.notification.errorInfo(err)
    );

  }

  get existing(): Observable<FixedLocationArea[]> {
    return this.fixedLocations$.pipe(distinctUntilChanged());
  }

  public areasByIds(ids: Array<number>): Observable<FixedLocationArea[]> {
    return this.fixedLocations$.pipe(
      map(areas => areas.filter(a => ids.indexOf(a.id) >= 0))
    );
  }

  public areaById(id: number): Observable<FixedLocationArea> {
    return this.fixedLocations$.pipe(
      map(areas => areas.find(a => a.id === id)),
      filter(area => !!area)
    );
  }

  public areaBySectionIds(ids: Array<number>): Observable<FixedLocationArea> {
    return this.fixedLocations$.pipe(
      map(areas => areas.filter(a => a.hasSectionIds(ids))),
      filter(areas => areas.length > 0),
      map(areas => areas[0])
    );
  }

  public sections(): Observable<FixedLocationSection[]> {
    return this.fixedLocations$.pipe(
      map(areas => areas
        .map(area => area.sections)
        .reduce((acc, cur) => acc.concat(cur), []))
    );
  }

  public sectionsByIds(ids: number[]) {
    return this.sections().pipe(
      map(fxs => fxs.filter(fx => ids.indexOf(fx.id) >= 0))
    );
  }
}
