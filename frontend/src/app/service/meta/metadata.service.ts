import {Injectable} from '@angular/core';
import {StructureMetaMapper} from '../mapper/structure-meta-mapper';
import {Observable} from 'rxjs';
import {BackendStructureMeta} from '../backend-model/backend-structure-meta';
import {StructureMeta} from '../../model/application/meta/structure-meta';
import {ErrorHandler} from '../error/error-handler.service';
import {HttpClient} from '@angular/common/http';
import {catchError, map} from 'rxjs/internal/operators';

const APPLICATION_URL = '/api/applications';
const METADATA_URL = '/api/meta';

export const META_PROJECT = 'Project';

@Injectable()
export class MetadataService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  public loadByType(type: string): Observable<StructureMeta> {
    const url = `${METADATA_URL}/${type}`;
    return this.load(url);
  }

  public loadByApplicationType(applicationType: string): Observable<StructureMeta> {
    const url = `${APPLICATION_URL}/${applicationType}/meta`;
    return this.load(url);
  }

  private load(url: string): Observable<StructureMeta> {
    return this.http.get<BackendStructureMeta>(url).pipe(
      map(meta => StructureMetaMapper.mapBackend(meta)),
      catchError(error => this.errorHandler.handle(error, 'Loading metadata failed'))
    );
  }

}
