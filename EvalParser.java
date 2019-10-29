import java.util.*;
import java.lang.String;
import java.util.LinkedList; 

public class EvalParser {
  Scanner scan = new Scanner();

  int tempID = 0;
  int tlabelID = 0; // Label id for true
  int flabelID = 0; // Label id for false
  int rlabelID = 0; // Label id for loops

  Token.TokenType last;

  /***************** Three Address Translator ***********************/
  // TODO #2 Continued: Write the functions for E/E', T/T', and F. Return the temporary ID associated with each subexpression and
  //                    build the threeAddressResult string with your three address translation 
  /****************************************/
  public ASTNode threeAddrProg(LinkedList<Token> tokens) {
    ASTNode op = new ASTNode(ASTNode.NodeType.PROG); // Match program type
    if (tokens.peek() != null && (tokens.peek().tokenType == Token.TokenType.PUBLIC || tokens.peek().tokenType == Token.TokenType.PRIVATE)){
      tokens.remove();
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CLASS){
        tokens.remove();
      }
      else {
        // Invalid program type
        System.out.println("ERROR: Invalid program type");
        System.exit(1);
      }
    }
    else {
      // Invalid program type
      System.out.println("ERROR: Invalid program type");
      System.exit(1);
    }
    ASTNode left = threeAddrId(tokens); // Match ID of program
    ASTNode currNode = left;
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OB){
      tokens.remove();
      op.setLeft(left);
      ASTNode right = threeAddrProgLst(tokens); // Match declarations and functions in program
      op.setRight(right);
      currNode = op;
      left = currNode;
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CB){
        tokens.remove();
      }//
      else {
        // Check brackets
        System.out.println("ERROR1: Check brackets");
        System.exit(1);
      }
    }
    return currNode;//
  }

  public ASTNode threeAddrProgLst(LinkedList<Token> tokens) {
    //ASTNode left = threeAddrStmt(tokens);
    ASTNode currNode = null;
    while(true) {
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.INT){
        ASTNode left = threeAddrVarDecl(tokens);
        ASTNode list = new ASTNode(ASTNode.NodeType.LIST);
        list.setLeft(left);
        //todo
        if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.END){
          tokens.remove();
        }
        else {
          // Invalid program list
          System.out.println("ERROR: Invalid program list");
          System.exit(1);
        }
        ASTNode right = currNode;
        list.setRight(right);
        currNode = list;
      }
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.VOID){
        ASTNode left = threeAddrFunc(tokens);
        ASTNode list = new ASTNode(ASTNode.NodeType.LIST);
        list.setLeft(left);
        ASTNode right = currNode;
        list.setRight(right);
        currNode = list;
      }
      else {
        break;
      }
    }
    return currNode;
  }

  public ASTNode threeAddrFunc(LinkedList<Token> tokens) {
    ASTNode op = new ASTNode(ASTNode.NodeType.PROG); // Match program type
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.VOID){
      tokens.remove();
    }
    else {
      // Invalid program type
      System.out.println("ERROR: Invalid program type");
      System.exit(1);
    }
    ASTNode left = threeAddrId(tokens); // Match ID of program
    ASTNode currNode = left;
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OP){
      tokens.remove();
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CP){
        tokens.remove();
        if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OB){
          tokens.remove();
          op.setLeft(left);
          ASTNode right = threeAddrStmtLst(tokens); // Match statements in program
          op.setRight(right);
          currNode = op;
          left = currNode;
          if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CB){
            tokens.remove();
          }
          else {
            // Check brackets
            System.out.println("ERROR1: Check brackets");
            System.exit(1);
          }
        }
        else {
          // Check brackets
          System.out.println("ERROR2: Check brackets");
          System.exit(1);
        }
      }
      else {
        // Check brackets
        System.out.println("ERROR3: Check brackets");
        System.exit(1);
      }
    }
    else {
      // Check brackets
      System.out.println("ERROR4: Check brackets");
      System.exit(1);
    }
    return currNode;
  }

  public ASTNode threeAddrVarDecl(LinkedList<Token> tokens) {
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.INT){
      tokens.remove();
    }
    else {
      // Invalid declaration type
      System.out.println("ERROR: Invalid declaration type");
      System.exit(1);
    }
    ASTNode currNode = threeAddrId(tokens);
    
    return currNode;
  }

  public ASTNode threeAddrStmtLst(LinkedList<Token> tokens) {
    ASTNode root = null; //left;
    ASTNode prev = null;
    while(true) {
      if (tokens.peek() != null && tokens.peek().tokenType != Token.TokenType.CB){
        ASTNode left = threeAddrStmt(tokens);
        ASTNode list = new ASTNode(ASTNode.NodeType.LIST);
	if (root == null)
	  root = list;
        if (prev != null)
          prev.setRight(list);
        list.setLeft(left);
        prev = list;
      }
      else {
        break;
      }
    }
    return root;
  }

  public ASTNode threeAddrStmt(LinkedList<Token> tokens) {
    ASTNode currNode = null;

    if (tokens.peek() != null && (tokens.peek().tokenType == Token.TokenType.IF || 
                                       tokens.peek().tokenType == Token.TokenType.WHILE)){ // Match control flow
      currNode = threeAddrCf(tokens);
      this.tempID = 0;
    }
    else if (tokens.size() > 2 && tokens.get(2).tokenType == Token.TokenType.END){ // Match declaration
      currNode = threeAddrVarDecl(tokens);
      tokens.remove(); // Remove semicolon
      this.tempID = 0;
    }
    else if (tokens.peek() != null && (tokens.peek().tokenType == Token.TokenType.INT || tokens.peek().tokenType == Token.TokenType.ID)){ // Match assignment type
      currNode = threeAddrAssignment(tokens);
      this.tempID = 0;
    }
    else {
      // Invalid statment
      System.out.println("ERROR: Invalid statement");
      System.exit(1);
    }
    return currNode;
  }

  public ASTNode threeAddrCf(LinkedList<Token> tokens) {
    ASTNode currNode = null;
    ASTNode cf = null;
    boolean whileFlag = false;    

    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.IF){
      cf = new ASTNode(ASTNode.NodeType.IF);
      tokens.remove();
    }
    else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.WHILE){
      cf = new ASTNode(ASTNode.NodeType.WHILE);
      whileFlag = true;
      tokens.remove();
    }
    else {
      // Invalid control flow
      System.out.println("ERROR: Invalid control flow");
      System.exit(1);
    }
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OP) {
      tokens.remove();
      
      cf.setLeft(threeAddrC(tokens));
      this.tempID = 0;
      cf.setVal("" + last);
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CP) {
        
        tokens.remove();
        if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OB) {
          if (whileFlag) {
            cf.setRID(this.rlabelID);
            this.rlabelID++;
          }

          tokens.remove();
          cf.setRight(threeAddrStmtLst(tokens));
          
          cf.setTID(cf.getLeft().getTID());
          cf.setFID(cf.getLeft().getFID());
           
          currNode = cf;
          if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CB) {
            tokens.remove();
          }
          else {
            // Invalid control flow
            System.out.println("ERROR: Invalid control flow");
            System.exit(1);
          }
        }
        else {
          // Invalid control flow
          System.out.println("ERROR: Invalid control flow");
          System.exit(1);
        }
      }
      else {
        // Invalid control flow
        System.out.println("ERROR: Invalid control flow");
        System.exit(1);
      }
    }
    else {
      // Invalid control flow
      System.out.println("ERROR: Invalid control flow");
      System.exit(1);
    }
    return currNode;
  }

  public ASTNode threeAddrAssignment(LinkedList<Token> tokens) {
    ASTNode op = new ASTNode(ASTNode.NodeType.ASSG);
    ASTNode left;
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.INT){
      left = threeAddrVarDecl(tokens);
    }
    else {
      left = threeAddrId(tokens); // Left tempID for operation three address generation
    }
    ASTNode currNode = left; 
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.ASSG){
      op.setVal("=");
      op.setLeft(left);
      tokens.remove();
      ASTNode right = threeAddrC(tokens);
      op.setRight(right);
      currNode = op;
      left = currNode;
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.END){
        tokens.remove();
      }
      else {
        // Invalid assignment
        System.out.println("ERROR: Invalid assignment");
        System.exit(1);
      }
    }
    return currNode;
  }
  
  public ASTNode threeAddrC(LinkedList<Token> tokens) {
    ASTNode left = threeAddrS(tokens);
    ASTNode currNode = left; 
    while (true) {
      // Handle comparison operators
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.AND){
        last = Token.TokenType.AND;
        ASTNode op = new ASTNode(ASTNode.NodeType.AND);
        op.setVal("&&");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrS(tokens);
        op.setRight(right);

        op.getRight().setFID(op.getLeft().getFID());
        op.getRight().setTID(op.getLeft().getTID());
        op.getLeft().setTID(op.getRight().getTID()+1);
        op.setTID(op.getRight().getTID());
        op.setFID(op.getRight().getFID());
        
        this.flabelID--;

        currNode = op;
        left = currNode;
      }
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OR) {
        last = Token.TokenType.OR;
        ASTNode op = new ASTNode(ASTNode.NodeType.OR);
        op.setVal("||");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrS(tokens);
        op.setRight(right);

        op.getRight().setTID(op.getLeft().getTID());
        op.getRight().setFID(op.getLeft().getFID());
        op.getLeft().setFID(op.getRight().getFID()+1);
        op.setTID(op.getRight().getTID());
        op.setFID(op.getRight().getFID());
 
        this.tlabelID--;

        currNode = op;
        left = currNode;
      }
      else
        break;
    }
    
    return currNode;
  }
  
  public ASTNode threeAddrS(LinkedList<Token> tokens) {
    ASTNode left = threeAddrG(tokens); // Left tempID for operation three address generation
    ASTNode currNode = left; 
    while (true) {
      // Handle equality operations
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.EQ){
        last = Token.TokenType.EQ;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal("==");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrG(tokens);
        op.setRight(right);
        op.setTID(this.tlabelID);
	op.setFID(this.flabelID);
        // Used to keep the original value intact for returns
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
      }
      // Handle inequality operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.NEQ) {
        last = Token.TokenType.NEQ;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal("!=");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrG(tokens);
        op.setRight(right);
        op.setTID(this.tlabelID);
	op.setFID(this.flabelID);
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
      }
      else {
        break;
      }
    }    

    return currNode;
  }

  public ASTNode threeAddrG(LinkedList<Token> tokens) {
    ASTNode left = threeAddrE(tokens); // Left tempID for operation three address generation
    ASTNode currNode = left; 
    while (true) {
      // Handle less than operations
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.LT){
        last = Token.TokenType.LT;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal("<");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrE(tokens);
        op.setRight(right);
        op.setTID(this.tlabelID);
	op.setFID(this.flabelID);
        // Used to keep the original value intact for returns
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
      }
      // Handle greater than operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.GT){
        last = Token.TokenType.GT;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal(">");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrE(tokens);
        op.setRight(right);
        op.setTID(this.tlabelID);
	op.setFID(this.flabelID);
        // Used to keep the original value intact for returns
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
      }
      // Handle less than or equal to operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.LTE){
        last = Token.TokenType.LTE;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal("<=");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrE(tokens);
        op.setRight(right);
        op.setTID(this.tlabelID);
	op.setFID(this.flabelID);
        // Used to keep the original value intact for returns
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
      }
      // Handle greater than or equal to operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.GTE) {
        last = Token.TokenType.GTE;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal(">=");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrE(tokens);
        op.setRight(right);
        op.setTID(this.tlabelID);
	op.setFID(this.flabelID);
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
      }
      else {
        break;
      }
    }
    return currNode;
  }

  public ASTNode threeAddrE(LinkedList<Token> tokens) {
    ASTNode left = threeAddrT(tokens); // Left tempID for operation three address generation
    ASTNode currNode = left; 
    while (true) {
      // Handle addition operations
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.PLUS){
        ASTNode op = new ASTNode(ASTNode.NodeType.OP);
        op.setVal("+");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrT(tokens);
        op.setRight(right);
        op.setID(tempID);
        // Used to keep the original value intact for returns
        tempID++;
        currNode = op;
        left = currNode;
      }
      // Handle subtraction operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.MINUS) {
        ASTNode op = new ASTNode(ASTNode.NodeType.OP);
        op.setVal("-");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrT(tokens);
        op.setRight(right);
        op.setID(tempID);
        tempID++;
        currNode = op;
        left = currNode;
      }
      else {
        break;
      }
    }
    return currNode;
  }

  public ASTNode threeAddrT(LinkedList<Token> tokens) {
    ASTNode left = threeAddrF(tokens);
    ASTNode currNode = left;
    while (true) {
      // Handle multiplication operations
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.MUL) {
        ASTNode op = new ASTNode(ASTNode.NodeType.OP);
        op.setVal("*");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrF(tokens);
        op.setRight(right);
        op.setID(tempID);
        tempID++;
        currNode = op;
        left = currNode;
      }
      // Handle division operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.DIV) {
        ASTNode op = new ASTNode(ASTNode.NodeType.OP);
        op.setVal("/");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrF(tokens);
        op.setRight(right);
        op.setID(tempID);
        tempID++;
        currNode = op;
        left = currNode;
      }
      else {
        break;
      }
    }
    return currNode;
  }

  public ASTNode threeAddrF(LinkedList<Token> tokens) {
    ASTNode currNode = null;
    // Handle recursion into expressions contained in parentheses
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OP) {
      tokens.remove();
      currNode = threeAddrE(tokens);
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CP) {
        tokens.remove();
      }
      else {
        // Handle invalid sequences of tokens (i.e. 1++1)
        System.out.println("ERROR: Expression not supported by grammar");
        System.exit(1);
      }
    }
    // Handle numbers
    else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.NUM) {
      currNode = new ASTNode(ASTNode.NodeType.NUM);
      currNode.setVal("" + tokens.peek().tokenVal);
      currNode.setID(tempID);
      
      this.tempID++;
      tokens.remove();
    }
    else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.ID) {
      currNode = new ASTNode(ASTNode.NodeType.ID);
      currNode.setVal(tokens.peek().tokenVal);
      currNode.setID(tempID);
 
      tokens.remove();
    }
    else {
      // If a factor has reached this point it is not an operation supported by this parser
      System.out.println("ERROR: Expression not supported by grammar");
      System.exit(1);
    } 
    
    return currNode;
  }

  public ASTNode threeAddrId(LinkedList<Token> tokens) {
    ASTNode id = new ASTNode(ASTNode.NodeType.ID);
    
    // Create a node that holds the name of the ID
    if (tokens.peek().tokenType == Token.TokenType.ID) {
      id.setVal(tokens.peek().tokenVal);
      tokens.remove();
    }
    else {
      System.out.println("ERROR: Invalid id");
      System.exit(1);
    }
    return id;
  }

  /***************** Simple Expression Evaluator ***********************/
  // TODO #1 Continued: Write the functions for E/E', T/T', and F. Return the expression's value
  /****************************************/

  /* TODO #1: Write a parser that can evaluate expressions */
  public int evaluateExpression(String eval){
    LinkedList<Token> tokens = scan.extractTokenList(eval);
    int result = evaluateE(tokens);
    return result;
  }

  // Evaluating E/E'
  private int evaluateE(LinkedList<Token> tokens){
    int r;
    r = evaluateT(tokens);
    while(true){ // E'
      if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.PLUS){
        tokens.remove(); //match('+');
        r = r + evaluateT(tokens);
      }else if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.MINUS){
        tokens.remove(); //match('-');
        r = r - evaluateT(tokens);
      }else{
        break;
      }
    }
    return r;
  }

  // Evaluating T/T'
  private int evaluateT(LinkedList<Token> tokens){
    int r;
    r = evaluateF(tokens);
    while(true){ // T'
      if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.MUL){
        tokens.remove(); //match('*');
        r = r * evaluateF(tokens);
      }else if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.DIV){
        tokens.remove(); //match('/');
        r = r / evaluateF(tokens);
      }else{
        break;
      }
    }
    return r;
  }

  // Evaluating F
  private int evaluateF(LinkedList<Token> tokens){
    int r = 0;
    if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OP){
      tokens.remove(); //match('(');
      if (tokens.peek().tokenType == Token.TokenType.CP) {
        System.out.println("ERROR: Not in the grammar.");
        System.exit(1);
      }
      r = evaluateE(tokens);
      if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CP){
        tokens.remove(); //match(')');
      }else{
        System.out.println("ERROR: Not in the grammer.");
        System.exit(1);
      }
    }else if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.NUM){
      r = Integer.parseInt(tokens.remove().tokenVal); //match(number);
    }else{
       System.out.println("ERROR: Not in the grammer.");
       System.exit(1);
    }
    return r;
  }

  /* TODO #2: Now add three address translation to your parser*/
  public LinkedList<TACObject> getThreeAddr(String eval){
    this.tempID = 0;
    
    LinkedList<Token> tokens = scan.extractTokenList(eval);
 
    LinkedList<TACObject> tacs = new LinkedList<TACObject>();
    tacs = postorder(threeAddrProg(tokens), tacs, false);
    
    this.tlabelID = 0;
    this.flabelID = 0;
    this.tempID = 0;
    this.rlabelID = 0;
    

    return tacs;
  }
  
  private TACObject threeAddrRELOP(TACObject.OpType op, ASTNode node, int labelID) {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    
    if (node.getLeft().getType() == ASTNode.NodeType.OP || node.getLeft().getType() == ASTNode.NodeType.NUM)
      str1 = "temp" + node.getLeft().getID();
    else if (node.getLeft().getType() == ASTNode.NodeType.ID)
      str1 = node.getLeft().getVal();
    else {
      System.out.println("ERROR: Type error in RELOP");
      System.exit(1);
    }
    
    if (node.getRight().getType() == ASTNode.NodeType.OP || node.getRight().getType() == ASTNode.NodeType.NUM)
      str2 = "temp" + node.getRight().getID();
    else if (node.getRight().getType() == ASTNode.NodeType.ID)
      str2 = node.getRight().getVal();
    else {
      System.out.println("ERROR: Type error in RELOP");
      System.exit(1);
    }

    str3 = "trueLabel" + labelID;
    TACObject ret = new TACObject(op, str1, str2, str3);
    return ret;
  }

  private LinkedList<TACObject> postorder(ASTNode root, LinkedList<TACObject> tacs, boolean orFlag) {

    if (root == null) {
      return tacs;
    }

    String str = "";
    TACObject obj;

    if (root.getType() == ASTNode.NodeType.WHILE) {
      str += "repeatLabel" + root.getRID();
      obj = new TACObject(TACObject.OpType.LABLE, str, null, null);
      tacs.add(obj);
    }

    if (root.getType() == ASTNode.NodeType.OR) {
      orFlag = true;
    } 

    tacs = postorder(root.getLeft(), tacs, orFlag); 
    tacs = postorder(root.getRight(), tacs, false);
    
    if (root.getType() == ASTNode.NodeType.RELOP) {
      if (root.getVal().equals("<")) {
        obj = threeAddrRELOP(TACObject.OpType.IF_LT, root, root.getTID());
        tacs.add(obj);
      }
      else if (root.getVal().equals(">")) {
        obj = threeAddrRELOP(TACObject.OpType.IF_GT, root, root.getTID());
        tacs.add(obj);
      }
      else if (root.getVal().equals("<=")) {
        obj = threeAddrRELOP(TACObject.OpType.IF_LTE, root, root.getTID());
        tacs.add(obj);
      }
      else if (root.getVal().equals(">=")) {
        obj = threeAddrRELOP(TACObject.OpType.IF_GTE, root, root.getTID());
        tacs.add(obj);
      }
      else if (root.getVal().equals("==")) {
        obj = threeAddrRELOP(TACObject.OpType.IF_EQ, root, root.getTID());
        tacs.add(obj);
      }
      else if (root.getVal().equals("!=")) {
        obj = threeAddrRELOP(TACObject.OpType.IF_NE, root, root.getTID());
        tacs.add(obj);
      }

      if (orFlag) {
        str = "falseLabel" + root.getFID();
        obj = new TACObject(TACObject.OpType.GOTO, null, null, str);
        tacs.add(obj);
        str = "falseLabel" + root.getFID();
        obj = new TACObject(TACObject.OpType.LABLE, str, null, null);
        tacs.add(obj);
      }
      else {
        str = "falseLabel" + root.getFID();
        obj = new TACObject(TACObject.OpType.GOTO, null, null, str);
        tacs.add(obj);
        str = "trueLabel" + root.getTID();
        obj = new TACObject(TACObject.OpType.LABLE, str, null, null);
        tacs.add(obj);
      }
    }

    if (root.getType() == ASTNode.NodeType.OR) {
      orFlag = true;
    } 

    String str1;
    String str2;
    if (root.getType() == ASTNode.NodeType.OP) {
      str = "temp" + root.getID();
      if (root.getLeft().getType() == ASTNode.NodeType.ID)
        str1 = root.getLeft().getVal();
      else
        str1 = "temp" + root.getLeft().getID();

      TACObject.OpType ot = null;
      if (root.getVal() == "+") {
        ot = TACObject.OpType.PLUS;
      }
      else if (root.getVal() == "-") {
        ot = TACObject.OpType.MINUS;
      }
      else if (root.getVal() == "*") {
        ot = TACObject.OpType.MUL;
      }
      else if (root.getVal() == "/") {
        ot = TACObject.OpType.DIV;
      }

      if (root.getRight().getType() == ASTNode.NodeType.ID)
        str2 = root.getRight().getVal();
      else
        str2 = "temp" + root.getRight().getID();
      obj = new TACObject(ot, str1, str2, str);
      tacs.add(obj);
    }
    else if (root.getType() == ASTNode.NodeType.NUM) {
      str = "temp" + root.getID();
      str1 =  root.getVal();
      obj = new TACObject(TACObject.OpType.ASSIGN, str1, null, str);
      tacs.add(obj);
    }
    else if (root.getType() == ASTNode.NodeType.ASSG) {
      if (root.getRight().getType() == ASTNode.NodeType.ID){
        str = root.getLeft().getVal();
        str1 = root.getRight().getVal();
      }
      else{
        str = root.getLeft().getVal();
        str1 = "temp" + root.getRight().getID();
      }
      obj = new TACObject(TACObject.OpType.ASSIGN, str1, null, str);
      tacs.add(obj);
    }
    else if (root.getType() == ASTNode.NodeType.IF) {
      str = "falseLabel" + root.getFID();
      obj = new TACObject(TACObject.OpType.LABLE, str, null, null);
      tacs.add(obj);
    }
    else if (root.getType() == ASTNode.NodeType.WHILE) {
      str = "repeatLabel" + root.getRID();
      obj = new TACObject(TACObject.OpType.GOTO, null, null, str);
      tacs.add(obj);
      str = "falseLabel" + root.getFID();
      obj = new TACObject(TACObject.OpType.LABLE, str, null, null);
      tacs.add(obj);
    }
    return tacs;
  }

}
