
import {BackendStructure} from '../backend-model/backend-structure';
import {Structure} from '../../model/application/structure';
export class StructureMapper {

  public static mapBackend(backendStructure: BackendStructure): Structure {
    return (backendStructure) ?
      new Structure(backendStructure.area,
        backendStructure.description,
        new Date(backendStructure.startDate),
        new Date(backendStructure.endDate)) : undefined;
  }

  public static mapFrontend(structure: Structure): BackendStructure {
    return (structure) ? {
      area: structure.area,
      description: structure.description,
      startDate: (structure.startDate) ? structure.startDate.toISOString() : undefined,
      endDate: (structure.endDate) ? structure.endDate.toISOString() : undefined
    } : undefined;
  }
}
