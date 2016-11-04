import {ApplicationType} from '../application/type/application-type';
export class FixedLocation {
  constructor()
  constructor(id: number, area: string, section: string, applicationType: ApplicationType)
  constructor(public id?: number, public area?: string, public section?: string, public applicationType?: ApplicationType) { }
}
