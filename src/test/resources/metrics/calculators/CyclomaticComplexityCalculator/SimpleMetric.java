package org.whatever.stuff;

class ClassA {

    public void method(int x) {
        if (x < 10) {
            if (x > 3) {
                System.out.println("between 3 and 10");
            }

            if (x > 5) {
                System.out.println("between 5 and 10");
            } else {
                System.out.println("less than 5");
            }
        }

        if (x > 100) {
            if (x < 50) {
                System.out.println("between 50 and 100");
            }
        }
    }
}