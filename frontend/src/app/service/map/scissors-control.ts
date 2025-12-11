import * as L from 'leaflet';
import * as turf from '@turf/turf';
import { Subject } from 'rxjs';
import { ShapeAdded } from './map-controller';
import { translations } from '@util/translations';
import { NotificationService }
from '@feature/notification/notification.service';
import { MapEventHandler } from './map-event-handler';

/**
 * This file contains the implementation of the ScissorsControl, a Leaflet
 * control that allows users to cut polygon features on the map.
 *
 * The control provides a user interface for selecting a polygon, drawing
 * a cutting line, and then splitting the selected polygon into multiple new
 * polygons based on the cutting line. The control manages its own state, user
 * interactions, and the geometric calculations required for the cutting
 * operation.
 *
 * The main components of the ScissorsControl are:
 * - A state machine to manage the tool's behavior (Inactive, SelectArea,
 *   DrawCut).
 * - UI elements for activating the tool, canceling the operation, and
 *   completing the cut.
 * - Event handlers for user interactions such as clicks and key presses.
 * - Integration with the Leaflet Draw plugin for drawing the cutting line.
 * - Use of the Turf.js library for performing the geometric calculations
 *   (e.g., line intersection, polygon difference).
 */

/**
 * Defines the possible states of the scissors tool. The state machine ensures
 * that the tool behaves correctly based on user interactions.
 */
export enum State {
  /**
   * The default state of the tool when it is not active. In this state, the
   * tool is not listening for any user interactions and the UI is hidden. The
   * tool returns to this state after a cut is completed or canceled.
   */
  Inactive,

  /**
   * The state when the user has activated the scissors tool but has not yet
   * selected a polygon to cut. In this state, the tool is waiting for the user
   * to click on a feature on the map.
   */
  SelectArea,

  /**
   * The state when the user has selected a polygon to cut. In this state, the
   * user can draw a cutting line across the selected polygon. The tool
   * provides UI elements for completing the cut or canceling the operation.
   */
  DrawCut
}

/**
 * Callback functions for enabling/disabling `map-controller` event handlers.
 */
interface EventHandlerCallbacks {
  // Temporarily disable Leaflet event handlers for the time of cutting:
  // - Intersection event handler
  //   - Disabling this allows the user to draw a complex cutting line
  //     (e.g., a zig-zag) without triggering intersection errors.
  // - Tooltip popup handler
  //   - Disable this so that tooltip popups are not shown during cutting.
  disable: () => void;
  // Re-enables the disabled event handlers.
  enable: () => void;
}

/**
 * A custom Leaflet control for cutting polygons on the map. The control
 * provides a user interface for selecting a polygon, drawing a cutting line,
 * and then splitting the selected polygon into multiple new polygons.
 */
export class ScissorsControl extends L.Control {
  public options: L.ControlOptions;
  private map: L.Map;
  private state = State.Inactive;
  private selectedFeature: any;
  private cuttingGroup: L.FeatureGroup;
  private cuttingLineDrawHandler: L.Draw.Polyline;
  private container: HTMLElement;
  private cancelButton: HTMLLIElement;
  private cutButton: HTMLLIElement;
  private toolActionButtons: HTMLUListElement;

  /**
   * Creates an instance of the ScissorsControl.
   *
   * @param options The options for the Leaflet control.
   * @param editedItems The feature group containing the items that can be
   *        edited.
   * @param notification The notification service for displaying messages to
   *        the user.
   * @param shapes$ A subject that emits an event when a shape is added or
   *        modified.
   * @param eventHandlerCallbacks Callback functions for enabling/disabling
   *        `map-controller` event handlers.
   */
  constructor(options: L.ControlOptions = { position: 'topright' },
              private editedItems: L.FeatureGroup,
              private notification: NotificationService,
              private shapes$: Subject<ShapeAdded>,
              private eventHandlerCallbacks: EventHandlerCallbacks) {
    super(options);
    this.options = options;
  }

