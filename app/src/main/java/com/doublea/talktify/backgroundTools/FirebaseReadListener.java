package com.doublea.talktify.backgroundTools;

import com.google.firebase.database.DataSnapshot;

public interface FirebaseReadListener {
    public void onSuccess(DataSnapshot data);
    public void onFailure();
}
