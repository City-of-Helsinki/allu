export class CodeSet {
  constructor(
    public id?: number,
    public type?: string,
    public code?: string,
    public description?: string,
    public value?: string) {}
}

export interface CodeSetTypeMap {
  [type: string]: CodeSetCodeMap;
}

export interface CodeSetCodeMap {
  [code: string]: CodeSet;
}
