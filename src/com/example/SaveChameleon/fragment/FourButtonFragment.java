package com.example.SaveChameleon.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.SaveChameleon.R;

/**
 * Created by zhao on 2016/4/14.
 */
public class FourButtonFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.four_button,container,false);
        return view;
    }
}
