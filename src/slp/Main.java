package slp;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashSet;

import slp.Slp.Exp;
import slp.Slp.Exp.Eseq;
import slp.Slp.Exp.Id;
import slp.Slp.Exp.Num;
import slp.Slp.Exp.Op;
import slp.Slp.ExpList;
import slp.Slp.Stm;
import util.Bug;
import util.Todo;
import control.Control;



public class Main
{
  // ///////////////////////////////////////////
  // maximum number of args

  private int maxArgsExp(Exp.T exp)
  {
    if (exp instanceof Exp.Op) {
    	Exp.Op op = (Exp.Op) exp;
    	int n1 = maxArgsExp(op.left);
    	int n2 = maxArgsExp(op.right);
    	
    	return n1 >= n2 ? n1:n2;
    }
    else if(exp instanceof Exp.Eseq) {
    	Exp.Eseq eseq = (Exp.Eseq) exp;
    	int n1 = maxArgsStm(eseq.stm);
    	int n2 = maxArgsExp(eseq.exp);
    	
    	return n1 >= n2 ? n1 : n2;
    }
    else if(exp instanceof Exp.Id || exp instanceof Exp.Num) {
    	return 0;
    }
    else
    new Bug();
    return 0;
  }
  
  
  private int maxArgsExpList(ExpList.T explist)
  {
	  if (explist instanceof ExpList.Pair) {
		  ExpList.Pair pair = (ExpList.Pair) explist;
		  int n1 = 1 + maxArgsExp(pair.exp);
		  int n2 = 1 + maxArgsExpList(pair.list);
		  
		  return n1 >= n2 ? n1 : n2;
	  }
	  else if(explist instanceof ExpList.Last)
	  {
		  ExpList.Last last = (ExpList.Last) explist;
		  int n = 1 + maxArgsExp(last.exp);
		  return n;
	  }
	  else
		  new Bug();
	  return 0;
  }

  private int maxArgsStm(Stm.T stm)
  {
    if (stm instanceof Stm.Compound) {
      Stm.Compound s = (Stm.Compound) stm;
      int n1 = maxArgsStm(s.s1);
      int n2 = maxArgsStm(s.s2);

      return n1 >= n2 ? n1 : n2;
    } else if (stm instanceof Stm.Assign) {
      Stm.Assign assign = (Stm.Assign) stm;
      int n = maxArgsExp(assign.exp);
      return n;
    } else if (stm instanceof Stm.Print) {
      Stm.Print prt = (Stm.Print) stm ;
      int n = maxArgsExpList(prt.explist);
      return n;
    } else
      new Bug();
    return 0;
  }

  // ////////////////////////////////////////
  // interpreter

  class IntAndTable{
		int i;
		Table t;
		IntAndTable(int ii, Table tt){
			i = ii;
			t = tt;
		}
		
	}
  
  private IntAndTable interpExp(Exp.T exp,Table T)
  {
	  if(exp instanceof Exp.Id) {
		  Exp.Id expId = (Exp.Id) exp;
		  return new IntAndTable(T.lookup( T,  expId.id),T);
		  
	  }else if(exp  instanceof Exp.Num) {
		  Exp.Num expNum = (Exp.Num) exp;
		  
		  return new IntAndTable(expNum.num,T);
		   
	  }else if(exp instanceof Exp.Op) {
		  Exp.Op expOp = (Exp.Op) exp;
		  if(expOp.op == Exp.OP_T.ADD) {
			  IntAndTable L = interpExp(expOp.left,T);
			  T = L.t;
			  IntAndTable R = interpExp(expOp.right,T);
			  return new IntAndTable(L.i + R.i,T);
			  
		  }else if(expOp.op == Exp.OP_T.SUB) {
			  IntAndTable L = interpExp(expOp.left,T);
			  IntAndTable R = interpExp(expOp.right,T);
			  return new IntAndTable(L.i - R.i,T); 
			  
		  }else if(expOp.op == Exp.OP_T.TIMES) {
			  IntAndTable L = interpExp(expOp.left,T);
			  IntAndTable R = interpExp(expOp.right,T);
			  return new IntAndTable(L.i * R.i,T);
			  
		  }else if(expOp.op == Exp.OP_T.DIVIDE) {
			  IntAndTable L = interpExp(expOp.left,T);
			  IntAndTable R = interpExp(expOp.right,T);
			  return new IntAndTable(L.i / R.i,T);
			  
		  }else
			  new Bug();
		  
	  }else if (exp instanceof Exp.Eseq) {
		  Exp.Eseq eseq = (Exp.Eseq) exp;
		  Table t = interpStm(eseq.stm,T);//
		  t.tail = T;
		  T = t;
		  return interpExp(eseq.exp,t);
	  }else
		new Bug();
    return new IntAndTable(0,T);
  }

