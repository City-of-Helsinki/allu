import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {FileUploader} from 'ng2-file-upload';
import {AttachmentInfo} from '../model/application/attachment/attachment-info';
import {HttpUtil, HttpResponse} from '../util/http.util';
import {ResponseContentType} from '@angular/http';
import {AttachmentInfoMapper} from './mapper/attachment-info-mapper';

@Injectable()
export class AttachmentService {

  private static uploadUrl = '/api/applications/appId/attachments';
  private static attachmentUrl = '/api/applications/attachments/';

  constructor(private authHttp: AuthHttp) {}

  public uploadFiles(applicationId: number, attachments: AttachmentInfo[]): Observable<Array<AttachmentInfo>> {
    let uploadSubject = new Subject<Array<AttachmentInfo>>();

    if (attachments && attachments.length !== 0) {
      let url = AttachmentService.uploadUrl.replace('appId', String(applicationId));
      let uploader = new ExtendedFileUploader({
        url: url,
        authToken: 'Bearer ' + localStorage.getItem('jwt')}, attachments);
      let files = attachments.filter(a => !a.id).map(a => a.file);

      uploader.onSuccessItem = (item, response, status, headers) => {
        let items = JSON.parse(response);
        let infos = items.map(i => AttachmentInfoMapper.mapBackend(i));
        uploadSubject.next(infos);
      };

      uploader.onErrorItem = (item, response, status, headers) => uploadSubject.error(response);
      uploader.onCompleteAll = () => uploadSubject.complete();
      uploader.addToQueue(files);
      uploader.uploadAll();
    } else {
      uploadSubject.complete();
    }

    return uploadSubject.asObservable();
  }

  remove(attachmentId: number): Observable<HttpResponse> {
    let url = AttachmentService.attachmentUrl + attachmentId;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response));
  }

  download(attachmentId: number, name: string): Observable<File> {
    let url = AttachmentService.attachmentUrl + attachmentId + '/data';
    let options = {responseType: ResponseContentType.Blob };
    return this.authHttp.get(url, options)
      .map(response => new File([response.blob()], name));
  }
}

export class ExtendedFileUploader extends FileUploader {

  constructor(options: any, private meta: AttachmentInfo[]) {
    super(options);
  }

  onBuildItemForm(fileItem: any, form: any): any {
    let metaForItem = this.meta.shift();
    let json = JSON.stringify([metaForItem]);
    let blob = new Blob([json], {type: 'application/json'});
    form.append('meta', blob);
    super.onBuildItemForm(fileItem, form);
  }
}
