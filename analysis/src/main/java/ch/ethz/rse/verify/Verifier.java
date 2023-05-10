package ch.ethz.rse.verify;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apron.Abstract1;
import apron.ApronException;
import apron.Environment;
import apron.Interval;
import apron.Manager;
import apron.MpqScalar;
import apron.Polka;
import apron.Tcons1;
import apron.Texpr1CstNode;
import apron.Texpr1Node;
import apron.Texpr1VarNode;
import ch.ethz.rse.VerificationProperty;
import ch.ethz.rse.numerical.NumericalAnalysis;
import ch.ethz.rse.numerical.NumericalStateWrapper;
import ch.ethz.rse.pointer.StoreInitializer;
import ch.ethz.rse.pointer.PointsToInitializer;
import ch.ethz.rse.utils.Constants;
import polyglot.ast.Call;
import polyglot.frontend.Pass;
import soot.Body;
import soot.Local;
import soot.SootClass;
import soot.SootHelper;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import soot.util.Cons;

// Added imports
import java.util.*;
import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;
import apron.Abstract1;
import apron.ApronException;
import apron.Environment;
import apron.Interval;
import apron.Manager;
import apron.Tcons1;

/**
 * Main class handling verification
 * 
 */
public class Verifier extends AVerifier {

	private static final Logger logger = LoggerFactory.getLogger(Verifier.class);

	/**
	 * class to be verified
	 */
	private final SootClass c;

	/**
	 * points to analysis for verified class
	 */
	private final PointsToInitializer pointsTo;

	/**
	 * 
	 * @param c class to verify
	 */
	public Verifier(SootClass c) {
		//Adding a new test comment

		logger.debug("Analyzing {}", c.getName());

		this.c = c;

		// pointer analysis
		this.pointsTo = new PointsToInitializer(this.c);
	}

	protected void runNumericalAnalysis(VerificationProperty property) {
		// TODO: FILL THIS OUT
		// here we need to construct an appropriate NumericalAnalysis-object 
		// we assume the class we analyze only has a single method (see project description)
		for(SootMethod method : c.getMethods()) {
			// note that each class has two methods, the method we are analyzing and the constructor
			// which according to description always has name <init>
			// note that constructing the NumericalAnalysis object automatically start the analysis
			NumericalAnalysis an = new NumericalAnalysis(method, property, this.pointsTo); 
			numericalAnalysis.put(method, an);
		}
	}

	@Override
	public boolean checksNonNegative() {
		// TODO: FILL THIS OUT
		boolean valid = true; 
		for(Map.Entry<SootMethod, NumericalAnalysis> entry : numericalAnalysis.entrySet()) {
			// goal: iterate through CFG of the analyzed method
			// and in each node of the CFG, check if it's a
			// call to get_delivery(v) and if so, check that v >= 0
			SootMethod m = entry.getKey();
			NumericalAnalysis an = entry.getValue();
			Manager man = an.man;
			Environment env = an.env;
			UnitGraph g = SootHelper.getUnitGraph(m);
			Iterator<Unit> i = g.iterator();
			
			while(i.hasNext()) {

				Unit u = (Unit) i.next();
				if(is_call_to_get_delivery(u)) {

					// get_delivery only has single argument
					Value arg = ((JInvokeStmt) u).getInvokeExpr().getArg(0);

					if (arg instanceof IntConstant) {
						if (!(((IntConstant) arg).value >= 0)){
							valid = false; 
						} 
					} else if (arg instanceof JimpleLocal) {
						Abstract1 in = an.getFlowBefore(u).get();
						String arg_name = ((JimpleLocal) arg).getName();
						try {
							Texpr1Node arg_var = new Texpr1VarNode(arg_name); 
							Tcons1 constraint = new Tcons1(env, Tcons1.SUPEQ, arg_var);
							logger.debug("Bound: " + in.getBound(man, arg_name).toString());
							logger.debug("Abstract state in: " + in.toString(man));
							if (!in.satisfy(man, constraint)){
								valid = false; 
							}
						} catch (ApronException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					} else {
						throw new RuntimeException("Unhandled case for arg of get_delivery");
					}

				}

			}
		}
		return valid;
	}

	@Override
	public boolean checkFitsInTrolley() {
		// TODO: FILL THIS OUT
		return true;
	}

	@Override
	public boolean checkFitsInReserve() {
		// TODO: FILL THIS OUT
		return true;
	}

	// TODO: MAYBE FILL THIS OUT: add convenience methods
	private boolean is_call_to_get_delivery(Unit u) {
		if (u instanceof JInvokeStmt) {
			return (((((JInvokeStmt) u).getInvokeExpr()).getMethod()).getName()).equals("get_delivery");
		} else {
			return false;
		}
	}

}
