import java.util.*;
import java.lang.String;

public class SimpleJava { 

  public String getThreeAddr(String eval){
    EvalParser parser = new EvalParser();
    
    return parser.getThreeAddr(eval);
  } 

  public static void main(String[] args){
    SimpleJava parser = new SimpleJava();
  }

}
