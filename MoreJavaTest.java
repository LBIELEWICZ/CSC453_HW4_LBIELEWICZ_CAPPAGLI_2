import java.io.*;
import java.util.*;

public class MoreJavaTest{

  public static void TestThreeAddrGen(){
    System.out.println("*******************************************");
    System.out.println("Testing Three Address Generation");

    String eval = "public class test {}";
    MoreJava parser = new MoreJava();
    String result = "";
    assert(parser.getThreeAddr(eval).equals(result));

    parser = new MoreJava();
    eval = "public class test {int x; int y; void mainEntry()  {}   void blarg(){} }";
    result = "";
    assert(parser.getThreeAddr(eval).equals(result));

    parser = new MoreJava();
    eval = "public class test {int x; int y; void mainEntry(){ int x; x = 3; if(2 < 3 && 5 < 4){ x = 42; }}}";
    result = "temp0 = 3\n"+
             "x = temp0\n"+
             "temp0 = 2\n"+
             "temp1 = 3\n"+
             "IF_LT: temp0, temp1, trueLabel1\n"+
             "GOTO: falseLabel0\n"+
             "trueLabel1\n"+
             "temp2 = 5\n"+
             "temp3 = 4\n"+
             "IF_LT: temp2, temp3, trueLabel0\n"+
             "GOTO: falseLabel0\n"+
             "trueLabel0\n"+
             "temp0 = 42\n"+
             "x = temp0\n"+
             "falseLabel0\n";
    assert(parser.getThreeAddr(eval).equals(result));

    System.out.println("Congrats: three address generation tests passed! Now make your own test cases "+
                       "(this is only a subset of what we will test your code on)");
    System.out.println("*******************************************");
  }

  public static void main(String[] args){
    TestThreeAddrGen();
  }

}
