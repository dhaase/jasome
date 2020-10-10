package org.whatever.stuff;

class ClassA {

    public ClassB getB() {
        return new ClassB();
    }

}

class ClassB {
    public ClassC getC() {
        return new ClassC();
    }
}

class ClassC {
    public void print() {
        System.out.println("Hello");
    }
}

class MainClass {
    private ClassA classA;

    public MainClass(ClassA classA) {
        this.classA = classA;
    }

    public void doPrint() {
        classA.getB().getC().print();
    }
}
