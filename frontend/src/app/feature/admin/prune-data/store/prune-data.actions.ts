import { createAction, props } from '@ngrx/store';
import { PruneDataItem } from '../models/prude-data-item.model';

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
  props<{ tab: string, page?: number, size?: number, sortField?: string, sortDirection?: string }>()
  );

  export const fetchAllDataSuccess = createAction(
  '[PruneData] Fetch All Data Success',
  props<{ data: PruneDataItem[], totalItems?: number }>()
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
    props<{ ids: number[]; error: any }>()
);

export const tableSortChange = createAction(
  '[PruneData] Table Sort Change',
  props<{ data: PruneDataItem[] }>()
);

export const tableSortReset = createAction(
  '[PruneData] Table Sort Reset'
);

export const deleteModalVisibility = createAction(
  '[PruneData] Table Sort Reset',
  props<{ show: boolean }>()
);

export const updatePagination = createAction(
  '[PruneData] Update Pagination',
  props<{ pageIndex: number; pageSize: number; data: PruneDataItem[] }>()
);
