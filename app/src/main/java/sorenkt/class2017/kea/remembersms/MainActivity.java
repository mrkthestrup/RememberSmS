package sorenkt.class2017.kea.remembersms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    Button btnAddNewRecord;
    SQLiteHelper sQLiteHelper;
    android.widget.LinearLayout parentLayout;
    LinearLayout layoutDisplayPeople;
    TextView noRecordsFound;

    public static String ROWID = String.valueOf(TableActivity.jobID);
    private static final int PERMISSION_REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getAllWidgets();
        sQLiteHelper = new SQLiteHelper(MainActivity.this);
        bindWidgetsWithEvent();
        displayAllRecords();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},PERMISSION_REQUEST_CODE);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            String name = data.getStringExtra(Constants.NAME);
            String phone = data.getStringExtra(Constants.PHONENUMBER);
            String message = data.getStringExtra(Constants.MESSAGE);
            String date = data.getStringExtra(Constants.DATE);
            String time = data.getStringExtra(Constants.TIME);

            ContactModel contact = new ContactModel();
            contact.setName(name);
            contact.setPhoneNumber(phone);
            contact.setMessage(message);
            contact.setDate(date);
            contact.setTime(time);

            if (requestCode == Constants.ADD_RECORD)
            {
                sQLiteHelper.insertRecord(contact);
            }
            else if (requestCode == Constants.UPDATE_RECORD)
            {
                contact.setID(ROWID);
                sQLiteHelper.updateRecord(contact);
            }
            displayAllRecords();
        }
    }


    private void getAllWidgets()
    {
        btnAddNewRecord = (Button) findViewById(R.id.addNewRecord);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        layoutDisplayPeople = (LinearLayout) findViewById(R.id.layoutDisplayPeople);

        noRecordsFound = (TextView) findViewById(R.id.noRecordsFound);
    }

    private void bindWidgetsWithEvent()
    {
        btnAddNewRecord.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onAddRecord();
            }
        });
    }

    private void onAddRecord()
    {
        Intent intent = new Intent(MainActivity.this, TableActivity.class);
        intent.putExtra(Constants.DML_TYPE, Constants.INSERT);
        startActivityForResult(intent, Constants.ADD_RECORD);
    }

    private void onUpdateRecord(String name, String phone, String message, String date, String time)
    {
        Intent intent = new Intent(MainActivity.this, TableActivity.class);
        //for some reason i have to change phone and name around
        intent.putExtra(Constants.NAME,phone);
        intent.putExtra(Constants.PHONENUMBER, name);
        intent.putExtra(Constants.MESSAGE, message);
        intent.putExtra(Constants.DATE, date);
        intent.putExtra(Constants.TIME, time);
        intent.putExtra(Constants.DML_TYPE, Constants.UPDATE);
        startActivityForResult(intent, Constants.UPDATE_RECORD);
    }


    private void displayAllRecords()
    {
        android.widget.LinearLayout inflateParentView;
        parentLayout.removeAllViews();

        ArrayList<ContactModel> contacts = sQLiteHelper.getAllRecords();

        if (contacts.size() > 0)
        {
            noRecordsFound.setVisibility(View.GONE);
            ContactModel contactModel;
            for (int i = 0; i < contacts.size(); i++)
            {
                contactModel = contacts.get(i);

                final Holder holder = new Holder();
                final View view = LayoutInflater.from(this).inflate(R.layout.inflate_record, null);
                inflateParentView = (LinearLayout) view.findViewById(R.id.inflateParentView);
                holder.fullContrac = (TextView) view.findViewById(R.id.tvFullName);

                view.setTag(contactModel.getID());
                holder.name = contactModel.getName();
                holder.phone = contactModel.getPhoneNumber();
                holder.message = contactModel.getMessage();
                holder.date = contactModel.getDate();
                holder.time = contactModel.getTime();

                String information ="Name: " + holder.name + holder.phone + " \n" +
                                    "Message: " + holder.message+ " \n" +
                                    "Date: " + holder.date + " \n" +
                                    "Time: " + holder.time;
                holder.fullContrac.setText(information);

                final CharSequence[] items = {Constants.UPDATE, Constants.DELETE};
                inflateParentView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setItems(items, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (which == 0)
                                {
                                    ROWID = view.getTag().toString();
                                    onUpdateRecord(holder.name, holder.phone, holder.message, holder.date, holder.time);
                                    TableActivity.mJobScheduler.cancel(Integer.parseInt(ROWID));

                                } else
                                    {
                                    AlertDialog.Builder deleteDialogOk = new AlertDialog.Builder(MainActivity.this);
                                    deleteDialogOk.setTitle("Delete Task?");
                                    deleteDialogOk.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    //sQLiteHelper.deleteRecord(view.getTag().toString());
                                                    ContactModel contact = new ContactModel();
                                                    contact.setID(view.getTag().toString());
                                                    sQLiteHelper.deleteRecord(contact);
                                                    displayAllRecords();
                                                }
                                            }
                                    );
                                    deleteDialogOk.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {

                                        }
                                    });
                                    deleteDialogOk.show();
                                }
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return true;
                    }
                });
                parentLayout.addView(view);
            }
        } else
            {
            noRecordsFound.setVisibility(View.VISIBLE);
        }
    }
}
