package sorenkt.class2017.kea.remembersms;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TableActivity extends AppCompatActivity
{
    EditText phoneEditText;
    EditText messageEditText;
    EditText dateText;
    EditText timeText;
    EditText nameText;
    static final int DATE_DIALOG_ID = 999;
    static final int TIME_DIALOG_ID = 1;
    private int mYear, mMonth, mDay;
    private int mHourOfDay, mMinute;

    SimpleDateFormat timeformat;
    SimpleDateFormat dateFormat;
    Calendar calendar;

    Button saveButton;

    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_manipulation);

        //maybe there is a better way to do this, but for now i works
        nameText = (EditText) findViewById(R.id.NameEditText);
        nameText.setVisibility(View.GONE);

        limitDate();
        getAllWidgets();
        bindWidgetsWithEvent();
        checkForRequest();
        time();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    private void time()
    {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, mHourOfDay);
        calendar.set(Calendar.MINUTE,mMinute);

        timeText = (EditText) findViewById(R.id.timeText);
        timeformat = new SimpleDateFormat("HH:mm");
        timeText.setText(timeformat.format(calendar.getTime()));

        timeText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TimePickerDialog mtime = new TimePickerDialog(TableActivity.this,mTimeSetLister,mHourOfDay,mMinute,true);

                mtime.show();
                showDialog(TIME_DIALOG_ID);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    private void limitDate()
    {
        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        //String dateFormat = "dd/MM/yyyy";
        dateText = (EditText) findViewById(R.id.dateText);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        dateText.setText(dateFormat.format(calendar.getTime()));

        dateText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog mDate = new DatePickerDialog(TableActivity.this, date, 2016, 2, 24);
                mDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDate.show();
                showDialog(DATE_DIALOG_ID);
            }
        });
    }

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            view.setMinDate(System.currentTimeMillis() - 1000);

            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            dateText.setText(new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear));
        }
    };

    private void checkForRequest()
    {
        String request = getIntent().getExtras().get(Constants.DML_TYPE).toString();
        if (request.equals(Constants.UPDATE)) {
            saveButton.setText(Constants.UPDATE);
            phoneEditText.setText(getIntent().getExtras().get(Constants.PHONENUMBER).toString());
            messageEditText.setText(getIntent().getExtras().get(Constants.MESSAGE).toString());
            dateText.setText(getIntent().getExtras().get(Constants.DATE).toString());
            timeText.setText(getIntent().getExtras().get(Constants.TIME).toString());
        } else {
            saveButton.setText(Constants.INSERT);
        }
    }

    private void bindWidgetsWithEvent()
    {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });
    }

    private void getAllWidgets()
    {
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        dateText = (EditText) findViewById(R.id.dateText);
        timeText = (EditText) findViewById(R.id.timeText);

        saveButton = (Button) findViewById(R.id.buttonSave);
    }

    private void onButtonClick()
    {
        if (phoneEditText.getText().toString().equals("") || messageEditText.getText().toString().equals("")|| dateText.getText().toString().equals("") || timeText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "ALL fields has to be filled out", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent();
            intent.putExtra(Constants.PHONENUMBER, phoneEditText.getText().toString());
            intent.putExtra(Constants.MESSAGE, messageEditText.getText().toString());
            intent.putExtra(Constants.DATE, dateText.getText().toString());
            intent.putExtra(Constants.TIME, timeText.getText().toString());
            Toast.makeText(getApplicationContext(),"Saved", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    private TimePickerDialog.OnTimeSetListener mTimeSetLister = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            mHourOfDay = hourOfDay;
            mMinute = minute;
            timeText.setText(new StringBuilder().append(mHourOfDay).append(":").append(mMinute));
            if (minute <= 9)
            {
                timeText.setText(new StringBuilder().append(mHourOfDay).append(":0").append(mMinute));
            }
        }
    };
}
