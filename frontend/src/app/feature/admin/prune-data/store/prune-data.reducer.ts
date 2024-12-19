import { createReducer, on } from '@ngrx/store';
import * as PruneDataActions from './prune-data.actions';
import { PruneDataItem } from '../models/prude-data-item.model';


export interface PruneDataState {
  allData: PruneDataItem[];
  filteredData: PruneDataItem[];
  currentTab: string | null;
  loading: boolean;
  error: any;
  selectedIds: number[];
}
    
export const initialState: PruneDataState = {
  allData: [],
  filteredData: [],
  currentTab: null,
  loading: false,
  error: null,
  selectedIds: []
};

export const pruneDataReducer = createReducer(
  initialState,
  on(PruneDataActions.loadPruneData, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(PruneDataActions.loadPruneDataSuccess, (state, { data }) => ({
    ...state,
    allData: data,
    filteredData: filterDataByTab(data, state.currentTab),
    loading: false
  })),
  on(PruneDataActions.setCurrentTab, (state, { tab }) => ({
    ...state,
    currentTab: tab,
    filteredData: filterDataByTab(state.allData, tab)
  })),
  on(PruneDataActions.toggleSelectItem, (state, { id }) => ({
    ...state,
    selectedIds: state.selectedIds.includes(id) 
      ? state.selectedIds.filter(itemId => itemId !== id)
      : [...state.selectedIds, id]
  })),
  on(PruneDataActions.toggleSelectAll, (state) => {
    const allIds = state.allData.map(item => item.id);
    const newSelectedIds = state.selectedIds.length === allIds.length ? [] : allIds;
    return {
      ...state,
      selectedIds: newSelectedIds
    };
  }),
  on(PruneDataActions.setCurrentTab, (state, { tab }) => ({
    ...state,
    currentTab: tab,
  })),
  on(PruneDataActions.fetchAllData, state => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(PruneDataActions.fetchAllDataSuccess, (state, { data }) => ({
    ...state,
    loading: false,
    data,
  })),
  on(PruneDataActions.fetchAllDataFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),
  on(PruneDataActions.deleteDataSuccess, (state, { ids }) => ({
    ...state,
  })),
  on(PruneDataActions.deleteDataFailure, (state, { error }) => ({
    ...state,
    error,
})));

function filterDataByTab(data: PruneDataItem[], tab: string | null): PruneDataItem[] {
  if (!tab) return data;
  return data.filter(item => item);
} 