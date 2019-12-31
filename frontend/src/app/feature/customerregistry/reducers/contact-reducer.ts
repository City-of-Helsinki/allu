import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {Contact} from '@model/customer/contact';
import {ContactActions, ContactActionType} from '@feature/customerregistry/actions/contact-actions';

export interface State extends EntityState<Contact> {
  loading: boolean;
}

export const adapter: EntityAdapter<Contact> = createEntityAdapter<Contact>({
  selectId: contact => contact.id
});

export const initialState: State = adapter.getInitialState({
  loading: false
});

export function reducer(state: State = initialState, action: ContactActions) {
  switch (action.type) {
    case ContactActionType.FindById: {
      return {
        ...state,
        loading: true
      };
    }
    case ContactActionType.FindByIdComplete: {
      if (action.payload.error) {
        return {
          ...state,
          loading: false
        };
      } else {
        return adapter.upsertOne(action.payload.contact, state);
      }
    }
    default: {
      return state;
    }
  }
}

export const getLoading = (state: State) => state.loading;

export const {
  selectIds: selectContactIds,
  selectEntities: selectContactEntities,
  selectAll: selectAllContacts,
  selectTotal: selectContactTotal
} = adapter.getSelectors();
