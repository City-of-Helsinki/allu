import {CityDistrict} from '../../model/common/city-district';

export class CityDistrictMapper {
  public static mapBackend(cityDistrict: BackendCityDistrict): CityDistrict {
    return (cityDistrict)
      ? new CityDistrict(cityDistrict.districtId, cityDistrict.name)
      : undefined;
  }

  public static mapFrontend(district: CityDistrict): BackendCityDistrict {
    return (district)
      ? { districtId: district.districtId, name: district.name}
      : undefined;
  }
}

export interface BackendCityDistrict {
  districtId: number;
  name: string;
}
