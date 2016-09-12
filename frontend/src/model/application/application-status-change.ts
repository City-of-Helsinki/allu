import {translations} from '../../util/translations';

export enum ApplicationStatus {
  PRE_RESERVED, // Alustava varaus
  CANCELLED, // Peruttu
  PENDING, // Vireillä
  HANDLING, // Käsittelyssä
  DECISIONMAKING, // Odottaa päätöstä
  DECISION, // Päätetty
  REJECTED, // Hylätty päätös
  RETURNED_TO_PREPARATION, // Palautettu valmisteluun
  FINISHED // Valmis
}

export function translateStatus(status: ApplicationStatus) {
  return translations.application.status[ApplicationStatus[status]];
}

export class ApplicationStatusChange {
  constructor(public id: number, public status: ApplicationStatus, public comment: string) {}

  public static of(id: number, status: ApplicationStatus): ApplicationStatusChange {
    return new ApplicationStatusChange(id, status, undefined);
  }

  public static withComment(id: number, status: ApplicationStatus, comment: string): ApplicationStatusChange {
    return new ApplicationStatusChange(id, status, comment);
  }
}
