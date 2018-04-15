package ws.droid.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ws.droid.R;
import ws.droid.controller.SessionController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionController sessionController = SessionController.getInstance();
        System.out.println("E-MAIL: " + sessionController.getSessionEmail());
        System.out.println("PASSWORD: " + sessionController.getSessionPassword());
        System.out.println("HREF: " + sessionController.getSessionHref());
    }

    /**
     * Runs when you click the New User button
     * */
    public void buttonUserNew(View view) {
        Intent intent = new Intent(this, UserNewActivity.class);
        startActivity(intent);
    }

    /**
     * Runs when you click the Login button
     * */
    public void buttonLogin(View view) {
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
    }


}
