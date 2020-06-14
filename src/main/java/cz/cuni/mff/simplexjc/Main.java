package cz.cuni.mff.simplexjc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        ClassGenerator.generateClass(new FileInputStream("D:\\micha\\MFF_UK\\NPRG021\\simplexjc\\src\\test\\resources\\simple.xml"));
    }
}
