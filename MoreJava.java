public class MoreJava {
	
	private EvalParser eval;
	
	public MoreJava() {
		eval = new EvalParser();
	}

	public String getThreeAddr(String str) {
		return eval.getThreeAddr(str);
	}
}
