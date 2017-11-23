
export class MapPopup {
  constructor(
    private header: HTMLElement,
    private contentRows: Array<string> = []
  ) {}

  content(): HTMLElement {
    const popup = L.DomUtil.create('div', 'popup-wrapper');
    popup.appendChild(this.header);
    const contentList = L.DomUtil.create('ul', 'popup-content', popup);
    this.contentRows.forEach(row => {
      const rowContent = L.DomUtil.create('li', undefined, contentList);
      rowContent.innerHTML = row;
    });

    return popup;
  }
}
