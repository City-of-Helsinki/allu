/*
 * Extend Leaflet typings with leaflet measure path.
 */

declare namespace L {
  export interface Polyline {
    showMeasurements(options?: MeasurementOptions): Polyline;
    hideMeasurements(): Polyline;
  }

  export interface MeasurementOptions {
    showOnHover: boolean;
    minPixelDistance: number;
    showDistances: boolean;
    showArea: boolean;
    lang: MeasurementLang;
  }

  export interface MeasurementLang {
    totalLength: string;
    totalArea: string;
    segmentLength: string;
  }
}
