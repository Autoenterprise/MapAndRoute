package com.velychko.kyrylo.mapandroute.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.velychko.kyrylo.mapandroute.R;

public class InputAddressFragment extends Fragment {



    public InputAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_address, container, false);
        return view;
    }

}
