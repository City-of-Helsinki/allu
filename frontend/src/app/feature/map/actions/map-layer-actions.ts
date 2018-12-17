import {MapLayer} from '@service/map/map-layer';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export enum MapLayerActionType {
  AddLayers = '[MapLayer] Add layers',
  SelectLayers = '[MapLayer] Select map layers',
  ResetLayers = '[MapLayer] Reset layers',
  AddTreeStructure = '[MapLayer] Add structure for layer tree'
}

export class AddLayers implements ActionWithTarget {
  readonly type = MapLayerActionType.AddLayers;
  constructor(public targetType: ActionTargetType, public payload: MapLayer[]) {}
}

export class SelectLayers implements ActionWithTarget {
  readonly type = MapLayerActionType.SelectLayers;
  constructor(public targetType: ActionTargetType, public payload: string[]) {}
}

export class ResetLayers implements ActionWithTarget {
  readonly type = MapLayerActionType.ResetLayers;
  constructor(public targetType: ActionTargetType) {}
}

export class AddTreeStructure implements ActionWithTarget {
  readonly type = MapLayerActionType.AddTreeStructure;
  constructor(public targetType: ActionTargetType, public payload: any) {}
}

export type MapLayerActions =
  | AddLayers
  | SelectLayers
  | ResetLayers
  | AddTreeStructure;
