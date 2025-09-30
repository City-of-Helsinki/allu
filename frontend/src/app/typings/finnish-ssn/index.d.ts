/*
declare class FinnishSSN {
  /!**
   * Validates parameter given SSN. Returns true if SSN is valid, otherwise false.
   * @param ssn - {String} For example '010190-123A'
   * @returns {boolean}
   *!/
  static validate(ssn): boolean;
}

export = FinnishSSN;
*/

declare module 'finnish-ssn' {
  export function validate(ssn: string): boolean;
}
