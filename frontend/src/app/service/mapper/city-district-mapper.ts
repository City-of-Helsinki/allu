import {CityDistrict} from '../../model/common/city-district';

export class CityDistrictMapper {
  public static mapBackend(cityDistrict: BackendCityDistrict): CityDistrict {
    return (cityDistrict)
      ? new CityDistrict(cityDistrict.id, cityDistrict.districtId, cityDistrict.name)
      : undefined;
  }

  public static mapFrontend(district: CityDistrict): BackendCityDistrict {
    return (district)
      ? { id: district.id, districtId: district.districtId, name: district.name}
      : undefined;
  }
}

export interface BackendCityDistrict {
  id: number;
  districtId: number;
  name: string;
}
