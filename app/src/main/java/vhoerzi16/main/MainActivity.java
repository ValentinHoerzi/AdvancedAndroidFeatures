package vhoerzi16.main;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    public static boolean ACCESS_GRANTED;

    DatabaseHelper mDatabaseHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        mDatabaseHelper = new DatabaseHelper(this);


    }

    public void onButtonClick(View view) {
        if(ACCESS_GRANTED){

        }else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                Log.e("ContextCompat.checkSelfPermission","FIRST");

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)) {}
                else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    Log.e("ActivityCompat.requestPermissions","THIRD");

                }
            } else {
                ACCESS_GRANTED = true;
                readContacts();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ACCESS_GRANTED = true;
        readContacts();
    }

    private void readContacts() {

        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = null;
        ContentResolver contentResolver = getContentResolver();

        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI
                , null
                , null
                , null
                , null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String number = "";

                boolean hasNumber = (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) == 1 ? true : false);

                if (hasNumber) {
                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            ,null
                            ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                            ,new String[]{id}
                            ,null);

                    while (phoneCursor.moveToNext()) {
                        number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phoneCursor.close();
                }
                contacts.add(new Contact(name, number, Integer.parseInt(id)));
            }
        }

        myListViewLayoutAdapter adapter = new myListViewLayoutAdapter(getApplicationContext(), R.layout.my_listview_layout, contacts);
        listView.setAdapter(adapter);

        for(Contact c : contacts){
            AddData(c.getContact_name(),c.getContact_TelefonNr());
        }
    }

    public void AddData(String name,String number) {
        boolean insertData = mDatabaseHelper.addData(name,number);

        if (insertData) {
            toastMessage("Data Successfully Inserted!");
        } else {
            toastMessage("Something went wrong");
        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}

