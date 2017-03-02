import {StructureMeta} from './structure-meta';

export class AttributeMeta {
  constructor(
    public name: string,
    public uiName: string,
    public dataType: string,
    public listType: string) {}
}
