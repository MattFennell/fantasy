import { State } from '../Reducers/root';
import { createSelector } from 'reselect';

const getBalanceState = (state: State) => state.account;
export const getBalance = createSelector([getBalanceState], a => a.balance);

const getFirstNameState = (state: State) => state.account;
export const getFirstName = createSelector([getFirstNameState], s => s.firstName);

const getSurnameState = (state: State) => state.account;
export const getSurname = createSelector([getSurnameState], s => s.surname);

const getPageBeingViewedState = (state: State) => state.account;
export const getPageBeingViewed = createSelector([getPageBeingViewedState], p => p.pageBeingViewed);

const getTotalPointsState = (state: State) => state.account;
export const getTotalPoints = createSelector([getTotalPointsState], p => p.totalPoints);
