export class CableInfoEntry {
  constructor()
  constructor(type: string, additionalInfo: string)
  constructor(public type?: string, public additionalInfo?: string) {}
}
