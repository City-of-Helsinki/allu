import {Injectable} from '@angular/core';
import {CityDistrict} from '../../model/common/city-district';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {LocationService} from '../location.service';
import {ArrayUtil} from '../../util/array-util';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class CityDistrictService {
  private cityDistricts$ = new BehaviorSubject<CityDistrict[]>([]);

  constructor(private service: LocationService) {
    this.service.districts().subscribe(ds => {
      const districts = ds
        .filter(d => d.districtId !== 0) // Ignore 0 Aluemeri
        .sort(ArrayUtil.naturalSort((district: CityDistrict) => district.name));
      this.cityDistricts$.next(districts);
    });
  }

  public get(): Observable<CityDistrict[]> {
    return this.cityDistricts$.asObservable();
  }

  public byId(id: number): Observable<CityDistrict> {
    return this.cityDistricts$
      .map(ds => ds.find(d => d.id === id))
      .filter(d => !!d);
  }

  public name(id: number): Observable<string> {
    return id !== undefined
      ? this.byId(id).map(d => d.name)
      : Observable.empty();
  }

  byIds(ids: number[]): Observable<CityDistrict[]> {
    return this.cityDistricts$
      .map(ds => ds.filter(d => ids.indexOf(d.id) >= 0));
  }
}
