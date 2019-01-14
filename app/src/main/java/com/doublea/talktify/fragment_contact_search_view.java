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
import com.doublea.talktify.backgroundTools.ContactAddListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_contact_search_view.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_contact_search_view#newInstance} factory method to
 * create an instance of this fragment.
 * Contact view designed for search view
 */
public class fragment_contact_search_view extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID = "userID";
    private static final String DISPNAME = "displayName";
    private static final String TAG = "dbg";
    // TODO: Rename and change types of parameters
    private UserData userData;
    private TextView name;
    private TextView add;
    private TextView chat;
    private FireBaseInteraction fireBase;
    private static ContactAddListener contactAddListener;
    private fragment_contact_search_view.OnFragmentInteractionListener mListener;
    public fragment_contact_search_view() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment fragment_contact.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_contact_search_view newInstance(UserData newUserData, ContactAddListener addListener) {
        fragment_contact_search_view fragment = new fragment_contact_search_view();
        contactAddListener = addListener;
        Bundle args = new Bundle();
        args.putString(ID, newUserData.getUID());
        args.putString(DISPNAME, newUserData.getDisplayName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d("dbg","1");
        userData = new UserData();
        if (getArguments() != null) {
            userData.setUID(getArguments().getString(ID));
            userData.setDisplayName(getArguments().getString(DISPNAME));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_search_view, container, false);
        add = (TextView) view.findViewById(R.id.tv_add_contact);
        chat = (TextView) view.findViewById(R.id.tv_initiate_chat) ;

        //Loads variables
        loadVariables();

        name = view.findViewById(R.id.tv_fragment_search_contact_name);
        name.setText(userData.getDisplayName());
        return view;
    }

    /**
     *
     */
    public void addToContacts(){
        contactAddListener.onSuccess(userData.getUID());
    }
    /**
     *
     */
    private void loadVariables() {
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addToContacts();
            }
        });
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
        fireBase = new FireBaseInteraction(userData.getUID(), new CompletionListener() {
            @Override
            public void onStart() {
                Log.d("TAG","Started");
            }

            @Override
            public void onSuccess() {
                Log.d("TAG","succeeded");
                userData = fireBase.getCurrentUserData();
            }

            @Override
            public void onFailure() {
                Log.d("TAG","failed");
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
        if (context instanceof fragment_contact_search_view.OnFragmentInteractionListener) {
            mListener = (fragment_contact_search_view.OnFragmentInteractionListener) context;
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
