package ch.ethz.rse.verify;

import soot.toolkits.graph.UnitGraph;
import soot.Unit;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.Stmt;
import soot.toolkits.graph.LoopNestTree;
import java.util.HashMap;
import java.util.*;

public class LoopAnalysis {
    private LoopNestTree loops;
    private HashMap<Unit, Loop> loop_of_unit;

    public LoopAnalysis(UnitGraph g) {
        this.loops = new LoopNestTree(g.getBody());
        this.loop_of_unit = new HashMap<Unit, Loop>(g.size());
        for(Loop l : loops) {
            List<Stmt> stmts = l.getLoopStatements();
            for(Stmt s : stmts) {
                loop_of_unit.put((Unit) s, l);
            }
        }
    }

    public Loop loop_of_unit(Unit u) {
        return this.loop_of_unit.get(u);
    }

    public boolean terminates(Loop l) {
        if(l.loopsForever()) {
            // this is the case if the loop has no exit-nodes
            return false;
        } else {
            // if the loop has exit nodes then we 
            // extract conditional expression (assume there is only one)
            // of the form exp > 0 or exp >= 0
            // check that exp.getBound() (: Interval) in the JumpBackStmt
            // isLeq() than in the loopHead and that it's not isEqual() 
            // to it
            // in other words, we check that the domain of the conditional
            // expression is certainly strictly smaller after executing 
            // the loop body
            return false;
        }
    }

    public int max_iterations_of(Loop l) {
        // check how much smaller exp is in JumpBackStmt compared 
        // to loopHeader. Find the upper and lower bound of exp
        // in loopHeader and divide this number by the minimum
        // decrement of exp
        // this yields the max_iterations_of(loop l)
        return 1;
    }
    
}
