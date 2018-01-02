import {Some} from './option';

const icon = {
  'image/png': 'image',
  'image/gif': 'image',
  'image/jpeg': 'image',
  'application/vnd.oasis.opendocument.spreadsheet': 'assessment',
  'application/vnd.ms-excel': 'assessment',
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'assessment'
};

const defaultIcon = 'description';

export class FileUtil {
  static iconForMimeType(type: string) {
    return Some(icon[type]).orElse(defaultIcon);
  }
}
