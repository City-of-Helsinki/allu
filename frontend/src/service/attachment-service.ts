import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {FileUploader} from 'ng2-file-upload';
import {AttachmentInfo} from '../model/application/attachment/attachment-info';
import {HttpUtil, HttpResponse} from '../util/http.util';
import {ResponseContentType} from '@angular/http';

@Injectable()
export class AttachmentService {

  private static uploadUrl = '/api/applications/appId/attachments';
  private static attachmentUrl = '/api/applications/attachments/';

  constructor(private authHttp: AuthHttp) {}

  public uploadFiles(applicationId: number, attachments: AttachmentInfo[]): Observable<number> {
    let uploadSubject = new Subject<number>();

    if (attachments && attachments.length !== 0) {
      let url = AttachmentService.uploadUrl.replace('appId', String(applicationId));
      let uploader = new ExtendedFileUploader({
        url: url,
        authToken: 'Bearer ' + localStorage.getItem('jwt')});
      let files = attachments.filter(a => !a.id).map(a => this.mapDescription(a.file, a.description));
      uploader.onProgressAll = (progress) => uploadSubject.next(progress);
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

  private mapDescription(file: any, description: string): any {
    file.description = description;
    return file;
  }
}

export class ExtendedFileUploader extends FileUploader {

  constructor(options: any) {
    super(options);
  }

  onBuildItemForm(fileItem: any, form: any): any {
    let json = JSON.stringify([{ name: fileItem._file.name, description: fileItem._file.description }]);
    let blob = new Blob([json], {type: 'application/json'});
    form.append('meta', blob);
    super.onBuildItemForm(fileItem, form);
  }
}
