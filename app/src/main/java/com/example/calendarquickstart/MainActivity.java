package com.example.calendarquickstart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.api.client.util.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//import android.util.SimpleDateFormat;


public class MainActivity extends ActionBarActivity implements DatePickerFragment.OnDateSetListener {

    //Button printThisWeek ;
    Button cmdPrint ;
//
//    Button cmdMinDate ;
//    Button cmdMaxDate ;

    //EventFetchTask EFT = new EventFetchTask() ;
    private Calendar calendar ;
    private DateTime max = null ;
    private DateTime min = null ;
    private final String maxS = "Max" ;
    private final String minS = "Min" ;
    void f()
    {
        //if(max != null && min!= null){

        {
            Bundle mBundle = new Bundle();
            mBundle.putString("max", StrMax);
            mBundle.putString("min", StrMin);

            Intent intent = new Intent(this, UpcomingEventsActivity.class);
            intent.putExtras(mBundle);
            startActivity(intent);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        cmdPrint =(Button)findViewById(R.id.cmdPrint);

//        cmdMinDate = (Button)findViewById(R.id.cmdMinDate);
//        cmdMaxDate = (Button)findViewById(R.id.cmdMaxDate);

        cmdPrint.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                            printThisWeek();
                        //f();
                    }
                }
        );

//        cmdMaxDate.setOnClickListener(
//                new Button.OnClickListener() {
//                    public void onClick(View v) {
//                     //   showDatePickerDialog(maxS) ;
//                    }
//                }
//        );
//
//        cmdMinDate.setOnClickListener(
//                new Button.OnClickListener() {
//                    public void onClick(View v) {
//                      //  showDatePickerDialog(minS) ;
//
//                    }
//                }
//        );




    }

    private void printThisWeek() {
        //DateTime now = new DateTime(System.currentTimeMillis());
       // Log.d("log" , now.toStringRfc3339());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

// get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Log.d("log" ,"Start of this week:       " + cal.getTime());
        Log.d("log" ,"... in milliseconds:      " + cal.getTimeInMillis());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String strStart = toDateTime(year,month,day);


        StrMax = strStart ;
        // start of the next week
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        Log.d("log" ,"Start of the next week:   " + cal.getTime());
        Log.d("log" ,"... in milliseconds:      " + cal.getTimeInMillis());
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        String strEnd = toDateTime(year,month,day);
        StrMin = strEnd ;
        f();
    }
    public String dateToDay(int year, int month, int day)
    {

        String strMonth ;
        String strDay ;

        if(month < 10 )
            strMonth = "0" + month ;
        else
            strMonth ="" + month ;
        if(day < 10 )
            strDay = "0" + day ;
        else
            strDay = "" + day ;
        Date date = null  ;
        String strDate = "" + year + "-" + strMonth + "-" + strDay ;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
        return dayOfWeek ;
    }
   public String toDateTime(int year, int month, int day)
   {
       String strMonth ;
       String strDay ;

       if(month < 10 )
           strMonth = "0" + month ;
       else
           strMonth ="" + month ;
       if(day < 10 )
           strDay = "0" + day ;
       else
           strDay = "" + day ;

       String strDate = "" + year + "-" + strMonth + "-" + strDay + "T00:00:00-00:00";
       return  strDate ;

   }
    public void showDatePickerDialog(String mode) {
        DialogFragment newFragment = new DatePickerFragment().newInstance(mode);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    String StrMax = null ;
    String StrMin = null ;
    @Override
    public void onDateSet(DatePicker view,String mode , int year, int month, int day) {

        String strMonth ;
        String strDay ;

        if(month < 10 )
            strMonth = "0" + month ;
        else
            strMonth ="" + month ;
       if(day < 10 )
           strDay = "0" + day ;
        else
           strDay = "" + day ;

        String strDate = "" + year + "-" + strMonth + "-" + strDay + "T00:00:00-00:00";

        if(mode.equals(maxS))
        {
            max = DateTime.parseRfc3339(strDate);
            this.StrMax = strDate ;
           // cmdMaxDate.setText(strDate.substring(0,10));
              //  cmdMaxDate.setText(max.toString());
        }
        if(mode.equals(minS))
        {

            min = DateTime.parseRfc3339(strDate);
            this.StrMin = strDate ;
         //   cmdMinDate.setText(strDate.substring(0,10));
                //     cmdMinDate.setText(min.toString());

        }


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
