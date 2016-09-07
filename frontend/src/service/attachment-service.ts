import {Injectable} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';
import {AttachmentInfo} from '../model/application/attachment-info';

@Injectable()
export class AttachmentService {

  private static uploadUrl = '/api/applications/appId/attachments';

  constructor() {}

  // TODO: the callback function should be removed when AttachmentService is changed to use the "hub approach"!
  public uploadFiles(applicationId: number, attachments: AttachmentInfo[], callback: () => any) {
    if (attachments && attachments.length !== 0) {
      let url = AttachmentService.uploadUrl.replace('appId', String(applicationId));
      let uploader = new ExtendedFileUploader({
        url: url,
        authToken: 'Bearer ' + localStorage.getItem('jwt')});
      let files = attachments.filter(a => !a.id).map(a => this.mapDescription(a.file, a.description));
      uploader.onCompleteAll = callback;
      uploader.addToQueue(files);
      uploader.uploadAll();
    }
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
    console.log('onBuildItemForm', fileItem);
    let json = JSON.stringify([{ name: fileItem._file.name, description: fileItem._file.description }]);
    let blob = new Blob([json], {type: 'application/json'});
    form.append('meta', blob);
    super.onBuildItemForm(fileItem, form);
  }
}
