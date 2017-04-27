import {ApplicationType} from '../../model/application/type/application-type';
export interface SidebarItem {
  type: SidebarItemType;
  count?: number;
}

export type SidebarItemType =
  'BASIC_INFO' |
  'ATTACHMENTS' |
  'INVOICING' |
  'COMMENTS' |
  'EMAIL' |
  'HISTORY' |
  'SUPERVISION' |
  'APPLICATIONS' |
  'PROJECTS' |
  'DECISION';

interface ApplicationTypeVisibleItems {
  [applicationType: string]: Array<SidebarItemType>;
}

const visibleForAll: Array<SidebarItemType> = ['BASIC_INFO', 'ATTACHMENTS', 'COMMENTS', 'EMAIL', 'HISTORY', 'APPLICATIONS', 'PROJECTS'];

export const visibleItemsByApplicationType: ApplicationTypeVisibleItems = {
  'EXCAVATION_ANNOUNCEMENT': visibleForAll.concat('INVOICING', 'SUPERVISION', 'DECISION'),
  'AREA_RENTAL': visibleForAll.concat('INVOICING', 'SUPERVISION', 'DECISION'),
  'TEMPORARY_TRAFFIC_ARRANGEMENTS': visibleForAll.concat('INVOICING', 'SUPERVISION', 'DECISION'),
  'CABLE_REPORT': visibleForAll.concat('SUPERVISION', 'DECISION'),
  'PLACEMENT_CONTRACT': visibleForAll.concat('INVOICING', 'SUPERVISION', 'DECISION'),
  'EVENT': visibleForAll.concat('INVOICING', 'SUPERVISION', 'DECISION'),
  'SHORT_TERM_RENTAL': visibleForAll.concat('INVOICING', 'SUPERVISION', 'DECISION'),
  'NOTE': visibleForAll.concat('SUPERVISION')
};

export function visibleFor(appType: string, sidebarItemType: SidebarItemType) {
  return visibleItemsByApplicationType[appType].indexOf(sidebarItemType) >= 0;
}

interface ItemToPath {
  [type: string]: Array<string>;
}

export const itemPaths: ItemToPath = {
  'BASIC_INFO': ['info'],
  'ATTACHMENTS': ['attachments'],
  'INVOICING': ['invoicing'],
  'COMMENTS': ['comments'],
  'EMAIL': ['email'],
  'HISTORY': ['history'],
  'SUPERVISION': ['supervision'],
  'APPLICATIONS': ['applications'],
  'PROJECTS': ['projects'],
  'DECISION': ['decision-preview']
};
