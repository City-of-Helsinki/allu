import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {MapLayer} from '@service/map/map-layer';
import {MapLayerActions, MapLayerActionType} from '@feature/map/actions/map-layer-actions';
import {createReducer} from '@util/create-reducer';
import {applicationLayers} from '@feature/map/map-layer.service';
import {TreeStructureNode} from '@feature/common/tree/tree-node';

export interface State extends EntityState<MapLayer> {
  selected: string[];
  treeStructure: TreeStructureNode<void>;
}

export const adapter: EntityAdapter<MapLayer> = createEntityAdapter<MapLayer>({
  selectId: (layer: MapLayer) => layer.id
});

export const initialState: State = adapter.getInitialState({
  selected: ['Karttasarja'].concat(applicationLayers),
  treeStructure: {}
});

export function reducer(state: State = initialState, action: MapLayerActions) {
  switch (action.type) {
    case MapLayerActionType.AddLayers: {
      return adapter.setAll(action.payload, {
        ...state
      });
    }

    case MapLayerActionType.SelectLayers: {
      return {
        ...state,
        selected: action.payload
      };
    }

    case MapLayerActionType.ResetLayers: {
      return {
        ...state,
        selected: initialState.selected
      };
    }

    case MapLayerActionType.AddTreeStructure: {
      return {
        ...state,
        treeStructure: action.payload
      };
    }

    default: {
      return {...state};
    }
  }
}

export const createReducerFor = createReducer<State, MapLayerActions>(initialState, reducer);

export const getSelected = (state: State) => state.selected;

export const getTreeStructure = (state: State) => state.treeStructure;
