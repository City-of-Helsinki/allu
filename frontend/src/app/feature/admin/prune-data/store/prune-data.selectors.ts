import { createFeatureSelector, createSelector } from '@ngrx/store';
import { PruneDataState } from './prune-data.reducer';

export const selectPruneDataState = createFeatureSelector<PruneDataState>('pruneData');

export const selectCurrentTab = createSelector(
    selectPruneDataState,
    state => state.currentTab
  );
  
export const selectPruneData = createSelector(
  selectPruneDataState,
  state => state.allData
);

export const selectFilteredData = createSelector(
  selectPruneDataState,
  state => state.filteredData
);

export const selectPruneDataLoading = createSelector(
  selectPruneDataState,
  state => state.loading
);

export const selectPruneDataError = createSelector(
  selectPruneDataState,
  state => state.error
);

export const selectSelectedIds = createSelector(
  selectPruneDataState,
  state => state.selectedIds
);

export const selectDeleteModalVisibility = createSelector(
  selectPruneDataState,
  state => state.showDeleteModal
);

export const selectDeleteInProgress = createSelector(
  selectPruneDataState,
  state => state.deleteInProgress
)

export const selectAllSelected = createSelector(
  selectFilteredData,
  selectSelectedIds,
  (items, selectedIds) => {
    if (items.length === 0) return false;
    const pageIds = items.map(item => (item as any).id ?? (item as any).customerId);
    return pageIds.every(id => selectedIds.includes(id));
  }
);

export const selectSomeSelected = createSelector(
  selectFilteredData,
  selectSelectedIds,
  (items, selectedIds) => {
    const pageIds = items.map(item => (item as any).id ?? (item as any).customerId);
    return pageIds.some(id => selectedIds.includes(id));
  }
);

export const selectTotalItems = createSelector(
  selectPruneDataState,
  state => state.totalItems
);
  
  
export const selectPageIndex = createSelector(
  selectPruneDataState,
  state => state.pageIndex
);
  
  
export const selectPageSize = createSelector(
  selectPruneDataState,
  state => state.pageSize
);
  
  
export const selectSortField = createSelector(
  selectPruneDataState,
  state => state.sortField
);
  
  
export const selectSortDirection = createSelector(
  selectPruneDataState,
  state => state.sortDirection
); 