package slp2;

import slp2.Slp.Exp;
import slp2.Slp.Exp.Eseq;
import slp2.Slp.Exp.Id;
import slp2.Slp.Exp.Num;
import slp2.Slp.Exp.Op;
import slp2.Slp.ExpList.Last;
import slp2.Slp.ExpList.Pair;
import slp2.Slp.Stm;
import slp2.Slp.Stm.Assign;
import slp2.Slp.Stm.Compound;
import slp2.Slp.Stm.Print;

public class Samples
{
  public static Stm.T prog = new Compound(new Assign("a", new Op(Exp.OP_T.ADD,
      new Num(3), new Num(5))), new Compound(new Assign("b", new Eseq(
      new Print(new Pair(new Id("a"), new Last(new Op(Exp.OP_T.SUB,
          new Id("a"), new Num(1))))), new Op(Exp.OP_T.TIMES, new Num(10),
          new Id("a")))), new Print(new Last(new Id("b")))));
  
  public static Stm.T prog1 = new Compound(new Assign("a", new Op(Exp.OP_T.ADD,
	      new Num(3), new Num(5))), new Compound(new Assign("b", new Eseq(
	      new Print(new Pair(new Id("a"), new Pair(new Id("a"),new Last(new Op(Exp.OP_T.SUB,
	          new Id("a"), new Num(1)))))), new Op(Exp.OP_T.TIMES, new Num(10),
	          new Id("a")))), new Print(new Last(new Id("b")))));


  public static Stm.T dividebyzero =
      new Print (new Last (new Op(Exp.OP_T.DIVIDE, new Num (1), new Num(0))));
  
  
}
