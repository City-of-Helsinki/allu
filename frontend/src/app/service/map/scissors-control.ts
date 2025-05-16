import * as L from 'leaflet';

export interface ScissorsControlOptions extends L.ControlOptions {
  scissorsClickHandler?: (e: MouseEvent) => void;
}

export class ScissorsControl extends L.Control {
  public options: ScissorsControlOptions;
  private container: HTMLElement;
  
  constructor(options: ScissorsControlOptions = { position: 'topright' }) {
    super(options);
    this.options = options;
  }

  onAdd(map: L.Map): HTMLElement {
    this.container = L.DomUtil.create('div', 'leaflet-bar leaflet-control');
    this.container.style.backgroundColor = 'white';
    
    const button = L.DomUtil.create('a', 'scissors-control', this.container);
    button.href = '#';
    button.title = 'Scissors tool';
    button.setAttribute('role', 'button');
    button.setAttribute('aria-label', 'Scissors tool');

    button.innerHTML = `
      <svg width="24px" height="24px" viewBox="0 0 16 16" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:cc="http://creativecommons.org/ns#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns="http://www.w3.org/2000/svg" id="svg16" version="1.1" fill="#000000"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"><metadata id="metadata22"><rdf:rdf><cc:work><dc:format>image/svg+xml</dc:format><dc:type rdf:resource="http://purl.org/dc/dcmitype/StillImage"></dc:type><dc:title></dc:title><dc:date>2021</dc:date><dc:creator><cc:agent><dc:title>Timothée Giet</dc:title></cc:agent></dc:creator><cc:license rdf:resource="http://creativecommons.org/licenses/by-sa/4.0/"></cc:license></cc:work><cc:license rdf:about="http://creativecommons.org/licenses/by-sa/4.0/"><cc:permits rdf:resource="http://creativecommons.org/ns#Reproduction"></cc:permits><cc:permits rdf:resource="http://creativecommons.org/ns#Distribution"></cc:permits><cc:requires rdf:resource="http://creativecommons.org/ns#Notice"></cc:requires><cc:requires rdf:resource="http://creativecommons.org/ns#Attribution"></cc:requires><cc:permits rdf:resource="http://creativecommons.org/ns#DerivativeWorks"></cc:permits><cc:requires rdf:resource="http://creativecommons.org/ns#ShareAlike"></cc:requires></cc:license></rdf:rdf></metadata><path id="path892" d="M4.5 10A2.5 2.5 0 0 0 2 12.5 2.5 2.5 0 0 0 4.5 15 2.5 2.5 0 0 0 7 12.5 2.5 2.5 0 0 0 4.5 10zm0 1A1.5 1.5 0 0 1 6 12.5 1.5 1.5 0 0 1 4.5 14 1.5 1.5 0 0 1 3 12.5 1.5 1.5 0 0 1 4.5 11z" style="fill:#373737;fill-opacity:1;stroke:none;stroke-width:.5;stroke-linejoin:bevel;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opacity:1"></path><path id="path897" d="M12 1 5 11h1v1l6-9V1zM8 7.5a.5.5 0 0 1 .5.5.5.5 0 0 1-.5.5.5.5 0 0 1-.5-.5.5.5 0 0 1 .5-.5z" style="fill:#373737;fill-opacity:1;stroke:none;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"></path><path id="path892-6" d="M11.5 10a2.5 2.5 0 0 1 2.5 2.5 2.5 2.5 0 0 1-2.5 2.5A2.5 2.5 0 0 1 9 12.5a2.5 2.5 0 0 1 2.5-2.5zm0 1a1.5 1.5 0 0 0-1.5 1.5 1.5 1.5 0 0 0 1.5 1.5 1.5 1.5 0 0 0 1.5-1.5 1.5 1.5 0 0 0-1.5-1.5z" style="fill:#373737;fill-opacity:1;stroke:none;stroke-width:.5;stroke-linejoin:bevel;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opacity:1"></path><path id="path897-7" d="M4 1v2l6 9v-1h1L4 1zm4 6.5a.5.5 0 0 1 .5.5.5.5 0 0 1-.5.5.5.5 0 0 1-.5-.5.5.5 0 0 1 .5-.5z" style="fill:#373737;fill-opacity:1;stroke:none;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"></path><path id="rect936" d="M0 5h4v1H0z" style="fill:#373737;fill-opacity:1;stroke:none;stroke-width:.5;stroke-linejoin:bevel;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opacity:1"></path><path id="rect936-3" d="M12 5h4v1h-4z" style="fill:#373737;fill-opacity:1;stroke:none;stroke-width:.5;stroke-linejoin:bevel;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opacity:1"></path></g></svg>
    `;

    // Prevent map click events
    L.DomEvent.disableClickPropagation(button);
    L.DomEvent.disableScrollPropagation(button);

    // Add click handler
    L.DomEvent.on(button, 'click', (e: Event) => {
      L.DomEvent.preventDefault(e);
      if (this.options.scissorsClickHandler) {
        this.options.scissorsClickHandler(e as MouseEvent);
      }
    });

    return this.container;
  }

  onRemove(map: L.Map): void {
    // Clean up event listeners
    L.DomEvent.off(this.container);
  }
} 