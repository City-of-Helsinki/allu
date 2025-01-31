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
  (items, selectedIds) => items.length > 0 && items.length === selectedIds.length
);

export const selectSomeSelected = createSelector(
  selectSelectedIds,
  selectedIds => selectedIds.length > 0
); 