  /**
   * Called when the control is added to the map. This method initializes the
   * control's UI and event handlers.
   *
   * @param map The Leaflet map instance.
   * @returns The main HTML container for the control.
   */
  onAdd(map: L.Map): HTMLElement {
    this.map = map;

    // Initialize a feature group to hold the cutting line and a draw handler
    // for the line.
    this.cuttingGroup = L.featureGroup().addTo(this.map);
    this.cuttingLineDrawHandler = new L.Draw.Polyline(this.map as any, {
      shapeOptions: {
        color: '#ff0000',
        weight: 3,
        opacity: 0.7
      },
      showLength: false,
      repeatMode: false
    });

    // Create the main tool button with the scissors icon.
    const toolButton = this.createButton(
      translations.map.edit.toolbar.buttons.scissors,
      `<img src="assets/svg/scissors-icon.svg" height="100%">`,
        this.activate.bind(this)
    );
    toolButton.className = 'scissors-button';

    // Prevent map click and scroll events from propagating to the map when the
    // button is clicked.
    L.DomEvent.disableClickPropagation(toolButton);
    L.DomEvent.disableScrollPropagation(toolButton);

    // Create a list to hold the action buttons (e.g., cut, cancel).
    this.toolActionButtons = L.DomUtil.create(
      'ul', 'leaflet-draw-actions scissors-actions'
    );

    // Create the cancel and cut buttons. These list items contain anchor
    // elements that are styled as buttons.
    this.cancelButton = this.createDrawActionLi('cancel',
                                                this.cancel.bind(this));
    this.cutButton = this.createDrawActionLi(
      'cut',
      this.cuttingLineDrawHandler
          .completeShape
          .bind(this.cuttingLineDrawHandler)
    );

    // Create the main container for the control and append the UI elements.
    this.container = L.DomUtil.create('div', 'leaflet-bar leaflet-control');
    this.container.append(toolButton);
    this.container.append(this.toolActionButtons);

    return this.container;
  }

  /**
   * Called when the control is removed from the map. This method cleans up any
   * event listeners.
   *
   * @param _ The Leaflet map instance.
   */
  onRemove(_: L.Map): void {
    // Clean up event listeners to prevent memory leaks.
    L.DomEvent.off(this.container);
  }

  /**
   * Returns the current state of the scissors tool.
   *
   * @returns The current state of the tool.
   */
  getState(): State {
    return this.state;
  }

  /**
   * Activates the scissors tool. This method is called when the user clicks
   * the scissors icon in the toolbar. It puts the tool into the `SelectArea`
   * state, where the user must select a polygon to be cut.
   */
  activate(): void {
      if (this.state !== State.Inactive) return;

      // Programmatically trigger an "Escape" key press to cancel any other
      // active Leaflet Draw/Edit tools.
      this.triggerEscKey();

      this.eventHandlerCallbacks.disable();

      // Display the "Cancel" action button.
      this.toolActionButtons.appendChild(this.cancelButton);

      // Set up event handlers for canceling the tool and selecting a feature.
      this.map.on('keyup', this.cancelOnEscKeypressHandler, this);
      this.map.on('click', this.selectFeatureHandler, this);
      this.map.once('draw:drawstart draw:editstart', this.cancel, this);

      this.state = State.SelectArea;
  }

  /**
   * Cancels the scissors tool and returns it to the `Inactive` state. This
   * method cleans up all tool-specific event handlers, UI elements, and
   * drawing states.
   */
  cancel(): void {
    // Disable all tool-specific event handlers.
    this.map.off('keyup', this.cancelOnEscKeypressHandler, this);
    this.map.off('click', this.selectFeatureHandler, this);
    this.map.off('draw:created', this.cutCreatedHandler, this);

    this.eventHandlerCallbacks.enable();

    // Disable the cutting line draw handler and clear any partially drawn
    // lines.
    this.cuttingLineDrawHandler.disable();
    this.cuttingGroup.clearLayers();

    // Remove the "Cancel" and "Cut" action buttons from the UI.
    for (const button of [this.cancelButton, this.cutButton]) {
      if (button.parentNode) button.parentNode.removeChild(button);
    }

    if (this.state !== State.Inactive) this.state = State.Inactive;
  }

  /**
   * Event handler that is triggered when the user has finished drawing the
   * cutting line. This method performs the cutting operation and replaces the
   * original polygon with the new, cut polygons.
   *
   * @param e The Leaflet mouse event containing the cutting line layer.
   */
  private cutCreatedHandler(e: L.LeafletMouseEvent): void {
      const cuttingLine = e.layer;
      this.cuttingGroup.addLayer(cuttingLine);

      const cutLineGeoJSON = (cuttingLine as any).toGeoJSON();
      const featureGeoJSON = this.selectedFeature;

      const cutResult = this.calculateCut(
        featureGeoJSON,
        cutLineGeoJSON
      );

      if (cutResult && cutResult.length > 0) {
        // Find and remove the original feature from the edited items layer.
        this.editedItems.eachLayer(layer => {
          const layerGeoJSON = (layer as any).toGeoJSON();
          if (turf.booleanEqual(layerGeoJSON, this.selectedFeature)) {
            this.editedItems.removeLayer(layer);
          }
        });

        // Add each of the new cut features back to the map.
        cutResult.forEach(cutFeature => {
          const newLayer = L.geoJSON(cutFeature).getLayers()[0];
          this.editedItems.addLayer(newLayer);

          // Show measurements for the new features if they are polygons.
          const geometryType = cutFeature.geometry.type;
          if (geometryType === 'Polygon' || geometryType === 'MultiPolygon') {
            (newLayer as any).showMeasurements(translations.map.measure);
          }
        });

        // Emit a shape change event to notify other parts of the application
        // that the shapes have been modified.
        this.shapes$.next(new ShapeAdded(this.editedItems, false));
      } else {
        this.notification.error(
          translations.map.notifications.cuttingUnsuccessful,
          translations.map.notifications.noChanges
        );
      }

      // Clean up the event handler for the draw:created event.
      this.map.off('draw:created', this.cutCreatedHandler);

      // Use the cancel method to reset the tool to its initial state.
      this.cancel();
  }

