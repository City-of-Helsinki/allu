/*
 * Extend Leaflet typings authenticated wms-layer.
 */

declare namespace L {
  export namespace TileLayer {
    export class WMSAuth extends TileLayer.WMS {
      wmsParams: WMSParams;
      options: WMSAuthOptions;

      constructor(baseUrl: string, options: WMSOptions);

      setParams(params: WMSParams, noRedraw?: boolean): this;
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
    timeout?: TimeoutOptions;
  }

  export interface TimeoutOptions {
    response?: number; // Wait x milliseconds for the server to start sending
    deadline?: number; // but allow y milliseconds for the tile to finish loading.
  }

  export namespace tileLayer {
    export function wmsAuth(baseUrl: string, options?: WMSAuthOptions): TileLayer.WMSAuth;
  }
}
