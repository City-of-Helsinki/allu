import {MIN_WIDTH} from './L.Draw.BufferPolyLine';
import {MIN_RADIUS} from "./L.Draw.FixedCircle";

L.drawLocal.draw.toolbar.lineWidth = {
  title: 'Line width',
  text: 'Width'
};

L.drawLocal.draw.toolbar.diameter = {
  title: 'Circle diameter',
  text: 'Diameter'
};

L.DrawToolbar.prototype.getActions = function getActionsExt(handler) {
  return [
    {
      enabled: handler.setWidth,
      title: L.drawLocal.draw.toolbar.lineWidth.title,
      text: L.drawLocal.draw.toolbar.lineWidth.text,
      suffix: 'm', // as meters
      type: 'input',
      callback: handler.setWidth,
      context: handler,
      attributes: {
        type: 'number',
        value: 2.0,
        step: 0.1,
        min: MIN_WIDTH,
        max: 100.0
      },
    },
    {
      enabled: handler.setMinDiameter,
      title: L.drawLocal.draw.toolbar.diameter.title,
      text: L.drawLocal.draw.toolbar.diameter.text,
      suffix: 'm', // as meters
      type: 'input',
      callback: handler.setMinDiameter,
      context: handler,
      attributes: {
        type: 'number',
        value: handler.getMinDiameter ? handler.getMinDiameter() : 2.0,
        step: 0.1,
        min: MIN_RADIUS * 2,
        max: 100.0
      },
    },
    {
      enabled: handler.completeShape,
      title: L.drawLocal.draw.toolbar.finish.title,
      text: L.drawLocal.draw.toolbar.finish.text,
      callback: handler.completeShape,
      context: handler
    },
    {
      enabled: handler.deleteLastVertex,
      title: L.drawLocal.draw.toolbar.undo.title,
      text: L.drawLocal.draw.toolbar.undo.text,
      callback: handler.deleteLastVertex,
      context: handler
    },
    {
      title: L.drawLocal.draw.toolbar.actions.title,
      text: L.drawLocal.draw.toolbar.actions.text,
      callback: this.disable,
      context: this
    }
  ];
};

L.DrawToolbar.prototype.getModeHandlers = function getModeHandlersExt(map) {
  return [
    {
      enabled: true,
      handler: new L.Draw.BufferPolyLine(map, this.options.bufferPolyline),
      title: L.drawLocal.draw.toolbar.buttons.polyline
    },
    {
      enabled: this.options.polyline,
      handler: new L.Draw.Polyline(map, this.options.polyline),
      title: L.drawLocal.draw.toolbar.buttons.polyline
    },
    {
      enabled: this.options.polygon,
      handler: new L.Draw.Polygon(map, this.options.polygon),
      title: L.drawLocal.draw.toolbar.buttons.polygon
    },
    {
      enabled: this.options.rectangle,
      handler: new L.Draw.Rectangle(map, this.options.rectangle),
      title: L.drawLocal.draw.toolbar.buttons.rectangle
    },
    {
      enabled: this.options.circle,
      handler: new L.Draw.Circle(map, this.options.circle),
      title: L.drawLocal.draw.toolbar.buttons.circle
    },
    {
      enabled: this.options.fixedCircle,
      handler: new L.Draw.FixedCircle(map, this.options.fixedCircle),
      title: L.drawLocal.draw.toolbar.buttons.circle
    },
    {
      enabled: this.options.marker,
      handler: new L.Draw.Marker(map, this.options.marker),
      title: L.drawLocal.draw.toolbar.buttons.marker
    },
    {
      enabled: this.options.circlemarker,
      handler: new L.Draw.CircleMarker(map, this.options.circlemarker),
      title: L.drawLocal.draw.toolbar.buttons.circlemarker
    }
  ];
};

