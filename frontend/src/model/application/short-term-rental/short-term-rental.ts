import {ApplicationTypeData} from '../type/application-type-data';

export class ShortTermRental extends ApplicationTypeData {
  constructor()
  constructor(description: string, commercial: boolean)
  constructor(public description?: string, public commercial?: boolean) {
    super();
  }
}
