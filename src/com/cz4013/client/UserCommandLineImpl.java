package com.cz4013.client;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by danielseetoh on 3/30/17.
 */
public class UserCommandLineImpl implements UserCommandLine{

    private Scanner sc;
    public enum DAYS {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY};
    public ArrayList<String> confirmationIdList;

    public UserCommandLineImpl(){
        sc = new Scanner(System.in);
        confirmationIdList = new ArrayList<String>();
        getUserInput();
    }

    public void userDisplayAvailability(){
        boolean success = true;
        System.out.println("Which facility do you wish to check?");
        String facility = sc.nextLine();
        System.out.println("Which days do you wish to check? e.g. MONDAY TUEDAY");
        String days = sc.nextLine();
        String[] daysArray = days.split(" ");
        String daysIntegers = "";
        for (int i = 0; i < daysArray.length; i++){
            try{
                daysIntegers = daysIntegers + DAYS.valueOf(daysArray[i].toUpperCase()).ordinal() + " ";
            } catch (Exception e){
                System.out.println("Invalid input.");
                success = false;
            }
        }

        if(success){
            String result = displayAvailability(facility, daysIntegers.substring(0, daysIntegers.length()-1));
            System.out.println(result);
        }
    }

    public String displayAvailability(String facility, String days){
        return "PASS";
    }

    public void userBookFacility(){
        boolean success = true;
        System.out.println("Which facility do you wish to book?");
        String facility = sc.nextLine();
        System.out.println("From what time do you wish to book? (DAY/HOUR/MINUTE) e.g. MONDAY/14/45");
        String startTime = sc.nextLine();
        System.out.println("Until what time do you wish to book? (has to be the same day) e.g. MONDAY/17/30");
        String endTime = sc.nextLine();
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
        }

        if (success){
            String confirmationId = bookFacility(facility, day, startMinute, endMinute);
            System.out.println(confirmationId);
        }
    }

    public String bookFacility(String facility, int day, int startMinute, int endMinute){
        return "PASS";
    }


    public void userChangeBookingTime(){
        boolean success = true;
        System.out.println("Enter the booking confirmation Id: ");
        String confirmationId = sc.nextLine();
        System.out.println("For how long do you wish to offset this booking?");
        int offset = 0;
        try {
            offset = Integer.parseInt(sc.nextLine());
            if (offset<0){
                success = false;
            }
        } catch(Exception e){
            System.out.println("Invalid Input");
        }

        if (success){
            String result = changeBookingTime(confirmationId, offset);
            System.out.println(result);
        }
    }

    public String changeBookingTime(String confirmationId, int offset){
        return "PASS";
    }

    public void userMonitorFacility(){
        boolean success = true;
        System.out.println("Which facility do you wish to monitor?");
        String facility = sc.nextLine();
        System.out.println("For how long do you wish to monitor the facility? (In minutes)");
        int monitorInterval = 0;
        try {
            monitorInterval = Integer.parseInt(sc.nextLine());
        } catch (Exception e){
            System.out.println("Invalid Input");
        }

        if (success){
            String result = monitorFacility(facility, monitorInterval);
            System.out.println(result);
        }
    }

    public String monitorFacility(String facility, int monitorInterval){
        return "PASS";
    }

    public void userGetFacilities(){
        String result = getFacilities();
        System.out.println(result);
    }

    public String getFacilities(){
        return "PASS";
    }

    public void userChangeBookingDuration(){
        boolean success = true;
        System.out.println("Enter the booking confirmation Id: ");
        String confirmationId = sc.nextLine();
        System.out.println("For how long do you wish to extend this booking?");
        int durationExtension = 0;
        try {
            durationExtension = Integer.parseInt(sc.nextLine());
            if (durationExtension<0){
                success = false;
            }
        } catch(Exception e){
            System.out.println("Invalid Input");
        }

        if (success){
            String result = changeBookingTime(confirmationId, durationExtension);
            System.out.println(result);
        }
    }

    public String changeBookingDuration(String confirmationId, int durationExtension){
        return "PASS";
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
                input = sc.nextInt();
                sc.nextLine();
                if (input < 0 || input > 6){
                    throw new Exception();
                }

                switch(input){
                    case 1:
                        userDisplayAvailability();
                        break;
                    case 2:
                        userBookFacility();
                        break;
                    case 3:
                        userChangeBookingTime();
                        break;
                    case 4:
                        userMonitorFacility();
                        break;
                    case 5:
                        userGetFacilities();
                        break;
                    case 6:
                        userChangeBookingDuration();
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



}
