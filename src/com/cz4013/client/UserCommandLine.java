package com.cz4013.client;

/**
 * Created by danielseetoh on 3/30/17.
 */
public interface UserCommandLine {

    String getFacilityAvailability(String facility, String days);

    String bookFacility(String facility, int day, int startMinute, int endMinute);

    String changeBooking (String confirmationId, int offset);

    String monitorFacility(String facility, int monitorInterval);

    String listFacilities();

    String extendBooking (String confirmationId, int durationExtension);

}
