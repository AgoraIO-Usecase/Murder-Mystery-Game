package io.agora.murder.mystery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import io.agora.murder.utils.ConstantApp;
import io.agora.rtc.Constants;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 加入游戏或者是围观
         * **/

        findViewById(R.id.home_button_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, CrimeActivity.class);
                i.putExtra(ConstantApp.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);

                startActivity(i);

            }
        });

        findViewById(R.id.home_button_look).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, CrimeActivity.class);
                i.putExtra(ConstantApp.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);

                startActivity(i);
            }
        });
    }
}