  private IntAndTable interpExplist(ExpList.T explist,Table T) {
	  if(explist instanceof ExpList.Pair) {
		  ExpList.Pair pair = (ExpList.Pair) explist;
		  IntAndTable iat1 = interpExp(pair.exp,T);
		  System.out.print(iat1.i+" ");
		  IntAndTable iat2 = interpExplist(pair.list,T);
		  
		  return iat2;
		  
	  }else if(explist instanceof ExpList.Last) {
		  ExpList.Last last = (ExpList.Last) explist;
		  return interpExp(last.exp,T);
		  
	  }else
		  new Bug();
	  
	  return new IntAndTable(0,T);
  }
  
  class Table{
		String id; 
		int value;
		Table tail;
		Table(String i, int v, Table t)
		{
			id = i;
			value = v;
			tail = t;
		}
		int lookup(Table t, String key) {
			while (true){
				
				if (t.id  == key) {
					return t.value;
				}
				if(t.tail == null)
				{
					return 0;
				}
				else {
					return lookup( t.tail,  key);
				}
			}
		}
	}


  private Table interpStm(Stm.T prog,Table t)
  {
	  
    if (prog instanceof Stm.Compound) {
      Stm.Compound compound = (Stm.Compound) prog;
      Table t1 = interpStm(compound.s1,t);
      //t1.tail = t;
      t = t1;
      Table t2 = interpStm(compound.s2,t1);
      t2.tail = t1;
      t = t2;
      return t2;
      
    } else if (prog instanceof Stm.Assign) {
      Stm.Assign assn = (Stm.Assign) prog;
      //Table t1 = new Table(assn.id,0,null);
      //t1.tail = t;
      //t = t1;
      IntAndTable iat = interpExp(assn.exp,t);
      return new Table(assn.id,iat.i,iat.t);
      
    } else if (prog instanceof Stm.Print) {
      Stm.Print prnt = (Stm.Print) prog;
      System.out.println(interpExplist(prnt.explist,t).i);
      
      //return 
    } else
      new Bug();
      return new Table(null, 1 , null);
  }

  // ////////////////////////////////////////
  // compile
  HashSet<String> ids;
  StringBuffer buf;

  private void emit(String s)
  {
    buf.append(s);
  }

  private void compileExp(Exp.T exp)
  {
    if (exp instanceof Id) {
      Exp.Id e = (Exp.Id) exp;
      String id = e.id;

      emit("\tmovl\t" + id + ", %eax\n");
    } else if (exp instanceof Num) {
      Exp.Num e = (Exp.Num) exp;
      int num = e.num;

      emit("\tmovl\t$" + num + ", %eax\n");
    } else if (exp instanceof Op) {
      Exp.Op e = (Exp.Op) exp;
      Exp.T left = e.left;
      Exp.T right = e.right;
      Exp.OP_T op = e.op;

      switch (op) {
      case ADD:
        compileExp(left);
        emit("\tpushl\t%eax\n");
        compileExp(right);
        emit("\tpopl\t%edx\n");
        emit("\taddl\t%edx, %eax\n");
        break;
      case SUB:
        compileExp(left);
        emit("\tpushl\t%eax\n");
        compileExp(right);
        emit("\tpopl\t%edx\n");
        emit("\tsubl\t%eax, %edx\n");
        emit("\tmovl\t%edx, %eax\n");
        break;
      case TIMES:
        compileExp(left);
        emit("\tpushl\t%eax\n");
        compileExp(right);
        emit("\tpopl\t%edx\n");
        emit("\timul\t%edx\n");
        break;
      case DIVIDE:
        compileExp(left);
        emit("\tpushl\t%eax\n");
        compileExp(right);
        emit("\tpopl\t%edx\n");
        emit("\tmovl\t%eax, %ecx\n");
        emit("\tmovl\t%edx, %eax\n");
        emit("\tcltd\n");
        emit("\tdiv\t%ecx\n");
        break;
      default:
        new Bug();
      }
    } else if (exp instanceof Eseq) {
      Eseq e = (Eseq) exp;
      Stm.T stm = e.stm;
      Exp.T ee = e.exp;

      compileStm(stm);
      compileExp(ee);
    } else
      new Bug();
  }

