package com.example.videogame_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebsiteVideogameActivity extends AppCompatActivity {

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website_videogame);

        WebView website = findViewById(R.id.webviewVideogameDetail);
        //Button buttonReturn = findViewById(R.id.buttonReturnWebsite);

        WebSettings webSettings = website.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //__Cojo los datos del intent
        bundle = getIntent().getExtras();
        String web = bundle.getString("URL");
        //Log.d("WEB_____________________________", web);

        website.setWebViewClient(new WebsiteVideogameActivity.CallbackWeb());
        website.loadUrl(web);

        //__Boton para volver a la pagina detalle
//        Intent intentVolver = new Intent(this, VideogameDetailActivity.class);
//        buttonReturn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(intentVolver);
//            }
//        });
    }

    private class CallbackWeb extends WebViewClient {
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent keyEvent){
            return false;
        }
    }
}