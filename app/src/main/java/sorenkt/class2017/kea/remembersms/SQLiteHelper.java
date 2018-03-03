package sorenkt.class2017.kea.remembersms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class SQLiteHelper extends SQLiteOpenHelper
{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyDb.db";
    private static final String TABLE_NAME = "INFORMATION";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_PHONE = "PHONENUMBER";
    private static final String COLUMN_MESSAGE = "MESSAGE";
    private static final String COLUMN_DATE = "DATE";
    private static final String COLUMN_TIME = "TIME";

    private SQLiteDatabase database;

    public SQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //creating tables
        String CREATE_INFORMATION_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_PHONE + " TEXT," +
                COLUMN_MESSAGE + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_TIME + " TEXT" + ")";
        db.execSQL(CREATE_INFORMATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //else create!
        onCreate(db);
    }

    //CRUD operations
    //Adding new Contact
    public void insertRecord(ContactModel contact)
    {
        database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_PHONE, contact.getPhoneNumber());
        values.put(COLUMN_MESSAGE, contact.getMessage());
        values.put(COLUMN_DATE, contact.getDate());
        values.put(COLUMN_TIME, contact.getTime());
        database.insert(TABLE_NAME, null, values);
        database.close();
    }

    public ArrayList<ContactModel> getAllRecords()
    {
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<ContactModel> contacts = new ArrayList<ContactModel>();
        ContactModel contactModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();

                contactModel = new ContactModel();
                contactModel.setID(cursor.getString(0));
                contactModel.setName(cursor.getString(1));
                contactModel.setPhoneNumber(cursor.getString(2));
                contactModel.setMessage(cursor.getString(3));
                contactModel.setDate(cursor.getString(4));
                contactModel.setTime(cursor.getString(5));

                contacts.add(contactModel);
            }
        }
        cursor.close();
        database.close();

        return contacts;
    }

    public void updateRecord(ContactModel contact)
    {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, contact.getName());
        contentValues.put(COLUMN_PHONE, contact.getPhoneNumber());
        contentValues.put(COLUMN_MESSAGE, contact.getMessage());
        contentValues.put(COLUMN_DATE, contact.getDate());
        contentValues.put(COLUMN_TIME, contact.getTime());
        database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{contact.getID()});
        database.close();
    }

    //maybe for later use!
    public void deleteAllRecords()
    {
        database = this.getReadableDatabase();
        database.delete(TABLE_NAME, null, null);
        database.close();
    }


    public void deleteRecord(ContactModel contact)
    {
        database = this.getReadableDatabase();
        database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{contact.getID()});
        database.close();
    }
}
