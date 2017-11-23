export interface AttachmentInfoForm {
  id?: number;
  type?: string;
  name?: string;
  description?: string;
  size?: number;
  creationTime?: string;
  handlerName?: string;
  file?: File;
}
