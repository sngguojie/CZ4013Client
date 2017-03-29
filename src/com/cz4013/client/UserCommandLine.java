package com.cz4013.client;

/**
 * Created by danielseetoh on 3/30/17.
 */
public interface UserCommandLine {

    String displayAvailability(String facility, String days);

    String bookFacility(String facility, int day, int startMinute, int endMInute);

    String changeBookingTime(String confirmationId, int offset);

    String monitorFacility(String facility, int monitorInterval);

    String getFacilities();

    String changeBookingDuration(String confirmationId, int durationExtension);

}
