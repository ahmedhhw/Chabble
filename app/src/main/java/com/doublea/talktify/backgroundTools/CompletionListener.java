package com.doublea.talktify.backgroundTools;

/**
 * Interface used to listen for events and wait for them to complete
 */
public interface CompletionListener {
    public void onStart(); // When task starts
    public void onSuccess(); // When task succeeds
    public void onFailure(); // When task fails
}
