export enum EventNature {
  PUBLIC_FREE,
  PUBLIC_NONFREE,
  CLOSED,
  PROMOTION,
  BIG_EVENT
}

export const selectableNatures = [EventNature.PUBLIC_FREE, EventNature.PUBLIC_NONFREE, EventNature.CLOSED];
