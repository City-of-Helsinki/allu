import {MIN_WIDTH} from './L.Draw.BufferPolyLine';

L.drawLocal.draw.toolbar.lineWidth = {
  title: 'Line width',
  text: 'Width'
};

const getActions = L.DrawToolbar.prototype.getActions;
L.DrawToolbar.prototype.getActions = function getActionsExt(handler) {
  const actions = [{
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
  }];
  return actions.concat(getActions.call(this, handler));
};

const getModeHandlers = L.DrawToolbar.prototype.getModeHandlers;
L.DrawToolbar.prototype.getModeHandlers = function getModeHandlersExt(map) {
  const modeHandlers = [{
    enabled: true,
    handler: new L.Draw.BufferPolyLine(map, this.options.bufferPolyline),
    title: L.drawLocal.draw.toolbar.buttons.polyline
  }];
  return modeHandlers.concat(getModeHandlers.call(this, map));
};

