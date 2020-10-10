package org.whatever.stuff2;

import org.whatever.stuff.*;

abstract public class D {

    static {
        System.out.println("Some stuff "+new CustomException());
    }

    public void usesB(B in) throws CustomException {
        System.out.println(in);
    }

    public static int utilMethod(int i) {
        return i*2;
    }
}
