package org.whatever.stuff;

import org.whatever.stuff2.*;

public class B {
    public <T extends C> void blah(T stuff) {
        System.out.println(stuff);
    }
}