  /**
   * Click handler that is active when the tool is in the `SelectArea` state.
   * This method is responsible for identifying the feature that the user has
   * clicked on and transitioning the tool to the `DrawCut` state.
   *
   * @param e The Leaflet mouse event.
   */
  private selectFeatureHandler(e: L.LeafletMouseEvent): void {
      // Find all features at the click point.
      const intersectingFeatures =
        MapEventHandler.clickIntersects(e, this.map, [this.editedItems]);

      if (intersectingFeatures.length === 0) {
        this.notification.info(translations.map.notifications.noSelectedArea,
                               translations.map.notifications.selectArea);
        return;
      }
      if (intersectingFeatures.length > 1) {
        this.notification.error(
          translations.map.notifications.couldNotSelectAreaToCut,
          translations.map.notifications.intersectingAreasInSelectedPoint
        );
        return;
      }

      // Transition the tool to the DrawCut state and notify the user.
      this.map.off('draw:drawstart', this.cancel, this);
      this.state = State.DrawCut;
      this.notification.success(
        translations.map.notifications.areaSelected,
        translations.map.notifications.drawCuttingLine
      );

      this.selectedFeature = intersectingFeatures[0];

      // Temporarily remove the normal click handlers to prevent conflicts.
      this.map.off('click');

      // Add the "Cut" button to the UI when the user starts drawing the
      // cutting line.
      this.map.once(
        'draw:drawstart',
        () => this.toolActionButtons.insertBefore(this.cutButton, this.cancelButton),
        this
      );

      // Enable the cutting line draw handler to allow the user to start
      // drawing.
      this.cuttingLineDrawHandler.enable();

      // Add event handlers for when the line is created or when the drawing is
      // canceled.
      this.map.once('draw:created', this.cutCreatedHandler, this);
      this.map.once('draw:drawstart draw:editstart', this.cancel, this);
    }

  /**
   * Creates a thick line polygon from a line feature. This used to calculate
   * the difference between the cutting line and the polygon to be cut.
   *
   * @param lineFeature The line feature to convert to a thick polygon.
   * @returns A GeoJSON polygon feature.
   */
  private createThickLinePolygonForCut(lineFeature: any): any {
    const offsetLine1 = turf.lineOffset(lineFeature, 0.00001,
                                        {units: 'kilometers'});
    const offsetLine2 = turf.lineOffset(lineFeature, -0.00001,
                                        {units: 'kilometers'});

    const polyCoords = [
      ...offsetLine1.geometry.coordinates,
      ...offsetLine2.geometry.coordinates.slice().reverse(),
      offsetLine1.geometry.coordinates[0] // close polygon
    ];
    const thickLineString = turf.lineString(polyCoords);
    return turf.lineToPolygon(thickLineString);
  }

  /**
   * Performs the geometric calculation to cut a polygon with a line. This
   * method uses the Turf.js library to calculate the difference between the
   * polygon and the cutting line.
   *
   * @param feature The polygon feature to be cut.
   * @param cuttingLine The line feature to use for cutting.
   * @returns An array of new GeoJSON features that result from the cut
   *          operation.
   */
  private calculateCut(
    feature: any,
    cuttingLine: any
  ): any[] {
    try {
      if (!feature || !feature.geometry || !cuttingLine ||
          !cuttingLine.geometry) {
        console.error('Invalid input for cutting operation');
        return [feature];
      }

      // Normalize the coordinates of the input features to ensure consistency.
      const normalizedFeature = this.normalizeCoordinates(feature);
      const normalizedLine = this.normalizeCoordinates(cuttingLine);

      const polygon = normalizedFeature.geometry;
      const line = normalizedLine.geometry;

      if ((polygon.type !== 'Polygon' && polygon.type !== 'MultiPolygon') ||
          (line.type !== 'LineString')) {
        return [feature];
      }

      // Check if the cutting line intersects the polygon at least twice.
      // If not, no cut is possible.
      const intersectPoints = turf.lineIntersect(normalizedFeature,
                                                 normalizedLine);
      if (intersectPoints.features.length < 2) {
        return [feature];
      }

      // Create a thick line polygon to use for the cutting operation.
      const thickLinePolygon =
        this.createThickLinePolygonForCut(normalizedLine);

      // Calculate the difference between the original polygon and the thick
      // line polygon.
      const clipped = turf.difference(
        turf.featureCollection([normalizedFeature, thickLinePolygon])
      );

      if (!clipped || !clipped.geometry || !clipped.geometry.coordinates ||
          clipped.geometry.coordinates.length === 0) {
        return [feature];
      }

      const results: any[] = [];
      const geom = turf.getGeom(clipped);

      const polygons = geom.type === 'Polygon'
        ? [geom]
        : geom.coordinates.map(coords => turf.polygon(coords).geometry);

      // Filter out any small "sliver" polygons that may result from the
      // cutting operation.
      polygons.forEach(pGeom => {
        const pFeature = turf.feature(pGeom);
        const area = turf.area(pFeature);
        if (area > 1) { // Filter out slivers, 1 square meter
          const newFeature = JSON.parse(JSON.stringify(feature));
          newFeature.geometry = pGeom;
          results.push(newFeature);
        }
      });

      return results.length > 0 ? results : [feature];

    } catch (error) {
      this.notification.error(
        translations.map.notifications.cuttingUnsuccessful
      );
      console.log('Cutting operation was unsuccessful: ' + error.message);
      return [feature];
    }
  }

