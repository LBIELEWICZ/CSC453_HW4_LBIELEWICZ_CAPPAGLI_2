
import java.util.*;
import java.lang.String;

public class MoreJava { 
	private EvalParser eval;

	public MoreJava() {
		eval = new EvalParser();
	}

	public String getThreeAddr(String eval){
		String str = "";
		EvalParser parser = new EvalParser();
		LinkedList<TACObject> list = parser.getThreeAddr(eval);
		while(list.size > 0){
			str = str + list.pop.toString();
		}
	return str;
	} 

	public static void main(String[] args){
		MoreJava parser = new MoreJava();
	}
}
