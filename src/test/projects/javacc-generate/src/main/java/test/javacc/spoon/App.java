package test.javacc.spoon;

import generate.javacc.spoon.Example;
/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        Example example = new Example(System.in);
        System.out.println("Hello World!");
    }
}
