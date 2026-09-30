import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Clear, LoadSuccess} from '@feature/history/actions/history-actions';
import {reducer, State} from '@feature/history/reducers/history-reducer';
import {ChangeHistoryItem} from '@model/history/change-history-item';

describe('History reducer', () => {
  it('should clear history state on clear action', () => {
    const existingHistory = [new ChangeHistoryItem()];
    const loaded = reducer(undefined, new LoadSuccess(ActionTargetType.Customer, existingHistory));

    const cleared = reducer(loaded as State, new Clear(ActionTargetType.Customer));

    expect(cleared.history).toEqual([]);
    expect(cleared.loading).toBeFalse();
    expect(cleared.fieldsVisible).toBeFalse();
    expect(cleared.statusHistory).toEqual([]);
  });
});
