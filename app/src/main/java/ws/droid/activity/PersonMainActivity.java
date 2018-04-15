package ws.droid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;

import ws.droid.R;


public class PersonMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // link the XML layout to this JAVA class
        setContentView(R.layout.activity_person_main);
    }

    /**
     * Runs when you click the New button
     * */
    public void buttonNew(View view) {
        Intent intent = new Intent(this, PersonNewActivity.class);
        startActivity(intent);
    }

    /**
     * Runs when you click the List button
     * */
    public void buttonList(View view) {
        Intent intent = new Intent(this, PersonListActivity.class);
        startActivity(intent);
    }

}
