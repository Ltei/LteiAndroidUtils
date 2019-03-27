package com.ltei.lauaudioconverter.callback;

import java.io.File;

public interface IConvertCallback {
    
    void onSuccess(File convertedFile);
    
    void onFailure(Exception error);

}