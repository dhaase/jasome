package org.whatever.stuff;

interface I {

}

interface J extends I {

}

interface K extends J {

}

class A {

}

class ClassX extends A implements K {

}

class ClassY extends A {

}
