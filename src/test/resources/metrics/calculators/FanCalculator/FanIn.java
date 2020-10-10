package org.whatever.stuff;

class ClassA {

    public void printSquare(ClassB b) {
        System.out.println(b.getNumber() * b.getNumber());
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

    public int getDoubleNumber() {
        return getNumber() + this.getNumber();
    }
}
