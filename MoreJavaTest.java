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
    System.out.println("Testing More Three Address Generation");

    parser = new MoreJava();
    eval = "public class conj { void main() { if (2 > 3 && 1 < 4 && 5 <= 5){ int x = 4; }}}";
    result = "temp0 = 2\n"+
             "temp1 = 3\n"+
             "IF_GT: temp0, temp1, trueLabel2\n"+
             "GOTO: falseLabel0\n"+
             "trueLabel2\n"+
             "temp2 = 1\n"+
             "temp3 = 4\n"+
             "IF_LT: temp2, temp3, trueLabel1\n"+
             "GOTO: falseLabel0\n"+
             "trueLabel1\n"+
             "temp4 = 5\n"+
             "temp5 = 5\n"+
             "IF_LTE: temp4, temp5, trueLabel0\n"+
             "GOTO: falseLabel0\n"+
             "trueLabel0\n"+
             "temp0 = 4\n"+
             "x = temp0\n"+
             "falseLabel0\n";
    assert(parser.getThreeAddr(eval).equals(result));

    parser = new MoreJava();
    eval = "public class conj { void main() { if (2 > 3 || 1 < 4 || 5 <= 5){ int x = 4; }} }";
    result = "temp0 = 2\n"+
             "temp1 = 3\n"+
             "IF_GT: temp0, temp1, trueLabel0\n"+
             "GOTO: falseLabel2\n"+
             "falseLabel2\n"+
             "temp2 = 1\n"+
             "temp3 = 4\n"+
             "IF_LT: temp2, temp3, trueLabel0\n"+
             "GOTO: falseLabel1\n"+
             "falseLabel1\n"+
             "temp4 = 5\n"+
             "temp5 = 5\n"+
             "IF_LTE: temp4, temp5, trueLabel0\n"+
             "GOTO: falseLabel0\n"+
             "trueLabel0\n"+
             "temp0 = 4\n"+
             "x = temp0\n"+
             "falseLabel0\n";
    System.out.println(parser.getThreeAddr(eval));
    System.out.println(result);
    assert(parser.getThreeAddr(eval).equals(result));

    System.out.println("ALL TESTS PASSED");
  }

  public static void main(String[] args){
    TestThreeAddrGen();
  }

}
