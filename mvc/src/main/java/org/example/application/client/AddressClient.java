package org.example.application.client;

import java.util.List;
import org.example.model.VehicleLocation;

public interface AddressClient {
    List<String> getAddresses(List<VehicleLocation> locations);
}
