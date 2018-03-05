package sorenkt.class2017.kea.remembersms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TableActivity extends AppCompatActivity
{
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;

    EditText nameEditText;
    EditText phoneEditText;
    EditText messageEditText;
    EditText dateText;
    EditText timeText;

    Button saveButton;

    static final int DATE_DIALOG_ID = 999;
    static final int TIME_DIALOG_ID = 1;
    private int mYear, mMonth, mDay;
    private int mHourOfDay, mMinute;
    SimpleDateFormat timeformat;
    SimpleDateFormat dateFormat;
    Calendar calendar;

    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_manipulation);
        getPermissionToReadUserContacts();

        limitDate();
        getAllWidgets();
        bindWidgetsWithEvent();
        checkForRequest();
        time();
    }

    public void getPermissionToReadUserContacts()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
        {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_CONTACTS))
                {
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        READ_CONTACTS_PERMISSIONS_REQUEST);
            }
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults)
    {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST)
        {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            }
                else
                {
                    Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
                }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onClickSelectContact(View btnSelectContact)
    {
        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            uriContact = data.getData();

            retrieveContactNumber();
        }
    }

    private void  retrieveContactNumber()
    {
        String contactNumber = null;
        String contactName = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact, new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst())
        {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst())
        {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst())
        {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        nameEditText.setText(contactName);

        phoneEditText = (EditText) findViewById(R.id.numberEditText);
        phoneEditText.setText("<" +formatNumber(contactNumber) + ">");

    }

    public static String formatNumber(String number)
    {
        return PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry());
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
            nameEditText.setText(getIntent().getExtras().get(Constants.PHONENUMBER).toString());
            phoneEditText.setText(getIntent().getExtras().get(Constants.NAME).toString());
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
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        dateText = (EditText) findViewById(R.id.dateText);
        timeText = (EditText) findViewById(R.id.timeText);
        phoneEditText = (EditText) findViewById(R.id.numberEditText);

        saveButton = (Button) findViewById(R.id.buttonSave);
    }

    private void onButtonClick()
    {
        if (nameEditText.getText().toString().equals("") || messageEditText.getText().toString().equals("")|| dateText.getText().toString().equals("") || timeText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "ALL fields has to be filled out", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent();
            intent.putExtra(Constants.NAME, nameEditText.getText().toString());
            intent.putExtra(Constants.PHONENUMBER, phoneEditText.getText().toString());
            intent.putExtra(Constants.MESSAGE, messageEditText.getText().toString());
            intent.putExtra(Constants.DATE, dateText.getText().toString());
            intent.putExtra(Constants.TIME, timeText.getText().toString());
            Toast.makeText(getApplicationContext(),"Saved", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    //on set!
    private TimePickerDialog.OnTimeSetListener mTimeSetLister = new TimePickerDialog.OnTimeSetListener()
    {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            Calendar datetime = Calendar.getInstance();
            Calendar c = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);
            datetime.set(Calendar.YEAR, mYear);
            datetime.set(Calendar.MONTH,mMonth);
            datetime.set(Calendar.DAY_OF_MONTH, mDay);

            if (datetime.getTimeInMillis() >= c.getTimeInMillis())
            {
                timeformat = new SimpleDateFormat("HH:mm");
                timeText.setText(timeformat.format(datetime.getTime()));

            } else
            {
                Toast.makeText(getApplicationContext(), "Invalid Time, try again", Toast.LENGTH_LONG).show();
            }
        }


    };
}
