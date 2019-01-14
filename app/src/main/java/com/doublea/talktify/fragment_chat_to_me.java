package com.doublea.talktify;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


public class fragment_chat_to_me extends Fragment {

    private String message;
    private TextView tvMessage;
    public fragment_chat_to_me() {
        // Required empty public constructor
    }

    public static fragment_chat_to_me newInstance(String msg) {
        fragment_chat_to_me fragment = new fragment_chat_to_me();
        Bundle args = new Bundle();
        args.putString("msg", msg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString("msg");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_to_me, container, false);
        tvMessage = view.findViewById(R.id.tv_chat_to_me_message);
        tvMessage.setText(message);
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
