package com.demo.instaoff;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginWebViewFragment extends DialogFragment {


    public LoginWebViewFragment() {
        // Required empty public constructor
    }

    public static LoginWebViewFragment newInstance() {
        LoginWebViewFragment fragment = new LoginWebViewFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        //textView.setText(R.string.hello_blank_fragment);
        return textView;
    }

}
