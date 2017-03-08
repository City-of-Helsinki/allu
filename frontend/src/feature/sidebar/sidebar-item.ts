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
