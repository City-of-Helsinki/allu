/*
 * Extend Leaflet typings with leaflet-wfst.
 */

import * as L from 'leaflet';

declare module 'leaflet' {
  class WFS extends FeatureGroup {
    loadFeatures(filters: Filter[] | Filter);
  }

  function wfs(options: any, readFormat?: any): WFS;

  function filter(filters: Filter[] | Filter): Filter;

  namespace Filter {
    class EQ extends Filter {
     constructor(property: string, literal: string, matchCase?: boolean);
    }

    class LT extends Filter {
      constructor(property: string, literal: string, matchCase?: boolean);
    }

    class GT extends Filter {
      constructor(property: string, literal: string, matchCase?: boolean);
    }

    class LEQ extends Filter {
      constructor(property: string, literal: string, matchCase?: boolean);
    }

    class GEQ extends Filter {
      constrcutor(property: string, literal: string, matchCase?: boolean);
    }

    class Like extends Filter {
      constructor(propertyName: string, likeExpression: string, attributes: object);
    }

    class And extends Filter {
      constructor(first: Filter, second?: Filter);
    }

    class Or extends Filter {
      constructor(first: Filter, second?: Filter);
    }

    class BBox extends Filter {
      constructor(propertyName: string, latLngBounds: L.LatLngBounds, CRS: L.CRS);
    }

    function eq(property: string, literal: string, matchCase?: boolean): EQ;
    function lt(property: string, literal: string, matchCase?: boolean): LT;
    function gt(property: string, literal: string, matchCase?: boolean): GT;
    function leq(property: string, literal: string, matchCase?: boolean): LEQ;
    function geq(property: string, literal: string, matchCase?: boolean): GEQ;
    function like(propertyName: string, likeExpression: string, attributes: object): Like;
    function and(first: Filter, ...rest: Filter[]): And;
    function or(first: Filter, ...rest: Filter[]): Or;
    function bbox(propertyName: string, latLngBounds: L.LatLngBounds, CRS: L.CRS): BBox;
  }

  class Filter {
    constructor();
    propertyName(value);
    literal(value);
    element(value);
  }
}
