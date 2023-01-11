package fi.hel.allu.servicecore.util;

import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.domain.util.Printable;
import fi.hel.allu.servicecore.domain.FixedLocationJson;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.service.ApplicationJsonService;
import fi.hel.allu.servicecore.service.LocationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AddressMaker {

    private final LocationService locationService;
    private volatile Map<Integer, FixedLocationJson> fixedLocations;

    public AddressMaker(LocationService locationService) {
        this.locationService = locationService;
    }

    public String getAddress(LocationJson locationJson){
        return locationJson.getAddress();
    }

    public String getAddress(Location location) {
        String address = "";
        if (location != null) {
            List<FixedLocationJson> localFixedLocations = location.getFixedLocationIds().stream()
                    .map(id -> fixedLocations().get(id)).collect(Collectors.toList());
            address = getFixedLocationAddresses(localFixedLocations);
        }
        if (!address.isEmpty()){
            return address;
        }
        else {
            return Optional.ofNullable(location.getPostalAddress()).map(
                    PostalAddress::getStreetAddress).orElse(null);
        }
    }

    private String getFixedLocationAddresses(List<FixedLocationJson> localFixedLocations) {
        return Printable.forAreasWithSections(getAreasWithSections(localFixedLocations));
    }

    private static Map<String, List<String>> getAreasWithSections(List<FixedLocationJson> localFixedLocations) {
        return localFixedLocations.stream().
                collect(Collectors.groupingBy(FixedLocationJson::getArea,
                                              Collectors.mapping(FixedLocationJson::getSection, Collectors.toList())));
    }

    private Map<Integer, FixedLocationJson> fixedLocations() {
        Map<Integer, FixedLocationJson> localFixedLocations = this.fixedLocations;
        if (localFixedLocations == null) {
            synchronized (ApplicationJsonService.class) {
                localFixedLocations = this.fixedLocations;
                if (localFixedLocations == null) {
                    this.fixedLocations = localFixedLocations = locationService.getAllFixedLocations().stream()
                            .collect(Collectors.toMap(FixedLocationJson::getId, item -> item));
                }
            }
        }
        return localFixedLocations;
    }
}