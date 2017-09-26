/*
 * Extend Leaflet typings authenticated wms-layer.
 */

declare namespace L {
  export namespace TileLayer {
    export class WMSAuth extends TileLayer.WMS {
      constructor(baseUrl: string, options: WMSOptions);

      setParams(params: WMSParams, noRedraw?: boolean): this;

      wmsParams: WMSParams;
      options: WMSAuthOptions;
    }
  }

  export interface WMSAuthOptions extends WMSOptions {
    layers: string;
    token: string;
    styles?: string;
    format?: string;
    transparent?: boolean;
    version?: string;
    crs?: CRS;
    uppercase?: boolean;
  }

  export namespace tileLayer {
    export function wmsAuth(baseUrl: string, options?: WMSAuthOptions): TileLayer.WMSAuth;
  }
}
