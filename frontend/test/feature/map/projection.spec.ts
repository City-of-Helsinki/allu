import {TestBed} from '@angular/core/testing';
import {Projection} from '@feature/map/projection';

describe('Projection', () => {
  let projection: Projection;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        Projection
      ],
    });

    projection = TestBed.inject(Projection);
  });

  it('should project wgs84 to epsg:3879 correctly', () => {
    const x = 25496808.002263;
    const y = 6673112.200334;
    const longitude = 24.9424988;
    const latitude = 60.1708763;
    const epsg3879 = projection.project([longitude, latitude]);
    expect(epsg3879[0]).toBeCloseTo(x, 0);
    expect(epsg3879[1]).toBeCloseTo(y, 0);
  });

  it('should project epsg:3879 to wgs84 correctly', () => {
    const x = 25496808.002263;
    const y = 6673112.200334;
    const longitude = 24.9424988;
    const latitude = 60.1708763;
    const wgs84 = projection.unproject([x, y]);
    expect(wgs84[0]).toBeCloseTo(longitude, 0);
    expect(wgs84[1]).toBeCloseTo(latitude, 0);
  });
});
