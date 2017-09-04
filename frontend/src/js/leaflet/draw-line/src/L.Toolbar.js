const ARROW_UP = '&#9650;';
const ARROW_DOWN = '&#9660;';

function changeValue(inputEl, byValue, options) {
  inputEl.value = (parseFloat(inputEl.value) + byValue).toFixed(1);
  options.callback.call(options.context, inputEl.value);
};

function addAttributes(attributes, element) {
  Object.keys(attributes).forEach(key => {
    element.setAttribute(key, attributes[key]);
  });
}

L.Toolbar.prototype._createInput = function createInput(options) {
  let inputContainer = L.DomUtil.create('div', options.className || '', options.container);
  L.DomUtil.addClass(inputContainer, 'leaflet-input-container');

  let inputLabel = L.DomUtil.create('div', 'leaflet-input-label', inputContainer);
  let input = L.DomUtil.create('input', 'leaflet-input', inputContainer);
  L.DomUtil.addClass(input, 'no-spinners');

  let spinnerContainer = L.DomUtil.create('div', 'leaflet-input-spinner-container');
  let increaseBtn = L.DomUtil.create('button', 'leaflet-input-spinner leaflet-input-spinner-increase');
  increaseBtn.innerHTML = ARROW_UP;
  let decreaseBtn = L.DomUtil.create('button', 'leaflet-input-spinner leaflet-input-spinner-decrease');
  decreaseBtn.innerHTML = ARROW_DOWN;
  spinnerContainer.appendChild(increaseBtn);
  spinnerContainer.appendChild(decreaseBtn);

  addAttributes(options.attributes || {}, input);

  // Screen reader tag
  let sr = L.DomUtil.create('span', 'sr-only', inputContainer);

  inputContainer.appendChild(inputLabel);
  inputContainer.appendChild(input);
  if (options.suffix) {
    let inputSuffix = L.DomUtil.create('div', 'leaflet-input-suffix', inputContainer);
    inputSuffix.innerHTML = options.suffix;
    inputContainer.appendChild(inputSuffix);
  }
  inputContainer.appendChild(spinnerContainer);
  input.appendChild(sr);

  if (options.title) {
    inputContainer.title = options.title;
    sr.innerHTML = options.title;
  }

  if (options.text) {
    inputLabel.innerHTML = options.text;
    sr.innerHTML = options.text;
  }

  L.DomEvent
    .on(inputContainer, 'click', L.DomEvent.stopPropagation)
    .on(inputContainer, 'mousedown', L.DomEvent.stopPropagation)
    .on(inputContainer, 'dblclick', L.DomEvent.stopPropagation)
    .on(inputContainer, 'touchstart', L.DomEvent.stopPropagation)
    .on(inputContainer, 'click', L.DomEvent.preventDefault)
    .on(increaseBtn, 'click', event => changeValue(input, 0.1, options), options.context)
    .on(decreaseBtn, 'click', event => changeValue(input, -0.1, options), options.context)
    .on(input, 'change', event => options.callback.call(options.context, event.target.value), options.context);

  return inputContainer;
};

L.Toolbar.prototype._createActions = function createActions(handler) {
  const container = this._actionsContainer;
  let buttons = this.getActions(handler);

  // Dispose the actions toolbar (todo: dispose only not used buttons)
  for (let di = 0, dl = this._actionButtons.length; di < dl; di++) {
    this._disposeButton(this._actionButtons[di].button, this._actionButtons[di].callback);
  }
  this._actionButtons = [];

  // Remove all old buttons
  while (container.firstChild) {
    container.removeChild(container.firstChild);
  }

  for (var i = 0; i < buttons.length; i++) {
    if ('enabled' in buttons[i] && !buttons[i].enabled) {
      continue;
    }

    let li = L.DomUtil.create('li', '', container);

    let button;
    if (buttons[i].type === 'input') {
      button = this._createInput({
        title: buttons[i].title,
        text: buttons[i].text,
        suffix: buttons[i].suffix,
        attributes: buttons[i].attributes,
        container: li,
        callback: buttons[i].callback,
        context: buttons[i].context
      })
    } else {
      button = this._createButton({
        title: buttons[i].title,
        text: buttons[i].text,
        container: li,
        callback: buttons[i].callback,

        context: buttons[i].context
      });
    }

    this._actionButtons.push({
      button: button,
      callback: buttons[i].callback
    });
  }
};
