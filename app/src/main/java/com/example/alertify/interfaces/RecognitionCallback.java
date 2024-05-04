package com.example.alertify.interfaces;

public interface RecognitionCallback {
    void onRecognitionComplete(String result);

    void onRecognitionFailure(String errorMessage);
}

