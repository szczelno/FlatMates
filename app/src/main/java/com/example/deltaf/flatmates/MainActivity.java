package com.example.deltaf.flatmates;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE=1;
    private FirebaseListAdapter<Message> adapter;
    RelativeLayout activity_main;
    FloatingActionButton fab;

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

        //changes! SO MUCH CHANGES
        if (FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }
    else
        {
            Snackbar.make(activity_main, "Welcome " +FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT);
        }
        displayChatMessage();
    }
    private void displayChatMessage()
    {

    }
}
