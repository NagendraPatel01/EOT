package com.eot_app.utility;

/*
 * Created by Sona-11 on 26/11/21.
 */

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.hypertrack.hyperlog.HyperLog;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * You have to make a clone of the file DigitalClock.java to use in your application, modify in the following manner:-
 * private final static String m12 = "h:mm aa";
 * private final static String m24 = "k:mm";
 */

public class MyDigitalClock {
    private static MyDigitalClock myDigitalClock;
    private Timer timer;
    private TimerTask timerTask;
//  private double myTimerTime = Double.parseDouble(App_preference.getSharedprefInstance().getShiftClockTime());
    private double myTimerTime = 0;

    int tempmSec=0,temphrs=0,tempminutes=0;

    private NotifyClock notifyClock;

    public static MyDigitalClock getInstance() {
        if (myDigitalClock == null) {
            myDigitalClock = new MyDigitalClock();
        }
        return myDigitalClock;
    }

    public void createTimerInstance(){
        if (timer == null)
            timer = new Timer();
        if (AppUtility.compareCheckInOutTime())
            calculateTimeDurationBck();
    }

    public void setNotifyClock(NotifyClock notifyClock) {
        this.notifyClock = notifyClock;
    }

    public void startTimerCounting() {
        HyperLog.e("TAG","Check in checkout startTimerCounting");
        if (!App_preference.getSharedprefInstance().getCheckInTime().equals("")) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        myTimerTime++;
                        String str = getTimerTime();
                        Log.e("myTimerTime-----", "" + str);
                        if (notifyClock != null) {
                            notifyClock.setClockTime(str);
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 1000, 1000);
        }
        HyperLog.e("TAG","Check in checkout startTimerCounting myTimerTime "+myTimerTime);
    }

    private String getTimerTime() {
        int round = (int) Math.round(myTimerTime);
        int second = (((round % 86000) % 3600) % 60)+tempmSec;
        int minutes = (((round % 86000) % 3600) / 60)+tempminutes;
        int hours =( ((round % 86000) / 3600))+temphrs;
        App_preference.getSharedprefInstance().setShiftClockime(String.valueOf(myTimerTime));
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", second);
    }

    public void stopTimerCounting() {
        if (timerTask != null) {
            timerTask.cancel();
            myTimerTime = 0.0;
            temphrs=tempminutes=tempmSec=0;
            App_preference.getSharedprefInstance().setShiftClockime(String.valueOf(myTimerTime));
        }
        HyperLog.e("TAG","Check in checkout stopTimerCounting myTimerTime "+myTimerTime);
    }

    public void setMyTimerTime(){
        myTimerTime=0;
        App_preference.getSharedprefInstance().setShiftClockime(String.valueOf(myTimerTime));
        if (timerTask != null)
            timerTask.cancel();
        }

    public void calculateTimeDurationBck(){
        boolean tets=false;
        try {
            String lastCheckInTime= AppUtility.getDateWithFormate(Long.parseLong(App_preference.getSharedprefInstance().getLoginRes().getLastCheckIn()),"dd/MM/yyyy-hh:mm a");

            if (!App_preference.getSharedprefInstance().getcheckId().equals("")) {
                 tets = AppUtility.compareCheckInOutTime();
                if (tets) {
                    if(lastCheckInTime!=null&&!lastCheckInTime.isEmpty()){
                        // changed by shivani 21 April 2022
                        // Now the check will be according to date time not only time because of the two                         day shift ex. 11 Pm to 3 Am
//                        App_preference.getSharedprefInstance().setCheckInTime(lastCheckInTime.split("-")[1]);
                        App_preference.getSharedprefInstance().setCheckInTime(lastCheckInTime);
                    }
                }
            }
            HyperLog.e("TAG","Check in checkout calculateTimeDates lastCheckInTime"+lastCheckInTime);

            String time1 = App_preference.getSharedprefInstance().getCheckInTime();
            // changed by shivani 21 April 2022

//          String time2 = AppUtility.getCurrentDateByFormat("hh:mm a");
            String time2 = AppUtility.getCurrentDateByFormat("dd/MM/yyyy-hh:mm a");

            if (!time1.isEmpty() && !time2.isEmpty()) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat
//                        = new SimpleDateFormat("hh:mm a");
                        = new SimpleDateFormat("dd/MM/yyyy-hh:mm a");
                Date date1 = null, date2 = null;
                try {
                    date1 = simpleDateFormat.parse(time1);
                    date2 = simpleDateFormat.parse(time2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Calculating the difference in milliseconds
                assert date2 != null;
                assert date1 != null;
                long differenceInMilliSeconds
                        = Math.abs(date2.getTime() - date1.getTime());

                Log.e("TimerTask-", String.valueOf(myTimerTime));
                Log.e("TimerTask-", String.valueOf(differenceInMilliSeconds));

                // Calculating the difference in Hours
                int numOfDays = (int) (differenceInMilliSeconds / (1000 * 60 * 60 * 24));

                long differenceInHours
                    = (differenceInMilliSeconds / (60 * 60 * 1000))
                    % 24;

                if(numOfDays>0)
                {
                    temphrs=(int)differenceInHours+(numOfDays*24);
                }
                else {
                    temphrs=(int)differenceInHours;
                }
                Log.e("TimerTask-", String.valueOf(temphrs));

                long hours = TimeUnit.MILLISECONDS.toHours(differenceInMilliSeconds) % 24;
                Log.e("TimerTask-", String.valueOf(hours));
                // Calculating the difference in Minutes
            long differenceInMinutes
                    = (differenceInMilliSeconds / (60 * 1000)) % 60;
                 tempminutes=(int)differenceInMinutes;
                Log.e("TimerTask-", String.valueOf(tempminutes));

            long differenceInSeconds
                    = (differenceInMilliSeconds / 1000) % 60;
                 tempmSec=(int)differenceInSeconds;
                Log.e("TimerTask-", String.valueOf(tempmSec));

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void calculateTimeDates()
    {
        try {
            String lastCheckInTime= AppUtility.getDateWithFormate(Long.parseLong(App_preference.getSharedprefInstance().getLoginRes().getLastCheckIn()),"dd/MM/yyyy-hh:mm a");
            if (!App_preference.getSharedprefInstance().getcheckId().equals("")) {
                boolean tets = AppUtility.compareCheckInOutTime();
                if (tets) {
                    if(lastCheckInTime!=null&&!lastCheckInTime.isEmpty()){
                        App_preference.getSharedprefInstance().setCheckInTime(lastCheckInTime);
                    }
                }
            }
            String time1 = App_preference.getSharedprefInstance().getCheckInTime();
            // changed by shivani 21 April 2022

//            String time2 = AppUtility.getCurrentDateByFormat("hh:mm a");
            String time2 = AppUtility.getCurrentDateByFormat("dd/MM/yyyy-hh:mm a");

            if (!time1.isEmpty() && !time2.isEmpty()) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat
//                        = new SimpleDateFormat("hh:mm a");
                        = new SimpleDateFormat("dd/MM/yyyy-hh:mm a");
                Date date1 = null, date2 = null;
                try {
                    date1 = simpleDateFormat.parse(time1);
                    date2 = simpleDateFormat.parse(time2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final int SECOND = 1000;
                final int MINUTE = 60 * SECOND;
                final int HOUR = 60 * MINUTE;
                final int DAY = 24 * HOUR;

                // Calculating the difference in milliseconds
                assert date2 != null;
                assert date1 != null;
                long differenceInMilliSeconds
                        = Math.abs(date2.getTime() - date1.getTime());

                Log.e("TimerTask-", String.valueOf(myTimerTime));
                Log.e("TimerTask-", String.valueOf(differenceInMilliSeconds));

                StringBuilder text = new StringBuilder("");
                if (differenceInMilliSeconds > DAY) {
                    text.append(differenceInMilliSeconds / DAY).append(" days ");
                    differenceInMilliSeconds %= DAY;
                }
                if (differenceInMilliSeconds > HOUR) {
                    text.append(differenceInMilliSeconds / HOUR).append(" hours ");
                    differenceInMilliSeconds %= HOUR;
                }
                if (differenceInMilliSeconds > MINUTE) {
                    text.append(differenceInMilliSeconds / MINUTE).append(" minutes ");
                    differenceInMilliSeconds %= MINUTE;
                }
                if (differenceInMilliSeconds > SECOND) {
                    text.append(differenceInMilliSeconds / SECOND).append(" seconds ");
                    differenceInMilliSeconds %= SECOND;
                }
                text.append(differenceInMilliSeconds).append(" ms");
                System.out.println(text.toString());
                Log.e("TimerTask-Time", text.toString());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
// TODO: this is the value in ms
    }

    public interface NotifyClock {
        void setClockTime(String str);
    }
}
