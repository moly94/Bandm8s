package bandm8s.hagenberg.fh.bandm8s;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_chooser);

        Button login = (Button) findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                Intent i= new Intent(getApplicationContext(), LoginActivity.class);
                i.putExtra("LoginChooser", "Login");
                startActivity(i);
            }
        });

        Button register = (Button) findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                Intent i= new Intent(getApplicationContext(), LoginActivity.class);
                i.putExtra("LoginChooser", "Register");
                startActivity(i);
            }
        });
    }
}
