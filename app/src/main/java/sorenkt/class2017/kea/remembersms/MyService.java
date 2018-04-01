package sorenkt.class2017.kea.remembersms;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;


public class MyService extends JobService
{
        private JobParameters params;

        Runnable runnable = new Runnable()
        {
            @Override
            public void run() {
                //Your work , i.e upload some date to your server

//                sendSMS("1234","Hallo, håber det her virker nu");
                PersistableBundle bundle = params.getExtras();

                Log.d("TAG", "Db slettede");
                jobFinished(params, false); // we have to call this method if we returned true

            }
        };

        @Override
        public boolean onStartJob(final JobParameters params)
        {
            this.params = params;
            new Thread(runnable).start();

            //All work is completed -> return false;
            return true; // we are not done yet, we have a background thread running
        }


        @Override
        public boolean onStopJob(JobParameters params)
        {
            Log.d("TAG: ","Stopped");
            //Called if the job is canceled before it finishes i.e when wifi has gone
            return true; //true if we want to reschedule the job
        }

    private void sendSMS(String phoneNumber, String message)
    {
        // hvordan skal den hente fra db ?
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);

        sendNotification();
    }


    public void sendNotification()
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setSmallIcon(R.drawable.ic_message_white_24dp);
        mBuilder.setContentTitle("RememberSMS");
        mBuilder.setContentText("Din besked er nu sendt til ");

        NotificationManager mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());
    }


}
