package com.ltei.lauaudioconverter.callback;

public interface ILoadCallback {
    
    void onSuccess();
    
    void onFailure(Exception error);
    
}