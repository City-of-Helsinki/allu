export type DateFilter = (date: Date) => boolean;

export const defaultDateFilter: DateFilter = (date: Date) => true;
