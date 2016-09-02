import {BackendAttachmentInfo} from '../backend-model/backend-attachment-info';
import {AttachmentInfo} from '../../model/application/attachment-info';
export class AttachmentInfoMapper {

  public static mapBackend(backendAttachmentInfo: BackendAttachmentInfo): AttachmentInfo {
    if (!backendAttachmentInfo) {
      return undefined;
    }
    return new AttachmentInfo(
      backendAttachmentInfo.id,
      backendAttachmentInfo.name,
      backendAttachmentInfo.description,
      backendAttachmentInfo.size,
      new Date(backendAttachmentInfo.creationTime),
      undefined);
  }

  public static mapFrontend(attachmentInfo: AttachmentInfo): BackendAttachmentInfo {
    return (attachmentInfo) ?
    {
      id: attachmentInfo.id,
      name: attachmentInfo.name,
      description: attachmentInfo.description,
      size: attachmentInfo.size,
      creationTime: (attachmentInfo.creationTime) ? attachmentInfo.creationTime.toISOString() : undefined
    } : undefined;
  }
}
