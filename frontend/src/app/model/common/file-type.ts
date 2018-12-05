export enum FileType {
  PNG = 'image/png',
  GIF = 'image/gif',
  JPEG = 'image/jpeg',
  PDF = 'application/pdf'
}

export function validForDecision(type: string): boolean {
  return type === FileType.PDF;
}
