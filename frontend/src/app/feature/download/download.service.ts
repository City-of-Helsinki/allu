import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {Headers, Response, ResponseContentType} from '@angular/http';
import {Option, Some} from '../../util/option';
import {ErrorHandler} from '../../service/error/error-handler.service';
import {findTranslation} from '../../util/translations';

const URL_PREFIX = '/api/';
const HEADER_CONTENT_DISPOSITION = 'content-disposition';
const FILENAME_PART = 'filename=';

@Injectable()
export class DownloadService {
  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  download(url: string, defaultFilename?: string): Observable<File> {
    if (url) {
      return this.authHttp.get(URL_PREFIX  + url, {responseType: ResponseContentType.ArrayBuffer })
        .map(response => this.toFile(response, defaultFilename))
        .catch(error => this.errorHandler.handle(error, findTranslation('common.error.downloadFailed')));
    } else {
      return Observable.empty();
    }
  }

  private toFile(response: Response, defaultFilename?: string): File {
    const filename = this.getFilename(response.headers).orElse(defaultFilename);
    return new File([response.arrayBuffer()], filename);
  }

  private getFilename(headers: Headers): Option<string> {
    return Some(headers.get(HEADER_CONTENT_DISPOSITION))
      .map(cd => cd.split(';'))
      .map(parts => parts.map(part => part.trim()))
      .map(parts => parts.find(p => p.startsWith(FILENAME_PART)))
      .map(filenamePart => filenamePart.replace(FILENAME_PART, ''));
  }
}
