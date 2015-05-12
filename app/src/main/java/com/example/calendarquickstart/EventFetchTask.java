package com.example.calendarquickstart;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * An asynchronous task that handles the Calendar API event list retrieval.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class EventFetchTask extends AsyncTask<Void, Void, Void> {
    private UpcomingEventsActivity mActivity;

    /**
     * Constructor.
     * @param activity UpcomingEventsActivity that spawned this task.
     */
    EventFetchTask(UpcomingEventsActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Calendar API to fetch event list.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mActivity.clearEvents();
            //  mActivity.updateEventList(fetchEventsFromCalendar());
            mActivity.updateEventList(fetchEventsFromCalendarCustom());
        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    UpcomingEventsActivity.REQUEST_AUTHORIZATION);

        } catch (IOException e) {
            mActivity.updateStatus("The following error occurred: " +
                    e.getMessage());
        }
        return null;
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */

    public List<String> fetchEventsFromCalendarCustom() throws IOException {

//        DateTime min = new DateTime(System.currentTimeMillis());
//        DateTime max = DateTime.parseRfc3339("2016-05-01T15:43:26-07:00");
        int maxResults =  40 ;
        Log.d("log",  mActivity.DTMax.toString());
        Log.d("log",mActivity.DTMin.toString() );
        return fetchEventsFromCalendarByTime( mActivity.DTMin , mActivity.DTMax, maxResults);
    }
    private List<String> fetchEventsFromCalendarByTime(DateTime min ,DateTime max,int maxResults) throws IOException {
        String tempDate = null ;
        // List the next 10 events from the primary calendar.
        //  DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Events events = mActivity.mService.events().list("primary")
                .setMaxResults(maxResults)
                .setTimeMin(max)
                .setTimeMax(min)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();

        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            DateTime end = event.getEnd().getDateTime();

            String[] startString  = start.toString().split("T"); // split dateTime To date and time
            String startDate = startString[0];
            String startTime = startString[1];

            String[] endString  = end.toString().split("T"); // split dateTime To date and time
            String endDate = endString[0];
            String endTime = endString[1];

            String eventTimes = startTime.subSequence(0,5) + "-" + endTime.subSequence(0,5);
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();
            }
            if (end == null) {
                // All-day events don't have start times, so just use
                // the start date.
                end = event.getEnd().getDate();
            }

            if(tempDate == null)
            {
                tempDate = startDate ;
                eventStrings.add(String.format("%s \n ",dateToDay(start)));
                eventStrings.add(String.format("\n"));

                eventStrings.add(String.format(" %s ",event.getSummary()));
                eventStrings.add(String.format("%s",eventTimes));
                eventStrings.add(String.format("%s", event.getLocation()));
                eventStrings.add(String.format("\n"));
            }
            else
            {
                if(tempDate.equals(startDate))
                {
                    eventStrings.add(String.format(" %s ",event.getSummary()));
                    eventStrings.add(String.format("%s",eventTimes));
                    eventStrings.add(String.format("%s", event.getLocation()));
                    eventStrings.add(String.format("\n"));
                }

                else
                {
                    tempDate = startDate ;

                    eventStrings.add(String.format("%s \n ",dateToDay(start)));
                    eventStrings.add(String.format("\n"));
                    eventStrings.add(String.format(" %s ",event.getSummary()));
                    eventStrings.add(String.format("%s",eventTimes));
                    eventStrings.add(String.format("%s", event.getLocation()));
                    eventStrings.add(String.format("\n"));
                      }
            }


            //eventStrings.add(String.format("%s \n\n %s  %s ",startDate ,  event.getSummary()  , eventTimes ));
        }
        return eventStrings;
    }
    public String dateToDay(DateTime dateTime)
    {

        String[] startString  = dateTime.toString().split("T"); // split dateTime To date and time
        String startDate = startString[0];
        String startTime = startString[1];


//        String strMonth ;
//        String strDay ;
//
//        if(month < 10 )
//            strMonth = "0" + month ;
//        else
//            strMonth ="" + month ;
//        if(day < 10 )
//            strDay = "0" + day ;
//        else
//            strDay = "" + day ;
        Date date = null  ;
//        String strDate = "" + year + "-" + strMonth + "-" + strDay ;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        try {
            date = sdf.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
        return dayOfWeek ;
    }
    private List<String> fetchEventsFromCalendar() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Events events = mActivity.mService.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();

        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();
            }
            eventStrings.add(
                    String.format("%s (%s)", event.getSummary(), start));
        }
        return eventStrings;
    }

}