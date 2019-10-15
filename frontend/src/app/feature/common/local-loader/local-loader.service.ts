import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

export const APP_CONFIG_URL = '/assets/app-config.json';

export interface AppConfig {
  contactInfo: string;
}

@Injectable()
export class LocalLoaderService {
  constructor(private http: HttpClient) {
  }

  load<T>(url: string): Observable<T> {
    return this.http.get<T>(url);
  }
}
