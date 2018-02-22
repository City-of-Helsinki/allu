export interface CustomerSearchQuery {
  name?: string;
  registryKey?: string;
  type?: string;
  active?: boolean;
  invoicingOnly?: boolean;
}
