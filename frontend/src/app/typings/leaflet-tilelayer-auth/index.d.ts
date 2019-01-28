/*
 * Extend Leaflet typings authenticated wms-layer.
 */

import * as L from 'leaflet';
import {TileLayerOptions} from 'leaflet';

declare module 'leaflet' {
  export namespace TileLayer {
    export class Auth extends TileLayer {
      options: TileLayerOptions;

      constructor(baseUrl: string, options: TileLayerOptions);
    }
  }

  export interface AuthOptions extends TileLayerOptions {
    minZoom?: number;
    maxZoom?: number;
    maxNativeZoom?: number;
    minNativeZoom?: number;
    subdomains?: string | string[];
    errorTileUrl?: string;
    zoomOffset?: number;
    tms?: boolean;
    zoomReverse?: boolean;
    detectRetina?: boolean;
    crossOrigin?: boolean;
    token: string;
    timeout?: TimeoutOptions;
  }

  export interface TimeoutOptions {
    response?: number; // Wait x milliseconds for the server to start sending
    deadline?: number; // but allow y milliseconds for the tile to finish loading.
  }

  export namespace tileLayer {
    export function auth(baseUrl: string, options?: AuthOptions): TileLayer.Auth;
  }
}
