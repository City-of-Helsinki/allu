import { createReducer, on } from '@ngrx/store';
import * as PruneDataActions from './prune-data.actions';
import { PruneDataItem } from '../models/prude-data-item.model';
import { TimeUtil } from '@app/util/time.util';
import moment from 'moment';


export interface PruneDataState {
  allData: PruneDataItem[];
  filteredData: PruneDataItem[];
  currentTab: string | null;
  loading: boolean;
  error: any;
  selectedIds: number[];
  showDeleteModal: boolean;
}
    
export const initialState: PruneDataState = {
  allData: [],
  filteredData: [],
  currentTab: 'EXCAVATION_ANNOUNCEMENTS',
  loading: false,
  error: null,
  selectedIds: [],
  showDeleteModal: false,
};

export const pruneDataReducer = createReducer(
  initialState,
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
    const allIds = state.filteredData.map(item => item.id);
    const newSelectedIds = state.selectedIds.length === allIds.length ? [] : allIds;
    return {
      ...state,
      selectedIds: newSelectedIds
    };
  }),
  on(PruneDataActions.fetchAllData, state => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(PruneDataActions.fetchAllDataSuccess, (state, { data }) => {
    const humanReadableData = makeDateTimesHumanReadable(data);
    return {
    ...state,
    loading: false,
    allData: humanReadableData,
    filteredData: filterDataByTab(humanReadableData, state.currentTab)}
  }),
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
  })),
  on(PruneDataActions.tableSortReset, (state) => ({
    ...state,
    filteredData: filterDataByTab(state.allData, state.currentTab)
  })),
  on(PruneDataActions.tableSortChange, (state, { data }) => ({
    ...state,
    filteredData: data
  })),
  on(PruneDataActions.deleteModalVisibility, (state, { show }) => ({
    ...state,
    showDeleteModal: show
  })),
);

function filterDataByTab(data: PruneDataItem[], tab: string | null): PruneDataItem[] {
  if (!tab) return data;
  return data.filter(item => item.applicationType === tab.toUpperCase());
}

function makeDateTimesHumanReadable(data: PruneDataItem[]) {
  return data.map(d => ({
    ...d, 
    startTime: moment(d.startTime).format('DD.MM.YYYY'), 
    endTime: moment(d.endTime).format('DD.MM.YYYY'),
    changeTime: moment(d.changeTime).format('DD.MM.YYYY - HH:mm')
  }));
}