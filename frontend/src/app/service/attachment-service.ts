import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {FileUploader} from 'ng2-file-upload';
import {AttachmentInfo} from '../model/application/attachment/attachment-info';
import {AttachmentInfoMapper} from './mapper/attachment-info-mapper';
import {DefaultAttachmentInfo} from '../model/application/attachment/default-attachment-info';
import {DefaultAttachmentInfoMapper} from './mapper/default-attachment-info-mapper';
import {ApplicationType} from '../model/application/type/application-type';
import {BackendAttachmentInfo} from './backend-model/backend-attachment-info';
import {BackendDefaultAttachmentInfo} from './backend-model/backend-default-attachment-info';
import {map} from 'rxjs/internal/operators';

const uploadUrl = '/api/applications/appId/attachments';
const downloadUrl = '/api/applications/attachments/:attachmentId/data';
const updateUrl = '/api/applications/attachments/:attachmentId';
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

  constructor(private http: HttpClient) {}

  public uploadFiles(applicationId: number, attachments: AttachmentInfo[]): Observable<Array<AttachmentInfo>> {
    const url = uploadUrl.replace('appId', String(applicationId));
    return this.upload(url, attachments);
  }

  remove(applicationId: number, attachmentId: number): Observable<{}> {
    const url = uploadUrl.replace('appId', String(applicationId)) + '/' + attachmentId;
    return this.http.delete(url);
  }

  download(attachmentId: number): Observable<Blob> {
    const url = downloadUrl.replace(':attachmentId', String(attachmentId));
    return this.http.get(url, {responseType: 'blob'});
  }

  updateAttachmentInfo(attachment: AttachmentInfo): Observable<AttachmentInfo> {
    const url = updateUrl.replace(':attachmentId', String(attachment.id));
    return this.http.put<BackendAttachmentInfo>(url, attachment).pipe(
      map(info => AttachmentInfoMapper.mapBackend(info))
    );
  }

  getDefaultAttachmentInfo(id: number): Observable<DefaultAttachmentInfo> {
    const url = defaultAttachmentGetUrl + '/' + id;
    return this.http.get<BackendDefaultAttachmentInfo>(url).pipe(
      map(info => DefaultAttachmentInfoMapper.mapBackend(info))
    );
  }

  getDefaultAttachmentInfos(): Observable<Array<DefaultAttachmentInfo>> {
    return this.http.get<BackendDefaultAttachmentInfo[]>(defaultAttachmentGetUrl).pipe(
      map(infos => infos.map(info => DefaultAttachmentInfoMapper.mapBackend(info)))
    );
  }

  getDefaultAttachmentInfosByType(appType: ApplicationType): Observable<Array<DefaultAttachmentInfo>> {
    const url = defaultAttachmentGetUrl + '/applicationType/' + ApplicationType[appType];
    return this.http.get<BackendDefaultAttachmentInfo[]>(url).pipe(
      map(infos => infos.map(info => DefaultAttachmentInfoMapper.mapBackend(info)))
    );
  }

  saveDefaultAttachment(attachment: DefaultAttachmentInfo): Observable<DefaultAttachmentInfo> {
    if (attachment.id) {
      return this.updateDefaultAttachmentInfo(attachment);
    } else {
      return this.upload(defaultAttachmentUrlEdit, [attachment]).pipe(
        map(results => results[0])
      );
    }
  }

  updateDefaultAttachmentInfo(attachment: DefaultAttachmentInfo): Observable<DefaultAttachmentInfo> {
    const url = defaultAttachmentUrlEdit + '/' + attachment.id;
    return this.http.put<BackendDefaultAttachmentInfo>(url, attachment).pipe(
      map(info => DefaultAttachmentInfoMapper.mapBackend(info))
    );
  }

  removeDefaultAttachment(id: number): Observable<{}> {
    const url = defaultAttachmentUrlEdit + '/' + id;
    return this.http.delete(url);
  }

  private upload(url: string, attachments: Array<AttachmentInfo>): Observable<Array<AttachmentInfo>> {
    const uploadSubject = new Subject<Array<AttachmentInfo>>();
    if (attachments && attachments.length !== 0) {
      const uploader = new ExtendedFileUploader({
        url: url,
        authToken: 'Bearer ' + sessionStorage.getItem('jwt')}, attachments);
      const files = attachments.map(a => a.file);

      uploader.onSuccessItem = (item, response, status, headers) => {
        const items = JSON.parse(response);
        const infos = items.map(i => AttachmentInfoMapper.mapBackend(i));
        uploadSubject.next(infos);
      };

      uploader.onErrorItem = (item, response, status, headers) => uploadSubject.error(response);
      uploader.onCompleteAll = () => uploadSubject.complete();
      uploader.addToQueue(<File[]>files);
      uploader.uploadAll();
    } else {
      uploadSubject.complete();
    }

    return uploadSubject.asObservable();
  }
}
