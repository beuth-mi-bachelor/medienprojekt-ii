package com.example.angi.schabuu;

/**
 * Created by angi on 17.05.15.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class SplashScreen extends Activity {

 /*   @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent i = new Intent("com.coderefer.androidsplashscreenexample.MAINACTIVITY");
                    startActivity(i);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
*/

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        StartAnimations();

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(5000);

                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent i = new Intent("com.coderefer.androidsplashscreenexample.MAINACTIVITY");
                    startActivity(i);
                }
            }
        };
        timerThread.start();
    }
    private void StartAnimations() {

        final ImageView aA = (ImageView) findViewById(R.id.A);
        final ImageView bB = (ImageView) findViewById(R.id.B);
        final ImageView cC = (ImageView) findViewById(R.id.C);
        final ImageView dD = (ImageView) findViewById(R.id.D);
        final ImageView eE = (ImageView) findViewById(R.id.E);
        final ImageView fF = (ImageView) findViewById(R.id.F);
        final ImageView gG = (ImageView) findViewById(R.id.G);
        final ImageView hH = (ImageView) findViewById(R.id.H);
        final ImageView iI = (ImageView) findViewById(R.id.I);
        final ImageView jJ = (ImageView) findViewById(R.id.J);
        final ImageView kK = (ImageView) findViewById(R.id.K);
        final ImageView lL = (ImageView) findViewById(R.id.L);
        final ImageView mM = (ImageView) findViewById(R.id.M);
        final ImageView nN = (ImageView) findViewById(R.id.N);
        final ImageView oO = (ImageView) findViewById(R.id.O);
        final ImageView pP = (ImageView) findViewById(R.id.P);
        final ImageView qQ = (ImageView) findViewById(R.id.Q);
        final ImageView rR = (ImageView) findViewById(R.id.R);
        final ImageView sS = (ImageView) findViewById(R.id.S);
        final ImageView tT = (ImageView) findViewById(R.id.T);
        final ImageView uU = (ImageView) findViewById(R.id.U);
        final ImageView vV = (ImageView) findViewById(R.id.V);
        final ImageView wW = (ImageView) findViewById(R.id.W);
        final ImageView xX = (ImageView) findViewById(R.id.X);
        final ImageView yY = (ImageView) findViewById(R.id.Y);
        final ImageView zZ = (ImageView) findViewById(R.id.Z);

        ArrayList<ImageView> buchstaben = new ArrayList<>();

        buchstaben.add(aA);
        buchstaben.add(bB);
        buchstaben.add(cC);
        buchstaben.add(dD);
        buchstaben.add(eE);
        buchstaben.add(fF);
        buchstaben.add(gG);
        buchstaben.add(hH);
        buchstaben.add(iI);
        buchstaben.add(jJ);
        buchstaben.add(kK);
        buchstaben.add(lL);
        buchstaben.add(mM);
        buchstaben.add(nN);
        buchstaben.add(oO);
        buchstaben.add(pP);
        buchstaben.add(qQ);
        buchstaben.add(rR);
        buchstaben.add(sS);
        buchstaben.add(tT);
        buchstaben.add(uU);
        buchstaben.add(vV);
        buchstaben.add(wW);
        buchstaben.add(xX);
        buchstaben.add(yY);
        buchstaben.add(zZ);

        for(ImageView element: buchstaben){
            System.out.println(element.getDrawable());
        }
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                aA.setVisibility(View.GONE);
                bB.setVisibility(View.GONE);
                cC.setVisibility(View.GONE);
            }
        }, 1999);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate_1);
        anim.reset();
        aA.clearAnimation();
        aA.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate_2);
        anim.reset();
        bB.clearAnimation();
        bB.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate_1);
        anim.reset();
        cC.clearAnimation();
        cC.startAnimation(anim);
    }
}
