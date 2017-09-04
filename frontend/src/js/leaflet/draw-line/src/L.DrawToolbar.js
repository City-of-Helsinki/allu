L.drawLocal.draw.toolbar.lineWidth = {
  title: 'Line width',
  text: 'Width'
};

const getActions = L.DrawToolbar.prototype.getActions;
L.DrawToolbar.prototype.getActions = function getActionsExt(handler) {
  const actions = [{
    enabled: handler.setWidth,
    title: L.drawLocal.draw.toolbar.lineWidth.title, // 'Viivan leveys', // L.drawLocal.draw.toolbar.finish.title,
    text: L.drawLocal.draw.toolbar.lineWidth.text, // 'Leveys', // L.drawLocal.draw.toolbar.finish.text,
    suffix: 'm', // as meters
    type: 'input',
    callback: handler.setWidth,
    context: handler,
    attributes: {
      type: 'number',
      value: 5.0,
      step: 0.1,
      min: 0.1,
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

