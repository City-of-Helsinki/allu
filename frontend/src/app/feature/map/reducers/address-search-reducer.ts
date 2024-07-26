import {Geocoordinates} from '@model/common/geocoordinates';
import {AddressSearchActions, AddressSearchActionType} from '@feature/map/actions/address-search-actions';
import {PostalAddress} from '@model/common/postal-address';
import {ArrayUtil} from '@util/array-util';

export interface State {
  matching: PostalAddress[];
  coordinates: Geocoordinates;
}

export const initialState: State = {
  matching: [],
  coordinates: undefined
};

export function reducer(state: State = initialState, action: AddressSearchActions) {
  switch (action.type) {
    case AddressSearchActionType.SearchSuccess:
      // no point refactoring complex working sort 
      // eslint-disable-next-line no-case-declarations
      const sorted = [...action.payload].sort(ArrayUtil.naturalSort((address: PostalAddress) => address.uiStreetAddress));
      return {
        ...state,
        matching: sorted
      };

    case AddressSearchActionType.FetchCoordinates:
    case AddressSearchActionType.ClearCoordinates:
      return {
        ...state,
        coordinates: undefined
      };

    case AddressSearchActionType.FetchCoordinatesSuccess:
      return {
        ...state,
        coordinates: action.payload
      };

    default: {
      return {...state};
    }
  }
}

export const getMatching = (state: State) => state.matching;

export const getCoordinates = (state: State) => state.coordinates;
