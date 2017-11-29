import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {FileUploader} from 'ng2-file-upload';
import {AttachmentInfo} from '../model/application/attachment/attachment-info';
import {HttpUtil} from '../util/http.util';
import {ResponseContentType} from '@angular/http';
import {AttachmentInfoMapper} from './mapper/attachment-info-mapper';
import {DefaultAttachmentInfo} from '../model/application/attachment/default-attachment-info';
import {DefaultAttachmentInfoMapper} from './mapper/default-attachment-info-mapper';
import {ApplicationType} from '../model/application/type/application-type';
import {HttpResponse} from '../util/http-response';

const uploadUrl = '/api/applications/appId/attachments';
const downloadUrl = '/api/applications/attachments/:attachmentId/data';
const attachmentUrl = '/api/applications/:appId/attachments';
const defaultAttachmentGetUrl = '/api/applications/default-attachments';
const defaultAttachmentUrlEdit = '/api/admin/attachments';

export class ExtendedFileUploader extends FileUploader {
  constructor(options: any, private meta: AttachmentInfo[]) {
    super(options);
  }

  onBuildItemForm(fileItem: any, form: any): any {
    const metaForItem = this.meta.shift();
    const json = JSON.stringify([metaForItem]);
    const blob = new Blob([json], {type: 'application/json'});
    form.append('meta', blob);
    super.onBuildItemForm(fileItem, form);
  }
}

@Injectable()
export class AttachmentService {

  constructor(private authHttp: AuthHttp) {}

  public uploadFiles(applicationId: number, attachments: AttachmentInfo[]): Observable<Array<AttachmentInfo>> {
    const url = uploadUrl.replace('appId', String(applicationId));
    return this.upload(url, attachments);
  }

  remove(applicationId: number, attachmentId: number): Observable<HttpResponse> {
    const url = uploadUrl.replace('appId', String(applicationId)) + '/' + attachmentId;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response));
  }

  download(attachmentId: number): Observable<Blob> {
    const url = downloadUrl.replace(':attachmentId', String(attachmentId));
    const options = {responseType: ResponseContentType.Blob };
    return this.authHttp.get(url, options)
      .map(response => response.blob());
  }

  getDefaultAttachmentInfo(id: number): Observable<DefaultAttachmentInfo> {
    const url = defaultAttachmentGetUrl + '/' + id;
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(info => DefaultAttachmentInfoMapper.mapBackend(info));
  }

  getDefaultAttachmentInfos(): Observable<Array<DefaultAttachmentInfo>> {
    return this.authHttp.get(defaultAttachmentGetUrl)
      .map(response => response.json())
      .map(infos => infos.map(info => DefaultAttachmentInfoMapper.mapBackend(info)));
  }

  getDefaultAttachmentInfosByType(appType: ApplicationType): Observable<Array<DefaultAttachmentInfo>> {
    const url = defaultAttachmentGetUrl + '/applicationType/' + ApplicationType[appType];
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(infos => infos.map(info => DefaultAttachmentInfoMapper.mapBackend(info)));
  }

  saveDefaultAttachments(attachment: DefaultAttachmentInfo): Observable<DefaultAttachmentInfo> {
    if (attachment.id) {
      return this.updateDefaultAttachmentInfo(attachment);
    } else {
      return this.upload(defaultAttachmentUrlEdit, [attachment]).map(results => results[0]);
    }
  }

  updateDefaultAttachmentInfo(attachment: DefaultAttachmentInfo): Observable<DefaultAttachmentInfo> {
    const url = defaultAttachmentUrlEdit + '/' + attachment.id;
    return this.authHttp.put(url, attachment)
      .map(response => response.json())
      .map(info => DefaultAttachmentInfoMapper.mapBackend(info));
  }

  removeDefaultAttachment(id: number): Observable<HttpResponse> {
    const url = defaultAttachmentUrlEdit + '/' + id;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response));
  }

  private upload(url: string, attachments: Array<AttachmentInfo>): Observable<Array<AttachmentInfo>> {
    const uploadSubject = new Subject<Array<AttachmentInfo>>();
    if (attachments && attachments.length !== 0) {
      const uploader = new ExtendedFileUploader({
        url: url,
        authToken: 'Bearer ' + localStorage.getItem('jwt')}, attachments);
      const files = attachments.map(a => a.file);

      uploader.onSuccessItem = (item, response, status, headers) => {
        const items = JSON.parse(response);
        const infos = items.map(i => AttachmentInfoMapper.mapBackend(i));
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
}
