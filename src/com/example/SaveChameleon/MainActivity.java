package com.example.SaveChameleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.SaveChameleon.view.AnimationView;

import java.util.*;


public class MainActivity extends Activity implements View.OnClickListener{
    AnimationView animationView;
    List<Integer> colors;
    List<Button> buttons;
    int buttonNum = 4;
    int aberrationStart,aberrationStop;
    int whichButton;
    int rightButton;
    LinearLayout topLayout;
    int choosedButton;
    Timer timer;
    TimerTask timeOutTask,timebarTask,frogDisappearTask,rightChoiceTask,wrongChoiceTask;

    float actionBarValue;

    boolean isRun = true;


    ColorDrawable frogColorDrawable;
    int frogColor;

    int timeOutNum = 0;
    public static final int TIMEOUT = 0;
    public static final int TIMEBARMOVE = 1;
    public static final int FROGDISAPPEAR = 2;
    public static final int RIGHTCHOICE = 3;
    public static final int WRONGCHOICE = 4;

    private RoundCornerProgressBar timeBar;

    long pauseRestTime = 0;
    long lastExecuteTime;

    Point point;

    Handler handler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        timer = new Timer();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case TIMEOUT:
                        timeBarReset();
                        changeUIColor();
                        animationView.startAnimation();
                        timeOutNum++;
                        if (timeOutNum == 5){
                            timeOutTask.cancel();
                            timebarTask.cancel();
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setIcon(R.drawable.frog);
                            dialog.setTitle("FUNK ONE MORE TIME");
                            dialog.setCancelable(false);
                            dialog.setNegativeButton("YEAH", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    animationView.initView(point.x);
                                    timeOutNum = 0;
                                    timeOutTask = new TimeOutTask();
                                    timebarTask = new TimeBarMoveTask();
                                    timer.schedule(timeOutTask,0,5000);
                                    timer.schedule(timebarTask,0,50);
                                }
                            });
                            dialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onDestroy();
                                    onBackPressed();
                                }
                            });
                            dialog.show();

                        }
                        break;
                    case TIMEBARMOVE:
                        timeBarMove();
                        break;
                    case FROGDISAPPEAR:
                        animationView.decreaseAlpha();
                        break;
                    case RIGHTCHOICE:
                        newRound(true);
                        break;
                    case WRONGCHOICE:
                        newRound(false);
                        break;

                }
            }
        };

        timeOutTask = new TimeOutTask();
        timebarTask = new TimeBarMoveTask();

        colors = new ArrayList<>();
        point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        animationView = new AnimationView(this,point.x);
        setContentView(R.layout.main);

        topLayout = (LinearLayout)findViewById(R.id.animation_layout);
        topLayout.addView(animationView);

        timeBar = (RoundCornerProgressBar)findViewById(R.id.time_bar);

        Button button1 = (Button)findViewById(R.id.button1);
        Button button2 = (Button)findViewById(R.id.button2);
        Button button3 = (Button)findViewById(R.id.button3);
        Button button4 = (Button)findViewById(R.id.button4);
        Button pauseButton = (Button)findViewById(R.id.pause_button);

        buttons = new ArrayList<>();
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);

        for (Button b:buttons){
            b.setOnClickListener(this);
        }

        aberrationStart = 10;
        aberrationStop = 50;

        timer.schedule(timeOutTask,0,5000);
        timer.schedule(timebarTask,0,50);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionBarValue = timeBar.getProgress();
                long currentTime = System.currentTimeMillis();
                pauseRestTime =pauseRestTime + currentTime-lastExecuteTime;

                timeOutTask.cancel();
                timebarTask.cancel();

                timeOutTask = new TimeOutTask();
                timebarTask = new TimeBarMoveTask();

                if (!isRun) {
                    if (choosedButton == rightButton) {
                        rightChoiceTask.cancel();
                    } else {
                        wrongChoiceTask.cancel();
                    }
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setIcon(R.drawable.frog);
                dialog.setTitle("CONTINUE");
                dialog.setCancelable(false);
                dialog.setNegativeButton("YEAH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lastExecuteTime = System.currentTimeMillis();
                        if (isRun){
                            timeBar.setProgress(actionBarValue);
                            timer.schedule(timebarTask,0,50);
                            timer.schedule(timeOutTask,(long)5000-pauseRestTime);
                        }else {
                            if (choosedButton == rightButton) {
                                rightChoiceTask = new RightChoiceTask();
                                timer.schedule(rightChoiceTask, 0);
                            } else {
                                wrongChoiceTask = new WrongChoiceTask();
                                timer.schedule(wrongChoiceTask, 0);
                            }
                        }
                    }
                });
                dialog.show();
            }
        });

    }

    private void genRandomColor(List<Integer> colorList,int num,int aberrationStart,int aberrationStop){
        colorList.clear();

        int red = (int)(30 + Math.random() * 190);
        int green = (int)(30 + Math.random() * 190);
        int blue = (int)(30 + Math.random() * 190);
        int color = 0xff000000 | red << 16 | green << 8 | blue;
        colorList.add(color);

        int temporaryR,temporaryG,temporaryB,temporaryColor;

        for (int i = 1;i<num;i++){
            if ((Math.random()*100)<50){
                temporaryR = red - (int)(Math.random()*2*(aberrationStop-aberrationStart) - aberrationStart);
                temporaryG = green - (int)(Math.random()*2*(aberrationStop-aberrationStart) - aberrationStart);
                temporaryB = blue - (int)(Math.random()*2*(aberrationStop-aberrationStart) - aberrationStart);
            }else {
                temporaryR = red + (int)(Math.random()*2*(aberrationStop-aberrationStart) - aberrationStart);
                temporaryG = green + (int)(Math.random()*2*(aberrationStop-aberrationStart) - aberrationStart);
                temporaryB = blue + (int)(Math.random()*2*(aberrationStop-aberrationStart) - aberrationStart);
            }
            temporaryColor = 0xff000000 | temporaryR << 16 | temporaryG << 8 | temporaryB;
            colorList.add(temporaryColor);
        }
    }

    private void changeUIColor(){
        genRandomColor(colors,buttonNum,aberrationStart,aberrationStop);
        topLayout.setBackgroundColor(colors.get(0));

        whichButton = (int)(Math.random() * colors.size());
        rightButton = whichButton;
        for (int i = 0;i<colors.size();i++){
            buttons.get(whichButton%(colors.size())).setBackgroundColor(colors.get(i));
            whichButton++;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                choosedButton = 0;
                frogColorDrawable = (ColorDrawable)buttons.get(0).getBackground();
                frogColor = frogColorDrawable.getColor();
                break;
            case R.id.button2:
                choosedButton = 1;
                frogColorDrawable = (ColorDrawable)buttons.get(1).getBackground();
                frogColor = frogColorDrawable.getColor();
                break;
            case R.id.button3:
                choosedButton = 2;
                frogColorDrawable = (ColorDrawable)buttons.get(2).getBackground();
                frogColor = frogColorDrawable.getColor();
                break;
            case R.id.button4:
                choosedButton = 3;
                frogColorDrawable = (ColorDrawable)buttons.get(3).getBackground();
                frogColor = frogColorDrawable.getColor();
                break;
        }

        if (timeOutNum<5 && isRun) {
            timebarTask.cancel();
            timebarTask = new TimeBarMoveTask();
            timeOutTask.cancel();
            timeOutTask = new TimeOutTask();
            isRun = false;
            if (choosedButton == rightButton) {
                frogDisappearTask = new FrogDisappearTask();
                timer.schedule(frogDisappearTask, 0, 10);
                rightChoiceTask = new RightChoiceTask();
                timer.schedule(rightChoiceTask, 2000);
            } else {
                animationView.changFrogColor(frogColor);
                wrongChoiceTask = new WrongChoiceTask();
                timer.schedule(wrongChoiceTask, 2000);
            }
        }
    }

    private void newRound(boolean isRight){

        timer.schedule(timebarTask,0,50);
        lastExecuteTime = System.currentTimeMillis();
        isRun = true;
        if (isRight) {
            pauseRestTime = 0;
            timeBarReset();
            frogDisappearTask.cancel();
            changeUIColor();
            animationView.changFrogColor(frogColor);
            timer.schedule(timeOutTask, 5000, 5000);
        }else {
            timer.schedule(timeOutTask, 0, 5000);
        }
    }

    private void timeBarMove(){
        timeBar.setProgress(timeBar.getProgress() + 1);
    }

    private void timeBarReset(){
        timeBar.setProgress(0);
    }

    class TimeOutTask extends TimerTask{
        @Override
        public void run() {
            Message message = new Message();
            message.what = TIMEOUT;
            handler.sendMessage(message);
            lastExecuteTime = scheduledExecutionTime();
            pauseRestTime = 0;
        }
    }

    class TimeBarMoveTask extends TimerTask{
        @Override
        public void run() {
            Message message = new Message();
            message.what = TIMEBARMOVE;
            handler.sendMessage(message);
        }
    }

    class FrogDisappearTask extends TimerTask{
        @Override
        public void run() {
            Message message = new Message();
            message.what = FROGDISAPPEAR;
            handler.sendMessage(message);
        }
    }

    class RightChoiceTask extends TimerTask{
        @Override
        public void run() {
            Message message = new Message();
            message.what = RIGHTCHOICE;
            handler.sendMessage(message);
        }
    }

    class WrongChoiceTask extends TimerTask{
        @Override
        public void run() {
            Message message = new Message();
            message.what = WRONGCHOICE;
            handler.sendMessage(message);
        }
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
