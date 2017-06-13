package com.example.deltaf.flatmates;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE=1;
    private FirebaseListAdapter<Message> adapter;
    RelativeLayout activity_main;
    RelativeLayout activity_alarm;
    FloatingActionButton fab;
    FloatingActionButton ala;
    public int alarmHour;
    public int alarmMin;
    public String alarmContent="";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_sign_out)
        {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main, "You've been signed out", Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        if(item.getItemId()==R.id.set_alarm){

            setAlarm();

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SIGN_IN_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                Snackbar.make(activity_main, "Successfully signed in, Welcome!", Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            }
            else
            {
                Snackbar.make(activity_main, "We couldn't sign you in, bye-bye", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity_main=(RelativeLayout)findViewById(R.id.activity_main);
        fab=(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener (new View.OnClickListener(){
            @Override
                    public void onClick (View view){
                EditText input = (EditText)findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new Message(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });
        //changes! SO MUCH CHANGES
        if (FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }
    else
        {
            Snackbar.make(activity_main, "Welcome " +FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT);
            displayChatMessage();
        }
    }

    private void displayChatMessage(){

        ListView listOfMessage = (ListView) findViewById(R.id.list_of_message);
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference())
        {

            @Override
            protected void populateView(View v, Message model, int position) {
                //Get references to the views of list_item.xml
                TextView messageText, messageUser, messageTime;
                messageText = (TextView) v.findViewById((R.id.message_text));
                messageUser = (TextView) v.findViewById((R.id.message_user));
                messageTime = (TextView) v.findViewById((R.id.message_time));

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyy (HH:mm:ss)", model.getMessageTime()));
            }
        };
listOfMessage.setAdapter(adapter);
    }

    private void setAlarm(){

        final Intent i=new Intent(this, MainActivity.class);
        setContentView(R.layout.activity_alarm);
        ala=(FloatingActionButton) findViewById(R.id.ala);
        ala.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick (View view){
                EditText input2 = (EditText)findViewById(R.id.alarmContent);
                TimePicker alarmTime = (TimePicker)findViewById(R.id.timePicker);
                alarmHour = alarmTime.getCurrentHour();
                alarmMin = alarmTime.getCurrentMinute();
                alarmContent = input2.getText().toString();
                Log.d("alarmHour", ""+alarmHour);
                Log.d("alarmMin", ""+alarmMin);
                Log.d("alarmContent", alarmContent);
                sendNotification(view);
                startActivity(i);
            }

        });



    }

    public void sendNotification(View view) {

//Get an instance of NotificationManager//
        long messageTime = new Date().getTime();


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_send)
                        .setContentTitle("Flatmates")
                        .setContentText(alarmContent)
                        .setWhen(messageTime + 60000 );


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());


    }

    }
