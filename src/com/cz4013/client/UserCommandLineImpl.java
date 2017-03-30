package com.cz4013.client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by danielseetoh on 3/30/17.
 */
public class UserCommandLineImpl implements UserCommandLine{

    private Scanner sc;
    private String address;
    private int port;
    public enum DAYS {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY};
    public ArrayList<String> confirmationIdList;
    public BookingSystem BSP;
    public CommunicationModule cm;

    public UserCommandLineImpl(String address, int port){
        this.sc = new Scanner(System.in);
        this.confirmationIdList = new ArrayList<String>();
        this.address = address;
        this.port = port;
    }

    public void userDisplayFacilityAvailability(){
        boolean success = true;
        System.out.println("Which facility do you wish to check?");
        String facility = this.sc.nextLine();
        if (facility.length() == 0){
            System.out.println("Invalid input.");
            return;
        }
        System.out.println("Which days do you wish to check? e.g. MONDAY TUESDAY");
        String days = this.sc.nextLine();
        if (days.length() == 0){
            System.out.println("Invalid input.");
            return;
        }
        String[] daysArray = days.split(" ");
        String daysIntegers = "";
        for (String day : daysArray){
            try{
                daysIntegers = daysIntegers + DAYS.valueOf(day.toUpperCase()).ordinal() + " ";
            } catch (Exception e){
                System.out.println("Invalid input.");
                success = false;
            }
        }

        // removes trailing space
        daysIntegers = daysIntegers.substring(0, daysIntegers.length()-1);

        if(success){
            String result = getFacilityAvailability(facility, daysIntegers);
            System.out.println(result);
        }
    }

    public String getFacilityAvailability(String facility, String days){
        return BSP.getFacilityAvailability(facility, days);
    }

    public void userBookFacility(){
        boolean success = true;
        System.out.println("Which facility do you wish to book?");
        String facility = this.sc.nextLine();
        if (facility.length() == 0){
            System.out.println("Invalid input.");
            return;
        }
        System.out.println("From what time do you wish to book? (DAY/HOUR/MINUTE) e.g. MONDAY/14/45");
        String startTime = this.sc.nextLine();
        if (startTime.length() == 0){
            System.out.println("Invalid input.");
            return;
        }
        System.out.println("Until what time do you wish to book? (has to be the same day) e.g. MONDAY/17/30");
        String endTime = this.sc.nextLine();
        if (endTime.length() == 0){
            System.out.println("Invalid input.");
            return;
        }
        int day = 0;
        int startMinute = 0;
        int endMinute = 0;
        try{
            String[] startTimeParsed = startTime.split("/");
            int startDay = DAYS.valueOf(startTimeParsed[0]).ordinal();
            int startHour = Integer.parseInt(startTimeParsed[1]);
            startMinute = Integer.parseInt(startTimeParsed[2]);
            startMinute += startHour*60;
            String[] endTimeParsed = endTime.split("/");
            int endDay = DAYS.valueOf(endTimeParsed[0]).ordinal();
            int endHour = Integer.parseInt(endTimeParsed[1]);
            endMinute = Integer.parseInt(endTimeParsed[2]);
            endMinute += endHour*60;

            day = startDay;
            if (day != endDay){
                success = false;
            }
        } catch (Exception e){
            System.out.println("Invalid Input");
            success = false;
        }

        if (success){
            String confirmationId = bookFacility(facility, day, startMinute, endMinute);
            System.out.println(confirmationId);
        }
    }

    public String bookFacility(String facility, int day, int startMinute, int endMinute){
        return BSP.bookFacility(facility, day, startMinute, endMinute);
    }


    public void userChangeBooking(){
        boolean success = true;
        System.out.println("Enter the booking confirmation Id: ");
        String confirmationId = this.sc.nextLine();
        if (confirmationId.length() == 0){
            System.out.println("Invalid input.");
            return;
        }
        System.out.println("For how long do you wish to offset this booking?");
        int offset = 0;
        try {
            offset = Integer.parseInt(this.sc.nextLine());
            if (offset<0){
                success = false;
            }
        } catch(Exception e){
            System.out.println("Invalid Input");
            success = false;
        }

        if (success){
            String result = changeBooking(confirmationId, offset);
            System.out.println(result);
        }
    }

