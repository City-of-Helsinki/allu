import * as L from 'leaflet';
import {pathStyle} from './map-draw-styles';

export function drawOptions(enabled: boolean): any {
  return enabled ? {
    // todo: this <false>false can be removed when typescript compiler allows type parameter of | false
    polyline: <false>false,
    marker: <false>false,
    circlemarker: <false>false,
    polygon: {
      shapeOptions: pathStyle.DEFAULT_DRAW,
      allowIntersection: false,
      showArea: true
    },
    circle: false,
    fixedCircle: {
      shapeOptions: pathStyle.DEFAULT_DRAW
    },
    rectangle: {
      shapeOptions: pathStyle.DEFAULT_DRAW
    },
    bufferPolyline: {
      shapeOptions: pathStyle.DEFAULT_DRAW,
      polyOptions: {
        shapeOptions: pathStyle.DEFAULT_DRAW
      }
    }
  } : undefined;
}

export function editOptions(featureGroup: L.FeatureGroup, enabled: boolean): L.Control.EditOptions {
  return {
    featureGroup,
    edit: enabled ? {selectedPathOptions: pathStyle.DEFAULT_EDIT} : false,
    remove: enabled
  };
}
