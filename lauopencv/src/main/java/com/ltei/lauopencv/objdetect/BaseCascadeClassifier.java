//
// This file is auto-generated. Please don't modify it!
//
package com.ltei.lauopencv.objdetect;

import com.ltei.lauopencv.core.Algorithm;

// C++: class BaseCascadeClassifier

public class BaseCascadeClassifier extends Algorithm {

    protected BaseCascadeClassifier(long addr) { super(addr); }

    // internal usage only
    public static BaseCascadeClassifier __fromPtr__(long addr) { return new BaseCascadeClassifier(addr); }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // native support for java finalize()
    private static native void delete(long nativeObj);

}
