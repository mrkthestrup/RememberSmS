package sorenkt.class2017.kea.remembersms;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main extends AppCompatActivity
{
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    private int mYear, mMonth, mDay;
    private int mHourOfDay, mMinute;
    EditText editText;
    EditText editText2;

    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHourOfDay = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        //String dateFormat = "dd/MM/yyyy";
        editText = (EditText) findViewById(R.id.text);
        editText2 = (EditText) findViewById(R.id.text2);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        editText.setText(sdf.format(c.getTime()));
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        editText2.setText(time.format(c.getTime()));

        editText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog(DATE_DIALOG_ID);

            }
        });

        editText2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog(TIME_DIALOG_ID);
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_list:
                                launchActivity();
                                break;
                            case R.id.action_about:
                                break;
                            case R.id.action_exit:
                                break;
                        }
                        return false;
                    }
                });
    }

    private void launchActivity() {

        Intent intent = new Intent(this, List.class);
        startActivity(intent);
    }

    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);

            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetLister, mHourOfDay, mMinute, true);
        }
        return null;

    }


    private TimePickerDialog.OnTimeSetListener mTimeSetLister = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            mHourOfDay = hourOfDay;
            mMinute = minute;
            editText2.setText(new StringBuilder().append(mHourOfDay).append(":").append(mMinute));
            if (minute <= 9)
            {
                editText2.setText(new StringBuilder().append(mHourOfDay).append(":0").append(mMinute));
            }
            System.out.println("-------");
            System.out.println(editText2.getText());
        }
    };

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
    {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth)
        {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            editText.setText(new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear));
        }

    };


}
