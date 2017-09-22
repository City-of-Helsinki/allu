
export class MapPopup {
  constructor(
    private header: HTMLElement,
    private contentRows: Array<string> = []
  ) {}

  content(): HTMLElement {
    let popup = L.DomUtil.create('div', 'popup-wrapper');
    popup.appendChild(this.header);
    let contentList = L.DomUtil.create('ul', 'popup-content', popup);
    this.contentRows.forEach(row => {
      let rowContent = L.DomUtil.create('li', undefined, contentList);
      rowContent.innerHTML = row;
    });

    return popup;
  }
}
