package org.whatever.stuff;


class Outer {

    public static class Inner {
        public void sayHello() {
            System.out.println("hi!");
        }
    }
}


class ClassX extends Outer.Inner {

}

