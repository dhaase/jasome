package org.whatever.stuff;

class ClassA {

    public void printDouble(ClassB b) {
        System.out.println(b.getNumber() * getFactor());
    }

    public int getFactor() {
        return 2;
    }

}

class ClassB {
    private int myNumber;

    public ClassB(int myNumber) {
        this.myNumber = myNumber;
    }

    public int getNumber() {
        return myNumber;
    }
}
