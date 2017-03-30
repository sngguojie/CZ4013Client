package com.cz4013.client;

import java.net.InetAddress;

public interface BookingSystem {
    public String getFacilityAvailability (String facilityName, String days);

    public String bookFacility (String facilityName, int d, int s, int e);

    public String changeBooking (String confirmID, int offset);

    public String monitorFacility (String facilityName, String address, int intervalMinutes, int port);

    public String listFacilities ();

    public String extendBooking (String confirmId, int extensionDuration);

    public String createFacility (String facilityName);

}