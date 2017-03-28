package ru.example.sic.my_ads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import ru.example.sic.my_ads.R;

import static ru.example.sic.my_ads.Constants.RC_LOGIN;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ParseUser.getCurrentUser() == null) {
            ParseLoginBuilder builder = new ParseLoginBuilder(this);
            Intent parseLoginIntent = builder
                    //.setAppLogo(R.mipmap.logo_vorschau900)
                    .setParseLoginEnabled(true)
                    .setParseLoginButtonText(getString(R.string.login_text))
                    .setParseLoginEmailAsUsername(true)
                    .build();
            startActivityForResult(parseLoginIntent, RC_LOGIN);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}