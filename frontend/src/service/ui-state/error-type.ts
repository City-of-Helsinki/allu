import {translations} from '../../util/translations';
export enum ErrorType {
  GEOLOCATION_SEARCH_FAILED,
  APPLICATION_SEARCH_FAILED,
  APPLICATION_WORKQUEUE_SEARCH_FAILED,
  APPLICATION_SAVE_FAILED,
  APPLICATION_STATUS_CHANGE_FAILED,
  APPLICATION_HANDLER_CHANGE_FAILED,
  PDF_GENERATION_FAILED,
  PROJECT_SAVE_FAILED,
}

/**
 * Contains all mappings from ErrorType -> message
 */
const errorTypeToMessage: Map<ErrorType, string> = createMappings();

/**
 * Given ErrorType is converted to readable form
 */
export function message(errorType: ErrorType): string {
  return errorTypeToMessage.get(errorType);
}

function createMappings(): Map<ErrorType, string> {
  let map = new Map<ErrorType, string>();
  map.set(ErrorType.GEOLOCATION_SEARCH_FAILED, translations.geolocation.error.searchFailed);
  map.set(ErrorType.APPLICATION_SEARCH_FAILED, translations.application.error.searchFailed);
  map.set(ErrorType.APPLICATION_WORKQUEUE_SEARCH_FAILED, translations.application.error.searchFailed);
  map.set(ErrorType.APPLICATION_SAVE_FAILED, translations.application.error.saveFailed);
  map.set(ErrorType.APPLICATION_STATUS_CHANGE_FAILED, translations.application.error.statusChangeFailed);
  map.set(ErrorType.APPLICATION_HANDLER_CHANGE_FAILED, translations.application.error.handlerChangeFailed);
  map.set(ErrorType.PDF_GENERATION_FAILED, translations.decision.error.generatePdf);
  map.set(ErrorType.PROJECT_SAVE_FAILED, translations.project.error.saveFailed);

  return map;
}