  private void compileExpList(ExpList.T explist)
  {
    if (explist instanceof ExpList.Pair) {
      ExpList.Pair pair = (ExpList.Pair) explist;
      Exp.T exp = pair.exp;
      ExpList.T list = pair.list;

      compileExp(exp);
      emit("\tpushl\t%eax\n");
      emit("\tpushl\t$slp_format\n");
      emit("\tcall\tprintf\n");
      emit("\taddl\t$4, %esp\n");
      compileExpList(list);
    } else if (explist instanceof ExpList.Last) {
      ExpList.Last last = (ExpList.Last) explist;
      Exp.T exp = last.exp;

      compileExp(exp);
      emit("\tpushl\t%eax\n");
      emit("\tpushl\t$slp_format\n");
      emit("\tcall\tprintf\n");
      emit("\taddl\t$4, %esp\n");
    } else
      new Bug();
  }

  private void compileStm(Stm.T prog)
  {
    if (prog instanceof Stm.Compound) {
      Stm.Compound s = (Stm.Compound) prog;
      Stm.T s1 = s.s1;
      Stm.T s2 = s.s2;

      compileStm(s1);
      compileStm(s2);
    } else if (prog instanceof Stm.Assign) {
      Stm.Assign s = (Stm.Assign) prog;
      String id = s.id;
      Exp.T exp = s.exp;

      ids.add(id);
      compileExp(exp);
      emit("\tmovl\t%eax, " + id + "\n");
    } else if (prog instanceof Stm.Print) {
      Stm.Print s = (Stm.Print) prog;
      ExpList.T explist = s.explist;

      compileExpList(explist);
      emit("\tpushl\t$newline\n");
      emit("\tcall\tprintf\n");
      emit("\taddl\t$4, %esp\n");
    } else
      new Bug();
  }

  // ////////////////////////////////////////
  public void doit(Stm.T prog)
  {
    // return the maximum number of arguments
    if (Control.ConSlp.action == Control.ConSlp.T.ARGS) {
      int numArgs = maxArgsStm(prog);
      System.out.println(numArgs);
    }

    // interpret a given program
    if (Control.ConSlp.action == Control.ConSlp.T.INTERP) {
    	//Table T = new Table("aasasasas",0,null);
        interpStm(prog,null);
    }

    // compile a given SLP program to x86
    if (Control.ConSlp.action == Control.ConSlp.T.COMPILE) {
      ids = new HashSet<String>();
      buf = new StringBuffer();

      compileStm(prog);
      try {
        // FileOutputStream out = new FileOutputStream();
        FileWriter writer = new FileWriter("slp_gen.s");
        writer
            .write("// Automatically generated by the Tiger compiler, do NOT edit.\n\n");
        writer.write("\t.data\n");
        writer.write("slp_format:\n");
        writer.write("\t.string \"%d \"\n");
        writer.write("newline:\n");
        writer.write("\t.string \"\\n\"\n");
        for (String s : this.ids) {
          writer.write(s + ":\n");
          writer.write("\t.int 0\n");
        }
        writer.write("\n\n\t.text\n");
        writer.write("\t.globl main\n");
        writer.write("main:\n");
        writer.write("\tpushl\t%ebp\n");
        writer.write("\tmovl\t%esp, %ebp\n");
        writer.write(buf.toString());
        writer.write("\tleave\n\tret\n\n");
        writer.close();
        Process child = Runtime.getRuntime().exec("gcc slp_gen.s");
        child.waitFor();
        if (!Control.ConSlp.keepasm)
          Runtime.getRuntime().exec("rm -rf slp_gen.s");
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(0);
      }
      // System.out.println(buf.toString());
    }
  }
}
