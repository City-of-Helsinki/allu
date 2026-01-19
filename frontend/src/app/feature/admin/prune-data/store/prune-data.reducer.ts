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
  deleteInProgress: boolean;
  totalItems: number;
  pageIndex: number;
  pageSize: number;
  sortField: string | null;
  sortDirection: string | null;
}
    
export const initialState: PruneDataState = {
  allData: [],
  filteredData: [],
  currentTab: 'EXCAVATION_ANNOUNCEMENTS',
  loading: false,
  error: null,
  selectedIds: [],
  showDeleteModal: false,
  deleteInProgress: false,
  totalItems: 0,
  pageIndex: 0,
  pageSize: 10,
  sortField: null,
  sortDirection: null
};

export const pruneDataReducer = createReducer(
  initialState,
  on(PruneDataActions.setCurrentTab, (state, { tab }) => ({
    ...state,
    currentTab: tab,
    filteredData: filterDataByTab(state.allData, tab),
    pageIndex: 0
  })),
  on(PruneDataActions.toggleSelectItem, (state, { id }) => ({
    ...state,
    selectedIds: state.selectedIds.includes(id) 
      ? state.selectedIds.filter(itemId => itemId !== id)
      : [...state.selectedIds, id]
  })),
  on(PruneDataActions.toggleSelectAll, (state) => {
    const pageIds = state.filteredData.map(item => getItemId(item));
    const allPageItemsSelected = pageIds.every(id => state.selectedIds.includes(id));
    
    let newSelectedIds: number[];
    if (allPageItemsSelected) {
      // Deselect only current page items
      newSelectedIds = state.selectedIds.filter(id => !pageIds.includes(id));
    } else {
      // Add current page items to selection (keep existing selections from other pages)
      const existingIds = new Set(state.selectedIds);
      pageIds.forEach(id => existingIds.add(id));
      newSelectedIds = Array.from(existingIds);
    }
    
    return {
      ...state,
      selectedIds: newSelectedIds
    };
  }),
  on(PruneDataActions.fetchAllData, (state, {page, size, sortField, sortDirection}) => ({
    ...state,
    loading: true,
    error: null,
    pageIndex: page !== undefined ? page : state.pageIndex,
    pageSize: size !== undefined ? size : state.pageSize,
    sortField: sortField !== undefined ? sortField : state.sortField,
    sortDirection: sortDirection !== undefined ? sortDirection : state.sortDirection
  })),
  on(PruneDataActions.fetchAllDataSuccess, (state, { data, totalItems }) => {
    const humanReadableData = makeDateTimesHumanReadable(data);
    return {
    ...state,
    loading: false,
    allData: humanReadableData,
    filteredData: humanReadableData,
    totalItems: totalItems !== undefined ? totalItems : state.totalItems
  }
  }),
  on(PruneDataActions.fetchAllDataFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),
  on(PruneDataActions.deleteData, (state) => ({
    ...state,
    showDeleteModal: false,
    deleteInProgress: true
  })),
  on(PruneDataActions.deleteDataSuccess, (state, { ids }) => {
    const newData = removeDeleted(state.allData, ids);
    const currentTabData = filterDataByTab(newData, state.currentTab);
    const newSelectedIds = state.selectedIds.filter(id => !ids.includes(id));
    return {
      ...state,
      deleteInProgress: false,
      allData: newData,
      filteredData: currentTabData,
      selectedIds: newSelectedIds
    };
  }),
  on(PruneDataActions.deleteDataFailure, (state, { error }) => ({
    ...state,
    error,
    deleteInProgress: false,
  })),
  on(PruneDataActions.tableSortReset, (state) => ({
    ...state,
    sortField: null,
    sortDirection: null,
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

function removeDeleted(data: PruneDataItem[], ids: number[]): PruneDataItem[] {
  return data.filter(item => !ids.includes(getItemId(item)));
}

function makeDateTimesHumanReadable(data: PruneDataItem[]) {
  return data.map(d => ({
    ...d, 
    startTime: moment(d.startTime).format('DD.MM.YYYY'), 
    endTime: moment(d.endTime).format('DD.MM.YYYY'),
    changeTime: moment(d.changeTime).format('DD.MM.YYYY - HH:mm')
  }));
}

function getItemId(item: any): number {
  return item.id ?? item.customerId;
}