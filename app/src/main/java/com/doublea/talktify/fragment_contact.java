package com.doublea.talktify;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doublea.talktify.backgroundTools.CompletionListener;
import com.doublea.talktify.backgroundTools.FireBaseInteraction;
import com.doublea.talktify.backgroundTools.UserData;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_contact.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_contact#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_contact extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID = "userID";
    private static final String TAG = "dbg";
    // TODO: Rename and change types of parameters
    private String userID;
    private UserData userData;
    private TextView name;
    private TextView chat;
    private FireBaseInteraction fireBase;
    private OnFragmentInteractionListener mListener;
    public fragment_contact() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment fragment_contact.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_contact newInstance(String userID) {
        fragment_contact fragment = new fragment_contact();
        Bundle args = new Bundle();
        args.putString(ID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString(ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"1");
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        name = view.findViewById(R.id.tv_fragment_contact_name);
        chat = (TextView) view.findViewById(R.id.tv_initiate_chat) ;

        //Loads variables
        loadVariables();

        return view;
    }

    /**
     * @TODO fix contact read
     */
    private void loadVariables() {

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),activity_chat.class);
                Bundle b = new Bundle();
                b.putString("ID",userData.getUID());
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        fireBase = new FireBaseInteraction(userID, new CompletionListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                userData = fireBase.getCurrentUserData();
                name.setText(userData.getDisplayName());
            }

            @Override
            public void onFailure() {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