    public String changeBooking(String confirmationId, int offset){
        return BSP.changeBooking(confirmationId, offset);
    }

    public void userMonitorFacility(){
        boolean success = true;
        System.out.println("Which facility do you wish to monitor?");
        String facility = this.sc.nextLine();
        if (facility.length() == 0){
            System.out.println("Invalid input.");
            return;
        }
        System.out.println("For how long do you wish to monitor the facility? (In minutes)");
        int monitorInterval = 0;
        try {
            monitorInterval = Integer.parseInt(this.sc.nextLine());
        } catch (Exception e){
            System.out.println("Invalid Input");
            success = false;
        }

        if (success){
            String result = monitorFacility(facility, monitorInterval);
            System.out.println(result);
            if (result == "Success Monitor"){
                System.out.println("Begin waiting for updates.");
                this.cm.waitForPacket();
            }
        }
    }

    public String monitorFacility(String facility, int monitorInterval){
        return BSP.monitorFacility(facility, this.address, monitorInterval, this.port);
    }

    public void userListFacilities(){
        String result = listFacilities();
        System.out.println(result);
    }

    public String listFacilities(){
        return BSP.listFacilities();
    }

    public void userExtendBooking(){
        boolean success = true;
        System.out.println("Enter the booking confirmation Id: ");
        String confirmationId = this.sc.nextLine();
        if (confirmationId.length() == 0){
            System.out.println("Invalid input.");
            return;
        }
        System.out.println("For how long do you wish to extend this booking?");
        int durationExtension = 0;
        try {
            durationExtension = Integer.parseInt(this.sc.nextLine());
            if (durationExtension<0){
                success = false;
            }
        } catch(Exception e){
            System.out.println("Invalid Input");
            success = false;
        }

        if (success){
            String result = extendBooking(confirmationId, durationExtension);
            System.out.println(result);
        }
    }

    public String extendBooking(String confirmationId, int durationExtension){
        return BSP.extendBooking(confirmationId, durationExtension);
//        return "PASS";
    }

    public void userQuit(){
        System.out.println("Thank you for using our system!");
    }


    public void getUserInput(){
        System.out.println("Welcome to the facility booking system!");
        int input = 10;
        do{
            System.out.println("What would you like to do next?");
            printOptions();
            try{
                input = this.sc.nextInt();
                sc.nextLine();
                if (input < 0 || input > 6){
                    throw new Exception();
                }

                switch(input){
                    case 1:
                        userDisplayFacilityAvailability();
                        break;
                    case 2:
                        userBookFacility();
                        break;
                    case 3:
                        userChangeBooking();
                        break;
                    case 4:
                        userMonitorFacility();
                        break;
                    case 5:
                        userListFacilities();
                        break;
                    case 6:
                        userExtendBooking();
                        break;
                    case 0:
                        userQuit();
                        break;
                    default:
                        System.out.println("Please input an integer from 0 to 6.");
                }

            } catch (Exception e){
                System.out.println("Please input an integer from 0 to 6.");
            }
        } while (input != 0);
    }

    public void printOptions(){
        System.out.println("(1) Get availability of facilities. ");
        System.out.println("(2) Book a facility. ");
        System.out.println("(3) Change a booking by an offset. ");
        System.out.println("(4) Add your client to the monitoring system and receive updates. ");
        System.out.println("(5) Get all facilities. ");
        System.out.println("(6) Change booking duration. ");
        System.out.println("(0) Quit. ");
    }

    public void setBookingSystemProxy(BookingSystem BSP){
        this.BSP = BSP;
    }

    public void setCommunicationModule(CommunicationModule cm){
        this.cm = cm;
    }

}
