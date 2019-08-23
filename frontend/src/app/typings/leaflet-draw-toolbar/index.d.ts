import * as L from 'leaflet';

declare module 'leaflet' {
  namespace Control {
    interface Draw {
      getToolbar(type: string): L.EditToolbar;
    }
  }

  interface EditToolbar {
    save(): void;
    enabled(): boolean;
  }
}
