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
      backendAttachmentInfo.mimeType,
      backendAttachmentInfo.name,
      backendAttachmentInfo.description,
      backendAttachmentInfo.size,
      new Date(backendAttachmentInfo.creationTime),
      backendAttachmentInfo.decisionAttachment,
      backendAttachmentInfo.handlerName,
      undefined);
  }

  public static mapFrontend(attachmentInfo: AttachmentInfo): BackendAttachmentInfo {
    return (attachmentInfo) ?
    {
      id: attachmentInfo.id,
      type: attachmentInfo.type,
      mimeType: attachmentInfo.mimeType,
      name: attachmentInfo.name,
      description: attachmentInfo.description,
      size: attachmentInfo.size,
      creationTime: (attachmentInfo.creationTime) ? attachmentInfo.creationTime.toISOString() : undefined,
      decisionAttachment: attachmentInfo.decisionAttachment,
      handlerName: attachmentInfo.handlerName
    } : undefined;
  }
}
