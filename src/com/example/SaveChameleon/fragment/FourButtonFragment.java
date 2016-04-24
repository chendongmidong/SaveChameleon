package com.example.SaveChameleon.fragment;

import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.SaveChameleon.MainActivity;
import com.example.SaveChameleon.R;

/**
 * Created by zhao on 2016/4/14.
 */
public class FourButtonFragment extends Fragment implements View.OnClickListener{
    MainActivity mainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.four_button,container,false);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Button button1 = (Button) getActivity().findViewById(R.id.button1);
        Button button2 = (Button) getActivity().findViewById(R.id.button2);
        Button button3 = (Button) getActivity().findViewById(R.id.button3);
        Button button4 = (Button) getActivity().findViewById(R.id.button4);

        mainActivity = (MainActivity) getActivity();

        mainActivity.buttons.clear();

        mainActivity.buttons.add(button1);
        mainActivity.buttons.add(button2);
        mainActivity.buttons.add(button3);
        mainActivity.buttons.add(button4);

        for (Button b:mainActivity.buttons){
            b.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                mainActivity.choosedButton = 0;
                mainActivity.frogColorDrawable = (ColorDrawable)mainActivity.buttons.get(0).getBackground();
                mainActivity.frogColor = mainActivity.frogColorDrawable.getColor();
                break;
            case R.id.button2:
                mainActivity.choosedButton = 1;
                mainActivity.frogColorDrawable = (ColorDrawable)mainActivity.buttons.get(1).getBackground();
                mainActivity.frogColor = mainActivity.frogColorDrawable.getColor();
                break;
            case R.id.button3:
                mainActivity.choosedButton = 2;
                mainActivity.frogColorDrawable = (ColorDrawable)mainActivity.buttons.get(2).getBackground();
                mainActivity.frogColor = mainActivity.frogColorDrawable.getColor();
                break;
            case R.id.button4:
                mainActivity.choosedButton = 3;
                mainActivity.frogColorDrawable = (ColorDrawable)mainActivity.buttons.get(3).getBackground();
                mainActivity.frogColor = mainActivity.frogColorDrawable.getColor();
                break;
        }

        mainActivity.judge();
    }
}
