import {CableInfoType} from './cable-info-type';

export class CableInfoEntry {
  constructor()
  constructor(type: CableInfoType, additionalInfo: string)
  constructor(public type?: CableInfoType, additionalInfo?: string) {}
}
