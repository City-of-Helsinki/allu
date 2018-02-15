export const ALLU_PREFIX = 'allu';
export const WINKKI_WORKS_PREFIX = 'winkki_works';
export const WINKKI_RENTS_AUDIENCES_PREFIX = 'winkki_rents_audiences';

export function isWinkkiId(id: string): boolean {
  return id !== undefined && (
    id.indexOf(WINKKI_WORKS_PREFIX) >= 0 ||
    id.indexOf(WINKKI_RENTS_AUDIENCES_PREFIX) >= 0);
}


