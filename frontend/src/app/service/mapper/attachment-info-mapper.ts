import {BackendAttachmentInfo} from '../backend-model/backend-attachment-info';
import {AttachmentInfo} from '../../model/application/attachment/attachment-info';
export class AttachmentInfoMapper {

  public static mapBackend(backendAttachmentInfo: BackendAttachmentInfo): AttachmentInfo {
    if (!backendAttachmentInfo) {
      return undefined;
    }
    return new AttachmentInfo(
      backendAttachmentInfo.id,
      backendAttachmentInfo.type,
      backendAttachmentInfo.name,
      backendAttachmentInfo.description,
      backendAttachmentInfo.size,
      new Date(backendAttachmentInfo.creationTime),
      backendAttachmentInfo.handlerName,
      undefined);
  }

  public static mapFrontend(attachmentInfo: AttachmentInfo): BackendAttachmentInfo {
    return (attachmentInfo) ?
    {
      id: attachmentInfo.id,
      type: attachmentInfo.type,
      name: attachmentInfo.name,
      description: attachmentInfo.description,
      size: attachmentInfo.size,
      creationTime: (attachmentInfo.creationTime) ? attachmentInfo.creationTime.toISOString() : undefined,
      handlerName: attachmentInfo.handlerName
    } : undefined;
  }
}
