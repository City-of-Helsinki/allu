/*
 * Extend Leaflet typings with grouped layers plugin support.
 */

declare namespace L {
  namespace control {
    export function groupedLayers(
      baseLayers: L.Control.LayersObject,
      groupedOverlays: {[key: string]: L.Control.LayersObject},
      options?: GroupedLayersOptions);

    export interface GroupedLayersOptions extends L.Control.LayersOptions {
      exclusiveGroups?: Array<string>;
      groupCheckboxes?: boolean;
    }
  }
}