  /**
   * Normalizes the coordinates of a GeoJSON feature to a fixed precision.
   * This helps to avoid floating point precision issues during geometric
   * calculations.
   *
   * @param geojson The GeoJSON feature to normalize.
   * @returns A new GeoJSON feature with normalized coordinates.
   */
  private normalizeCoordinates(geojson: any): any {
    const PRECISION = 9; // Adjust precision as needed

    const normalized = JSON.parse(JSON.stringify(geojson)); // Deep clone

    // Function to normalize a single coordinate pair.
    const normalizeCoord = (coord: number[]): number[] => {
      return [
        parseFloat(coord[0].toFixed(PRECISION)),
        parseFloat(coord[1].toFixed(PRECISION))
      ];
    };

    // Function to recursively process coordinates.
    const processCoordinates = (coords: any[]): any[] => {
      if (coords.length === 0) return coords;

      // Check if we're at a coordinate pair (depth reached).
      if (typeof coords[0] === 'number') {
        return normalizeCoord(coords);
      }

      // Otherwise process each element recursively.
      return coords.map(c => processCoordinates(c));
    };

    // Process the geometry coordinates.
    if (normalized.geometry && normalized.geometry.coordinates) {
      normalized.geometry.coordinates =
        processCoordinates(normalized.geometry.coordinates);
    }

    return normalized;
  }

  /**
   * Creates a button element with a click handler.
   *
   * @param title The title of the button (used for the tooltip).
   * @param innerHTML The HTML content of the button.
   * @param actionFn The function to call when the button is clicked.
   * @returns The created anchor element.
   */
  private createButton(title: string,
                       innerHTML: string,
                       actionFn: () => void): HTMLAnchorElement {
    const button = L.DomUtil.create('a', '');
    button.href = '#';
    button.title = title;
    button.innerHTML = innerHTML;
    button.setAttribute('role', 'button');
    button.setAttribute('aria-label', title);

    L.DomEvent.on(button, 'click', (e: MouseEvent) => {
        L.DomEvent.preventDefault(e);
        actionFn();
    }, this);

    return button;
  }

  /**
   * Creates a list item element containing a button for a draw action.
   *
   * @param actionName The name of the action (e.g., "cancel", "cut").
   * @param actionFn The function to call when the button is clicked.
   * @returns The created list item element.
   */
  private createDrawActionLi(actionName: string,
                             actionFn: () => void): HTMLLIElement {
    const li = L.DomUtil.create('li', '');
    const button = this.createButton(
      translations.map.edit.toolbar.actions[actionName].title,
      translations.map.edit.toolbar.actions[actionName].text,
      actionFn
    );
    li.append(button);
    return li;
  }

  /**
   * Event handler for the keyup event. This method cancels the tool if the
   * user presses the "Escape" key.
   *
   * @param e The Leaflet keyboard event.
   */
  private cancelOnEscKeypressHandler(e: L.LeafletKeyboardEvent): void {
    if (e.originalEvent.key === 'Escape') {
          this.cancel();
    }
  }

  /**
   * Programmatically triggers an "Escape" key press event on the map
   * container. This is used to cancel any other active Leaflet Draw/Edit
   * tools.
   */
  private triggerEscKey() {
    const escapeEvent = new KeyboardEvent('keyup', {
      key: 'Escape',
      code: 'Escape',
      keyCode: 27,
      which: 27,
      bubbles: true,
      cancelable: true,
      view: window
    });

    this.map.getContainer().dispatchEvent(escapeEvent);
  }
}
