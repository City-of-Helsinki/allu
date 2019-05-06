export const ALLU_PREFIX = 'allu';
export const WINKKI_PREFIX = 'Winkki';

export function isWinkkiId(id: string): boolean {
  return id !== undefined && id.indexOf(WINKKI_PREFIX) >= 0;
}


