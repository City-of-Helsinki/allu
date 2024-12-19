import { createAction, props } from '@ngrx/store';
import { PruneDataItem } from '../models/prude-data-item.model';

export const loadPruneData = createAction('[Prune Data] Load Data');

export const loadPruneDataSuccess = createAction(
  '[Prune Data] Load Data Success',
  props<{ data: PruneDataItem[] }>()
);

export const loadPruneDataFailure = createAction(
  '[Prune Data] Load Data Failure',
  props<{ error: any }>()
);

export const setCurrentTab = createAction(
  '[Prune Data] Set Current Tab',
  props<{ tab: string }>()
);

export const toggleSelectAll = createAction(
  '[Prune Data] Toggle Select All',
  props<{ tab: string }>()
);

export const toggleSelectItem = createAction(
  '[Prune Data] Toggle Select Item',
  props<{ id: number, tab: string }>()
); 

export const fetchAllData = createAction(
    '[PruneData] Fetch All Data',
    props<{ tab: string }>()
);

export const fetchAllDataSuccess = createAction(
    '[PruneData] Fetch All Data Success',
    props<{ data: PruneDataItem[] }>()
);

export const fetchAllDataFailure = createAction(
    '[PruneData] Fetch All Data Failure',
    props<{ error: string }>()
  );
  
  export const deleteData = createAction(
    '[PruneData] Delete Data',
    props<{ ids: number[] }>()
);

export const deleteDataSuccess = createAction(
    '[PruneData] Delete Data Success',
    props<{ ids: number[] }>()
);
  
  export const deleteDataFailure = createAction(
    '[PruneData] Delete Data Failure',
    props<{ error: string }>()
);