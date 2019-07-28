package si.scng.scng;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UpdatesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Posodobitve aplikacije");
        setContentView(R.layout.activity_updates);
    }
}
