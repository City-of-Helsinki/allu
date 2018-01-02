export interface BackendAttachmentInfo {
  id: number;
  type: string;
  mimeType: string;
  name: string;
  description: string;
  size: number;
  creationTime: string;
  decisionAttachment: boolean;
  handlerName: string;
}
