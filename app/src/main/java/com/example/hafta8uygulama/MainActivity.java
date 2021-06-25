package com.example.hafta8uygulama;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/*
emulatör'e müzik atmak için android studio'da Device File Explorer
kısmında SD Cart klasörünün içinde ki music klasörüne istenilen
müzik atılır.
 */

public class MainActivity extends AppCompatActivity {

    final static int DOSYA_SECILDI=542;
    MediaPlayer oynatici;
    boolean oynatiliyor=false;
    Context baglan;
    Timer zamanlayici;

    private String formatla(int saniye){
        int dk=saniye/60;
        int sn=saniye+dk*60;
        String str="";
        if(dk<10)
            str+="0";
        str+=dk+":";
        if (sn<10)
            str+="0";
        str+=sn;
        return str;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baglan=this;
        oynatici=new MediaPlayer();
        final TextView tvBilgi=(TextView)findViewById(R.id.tvBilgi);
        Button btnSec=(Button)findViewById(R.id.btnSec);
        final Button btnCal=(Button)findViewById(R.id.btnCal);

        btnSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("audio/mpeg");
                startActivityForResult(i,DOSYA_SECILDI);
            }
        });
        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(oynatiliyor){
                    oynatiliyor=false;
                    oynatici.stop();
                    btnCal.setBackground(getResources().getDrawable(R.drawable.play));
                }else{
                    oynatiliyor=true;
                    try {
                        oynatici.prepare();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                    oynatici.start();

                    zamanlayici=new Timer();
                    zamanlayici.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            final int sn=oynatici.getCurrentPosition()/1000;//o anda bulunduğumuz süreyi milisaniye olarak verir.
                            final int toplam=oynatici.getDuration()/1000;//Toplam süreyi milisaniye cinsinden verir.

                            runOnUiThread(new Runnable() { //Arayüzü hazırlayan thread
                                @Override
                                public void run() {
                                    tvBilgi.setText(formatla(sn)+ "/"+formatla(toplam));
                                }
                            });
                        }
                    },0,1000);

                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==DOSYA_SECILDI){
            Uri dosyaYolu=data.getData();//Mp3'ün bulunduğu konumu getData()ile aldık.
            try {
                oynatici.setDataSource(baglan,dosyaYolu);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }
}