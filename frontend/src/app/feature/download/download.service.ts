import {Injectable} from '@angular/core';
import {EMPTY, Observable} from 'rxjs';
import {Option, Some} from '../../util/option';
import {ErrorHandler} from '../../service/error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {BlobFile} from './blob-file';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {catchError, map} from 'rxjs/internal/operators';

const URL_PREFIX = '/api/';
const HEADER_CONTENT_DISPOSITION = 'content-disposition';
const FILENAME_PART = 'filename=';

@Injectable()
export class DownloadService {
  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  download(url: string, defaultFilename?: string): Observable<BlobFile> {
    if (url) {
      return this.http.get(URL_PREFIX  + url, {observe: 'response', responseType: 'blob'}).pipe(
        map(response => this.toFile(response, defaultFilename)),
        catchError(error => this.errorHandler.handle(error, findTranslation('common.error.downloadFailed')))
      );
    } else {
      return EMPTY;
    }
  }

  private toFile(response: HttpResponse<Blob>, defaultFilename?: string): BlobFile {
    const filename = this.getFilename(response.headers).orElse(defaultFilename);
    return new BlobFile(response.body, filename);
  }

  private getFilename(headers: HttpHeaders): Option<string> {
    return Some(headers.get(HEADER_CONTENT_DISPOSITION))
      .map(cd => cd.split(';'))
      .map(parts => parts.map(part => part.trim()))
      .map(parts => parts.find(p => p.startsWith(FILENAME_PART)))
      .map(filenamePart => filenamePart.replace(FILENAME_PART, ''));
  }
}
