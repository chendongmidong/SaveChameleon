package com.example.SaveChameleon;

import android.app.*;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.SaveChameleon.fragment.FourButtonFragment;
import com.example.SaveChameleon.fragment.NineButtonFragment;
import com.example.SaveChameleon.fragment.SixButtonFragment;
import com.example.SaveChameleon.view.AnimationView;

import java.util.*;

public class MainActivity extends Activity {
    public AnimationView animationView;
    LinearLayout backgroundLayout;
    List<Integer> colors;
    FragmentManager fragmentManager;
    public List<Button> buttons;
    int buttonNum = 4;
    //generate random color algorithm parameter
    int randomColorGap,randomColorRange;
    int rightButton;
    public int choosedButton;
    //indicate which button's color is same with background
    int whichButton;
    
    public Timer timer;
    public TimerTask timeOutTask,timeBarTask,frogDisappearTask,rightChoiceTask,wrongChoiceTask;

    float actionBarValue;
    //indicate if the timeBar is moving
    public boolean isRun = true;

    //to get the selected button color
    public ColorDrawable frogColorDrawable;
    public int frogColor;

    public int timeOutNum = 0;
    int selectRightNum = 0;
    public static final int TIMEOUT = 0;
    public static final int TIMEBARMOVE = 1;
    public static final int FROGDISAPPEAR = 2;
    public static final int RIGHTCHOICE = 3;
    public static final int WRONGCHOICE = 4;

    private RoundCornerProgressBar timeBar;

    //to mark the time when pause button be click
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
                            timeBarTask.cancel();
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setIcon(R.drawable.frog);
                            dialog.setTitle("FUNK ONE MORE TIME");
                            dialog.setCancelable(false);
                            dialog.setNegativeButton("YEAH", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    animationView.initView(point.x);
                                    timeOutNum = 0;
                                    selectRightNum = 0;
                                    changeFragment(4);
                                    timeOutTask = new TimeOutTask();
                                    timeBarTask = new TimeBarMoveTask();
                                    timer.schedule(timeOutTask,0,5000);
                                    timer.schedule(timeBarTask,0,50);
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
                        selectRightNum++;
                        newRound(true);
                        break;
                    case WRONGCHOICE:
                        newRound(false);
                        break;

                }
            }
        };

        timeOutTask = new TimeOutTask();
        timeBarTask = new TimeBarMoveTask();
        fragmentManager = getFragmentManager();
        colors = new ArrayList<>();
        point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        animationView = new AnimationView(this,point.x);
        setContentView(R.layout.main);

        backgroundLayout = (LinearLayout)findViewById(R.id.animation_layout);
        backgroundLayout.addView(animationView);

        timeBar = (RoundCornerProgressBar)findViewById(R.id.time_bar);


        Button pauseButton = (Button)findViewById(R.id.pause_button);

        buttons = new ArrayList<>();

        changeFragment(4);

        randomColorGap = 30;
        randomColorRange = 8;

        timer.schedule(timeOutTask,0,5000);
        timer.schedule(timeBarTask,0,50);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionBarValue = timeBar.getProgress();
                long currentTime = System.currentTimeMillis();
                pauseRestTime =pauseRestTime + currentTime-lastExecuteTime;

                timeOutTask.cancel();
                timeBarTask.cancel();

                timeOutTask = new TimeOutTask();
                timeBarTask = new TimeBarMoveTask();

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
                            timer.schedule(timeBarTask,0,50);
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


    private void genRandomColor(int num){
        colors.clear();

        int red = (int)(30 + Math.random() * 190);
        int green = (int)(30 + Math.random() * 190);
        int blue = (int)(30 + Math.random() * 190);
        int baseColor = 0xff000000 | red << 16 | green << 8 | blue;
        colors.add(baseColor);

        int temporaryR,temporaryG,temporaryB,temporaryColor;

        for (int i = 1;i<num;i++){
                temporaryR = red + (int)(Math.random()*2*randomColorGap - randomColorGap);
                temporaryG = green + (int)(Math.random()*2*randomColorGap - randomColorGap);
                temporaryB = blue + (int)(Math.random()*2*randomColorGap - randomColorGap);
                temporaryColor = 0xff000000 | temporaryR << 16 | temporaryG << 8 | temporaryB;
//            temporaryR = red + (int)(Math.random()*(randomColorGap) + randomColorRange);
//            red = temporaryR;
//            temporaryG = green + (int)(Math.random()*(randomColorGap) + randomColorRange);
//            green = temporaryG;
//            temporaryB = blue + (int)(Math.random()*(randomColorGap) + randomColorRange);
//            blue = temporaryB;
//            temporaryColor = 0xff000000 | temporaryR << 16 | temporaryG << 8 | temporaryB;
            colors.add(temporaryColor);
        }
    }

    public void judge(){
        if (timeOutNum<5 && isRun) {
            timeBarTask.cancel();
            timeBarTask = new TimeBarMoveTask();
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

    private void changeUIColor(){
        genRandomColor(buttonNum);
        backgroundLayout.setBackgroundColor(colors.get(0));

        whichButton = (int)(Math.random() * colors.size());
        rightButton = whichButton;
        for (int i = 0;i<colors.size();i++){
            buttons.get(whichButton%(colors.size())).setBackgroundColor(colors.get(i));
            whichButton++;
        }
    }


    private void newRound(boolean isRight){
        timer.schedule(timeBarTask,0,50);
        lastExecuteTime = System.currentTimeMillis();
        isRun = true;
        if (isRight) {
            if (selectRightNum == 4){
                changeFragment(6);
            }
            if (selectRightNum == 8){
                changeFragment(9);
            }
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
//    public void setButtonListen(){
//        for (Button b: buttons){
//            b.setOnClickListener(this);
//        }
//    }

    private void changeFragment(int i){
        switch (i){
            case 4:
                FourButtonFragment fourButtonFragment = new FourButtonFragment();
                FragmentTransaction transaction4 = fragmentManager.beginTransaction();
                transaction4.replace(R.id.button_layout,fourButtonFragment);
                transaction4.commit();
                fragmentManager.executePendingTransactions();
                buttonNum = 4;
                break;
            case 6:
                SixButtonFragment sixButtonFragment = new SixButtonFragment();
                FragmentTransaction transaction6 = fragmentManager.beginTransaction();
                transaction6.replace(R.id.button_layout,sixButtonFragment);
                transaction6.commit();
                fragmentManager.executePendingTransactions();
                buttonNum = 6;
                randomColorGap = 20;
                randomColorRange = 6;
                break;
            case 9:
                NineButtonFragment nineButtonFragment = new NineButtonFragment();
                FragmentTransaction transaction9 = fragmentManager.beginTransaction();
                transaction9.replace(R.id.button_layout,nineButtonFragment);
                transaction9.commit();
                fragmentManager.executePendingTransactions();
                buttonNum = 9;
                randomColorGap = 10;
                randomColorRange = 2;
                break;
        }
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
