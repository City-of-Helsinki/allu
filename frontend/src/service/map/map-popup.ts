import PopupOptions = L.PopupOptions;
import LatLng = L.LatLng;

export class MapPopup {
  constructor(
    private header: string,
    private contentRows: Array<string> = []
  ) {}

  content(): string {
    let popup = L.DomUtil.create('div', 'popup-wrapper');
    let header = L.DomUtil.create('h1', 'popup-header', popup);
    header.innerHTML = this.header;

    let contentList = L.DomUtil.create('ul', 'popup-content', popup);
    this.contentRows.forEach(row => {
      let rowContent = L.DomUtil.create('li', undefined, contentList);
      rowContent.innerHTML = row;
    });

    return popup.outerHTML;
  }
}
