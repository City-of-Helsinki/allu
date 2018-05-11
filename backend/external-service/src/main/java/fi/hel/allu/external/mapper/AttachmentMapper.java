package fi.hel.allu.external.mapper;

import fi.hel.allu.external.domain.AttachmentInfoExt;
import fi.hel.allu.servicecore.domain.AttachmentInfoJson;

public class AttachmentMapper {

  public static AttachmentInfoJson toAttachmentInfoJson(AttachmentInfoExt attachmentInfoExt) {
    AttachmentInfoJson attachmentInfo = new AttachmentInfoJson();
    attachmentInfo.setDescription(attachmentInfoExt.getDescription());
    attachmentInfo.setName(attachmentInfoExt.getName());
    attachmentInfo.setMimeType(attachmentInfoExt.getMimeType());
    return attachmentInfo;
  }
}
