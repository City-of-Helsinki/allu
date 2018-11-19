import { Pipe, PipeTransform } from '@angular/core';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {EMPTY, Observable} from 'rxjs';
import {CodeSetCodeMap} from '@model/codeset/codeset';
import {Some} from '@util/option';
import {map} from 'rxjs/operators';

@Pipe({name: 'countryName'})
export class CountryNamePipe implements PipeTransform {
  constructor(private store: Store<fromRoot.State>) {
  }

  transform(value: string): Observable<string> {
    if (value) {
      return this.store.pipe(
        select(fromRoot.getCodeSetCodeMap('Country')),
        map(countries => this.getCountry(value, countries))
      );
    } else {
      return EMPTY;
    }
  }

  getCountry(code: string, countries?: CodeSetCodeMap): string {
    if (countries) {
      return Some(code)
        .map(c => countries[c])
        .map(cs => cs.description)
        .orElse(undefined);
    } else {
      return undefined;
    }
  }
}
