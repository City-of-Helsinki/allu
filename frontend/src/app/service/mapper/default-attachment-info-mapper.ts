import {DefaultAttachmentInfo} from '../../model/application/attachment/default-attachment-info';
import {BackendDefaultAttachmentInfo} from '../backend-model/backend-default-attachment-info';

export class DefaultAttachmentInfoMapper {
  public static mapBackend(backendAttachmentInfo: BackendDefaultAttachmentInfo): DefaultAttachmentInfo {
    if (!backendAttachmentInfo) {
      return undefined;
    }
    return new DefaultAttachmentInfo(
      backendAttachmentInfo.id,
      backendAttachmentInfo.type,
      backendAttachmentInfo.name,
      backendAttachmentInfo.description,
      backendAttachmentInfo.size,
      new Date(backendAttachmentInfo.creationTime),
      backendAttachmentInfo.decisionAttachment,
      backendAttachmentInfo.handlerName,
      new Blob(['empty'], {}), // Default attachment info needs to contain some file so dummy file is used
      backendAttachmentInfo.defaultAttachmentId,
      backendAttachmentInfo.applicationTypes,
      backendAttachmentInfo.fixedLocationId);
  }

  public static mapFrontend(attachmentInfo: DefaultAttachmentInfo): BackendDefaultAttachmentInfo {
    return (attachmentInfo) ?
      {
        id: attachmentInfo.id,
        type: attachmentInfo.type,
        name: attachmentInfo.name,
        description: attachmentInfo.description,
        size: attachmentInfo.size,
        creationTime: (attachmentInfo.creationTime) ? attachmentInfo.creationTime.toISOString() : undefined,
        decisionAttachment: attachmentInfo.decisionAttachment,
        handlerName: attachmentInfo.handlerName,
        defaultAttachmentId: attachmentInfo.defaultAttachmentId,
        applicationTypes: attachmentInfo.applicationTypes,
        fixedLocationId: attachmentInfo.fixedLocationId
      } : undefined;
  }
}
