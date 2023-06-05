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
import apron.Texpr1BinNode;
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
import soot.ValueBox;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.internal.JimpleLocalBox;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import soot.util.Cons;

import java.rmi.NotBoundException;
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
			if(!method.isConstructor()) {
				NumericalAnalysis an = new NumericalAnalysis(method, property, this.pointsTo); 
				numericalAnalysis.put(method, an);
			}
		}
	}

	@Override
	public boolean checksNonNegative() {
		// TODO: FILL THIS OUT
		for(Map.Entry<SootMethod, NumericalAnalysis> entry : numericalAnalysis.entrySet()) {
			NumericalAnalysis an = entry.getValue();
			if(!an.non_negative_satisfied) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean checkFitsInTrolley() {
		// TODO: FILL THIS OUT
		for(Map.Entry<SootMethod, NumericalAnalysis> entry : numericalAnalysis.entrySet()) {
			NumericalAnalysis an = entry.getValue();
			if(!an.fits_in_trolley_satisfied) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean checkFitsInReserve() {
		// TODO: FILL THIS OUT
		for(Map.Entry<SootMethod, NumericalAnalysis> entry : numericalAnalysis.entrySet()) {
			NumericalAnalysis an = entry.getValue();
			if(!an.fits_in_reserve_satisfied) {
				return false;
			}
		}
		return true;
		// boolean valid = true; 
		// for(Map.Entry<SootMethod, NumericalAnalysis> entry : numericalAnalysis.entrySet()) {
		// 	NumericalAnalysis an = entry.getValue();
		// 	if(!an.fitsInReserve()) {
		// 		valid = false;
		// 	}
		// }
		// if(!valid) {
			// logger.debug("entered checkFitsInReserve");
		// for(Map.Entry<SootMethod, NumericalAnalysis> entry : numericalAnalysis.entrySet()) {
		// 	SootMethod m = entry.getKey();
		// 	NumericalAnalysis an = entry.getValue();
		// 	Manager man = an.man;
		// 	Environment env = an.env;
		// 	UnitGraph g = SootHelper.getUnitGraph(m);


		// 	// this stores at each unit of the CFG, how much each Store object has
		// 	// received until that point
		// 	AmountsPerNode received_amt = new AmountsPerNode(g, pointsTo, m, an, man, env);

		// 	try {
		// 		received_amt.compute_received_amounts();
		// 	} catch(FitsInReserveException e) {
		// 		// this is only thrown if some Store-object receives an infinite amount
		// 		// in which case FITS_IN_RESERVE is false
		// 		return false; 
		// 	}
		// }
		// return true;
		// } else {
		// 	return valid;
		// }
	}

	// TODO: MAYBE FILL THIS OUT: add convenience methods
	public static boolean is_reachable_call_to_get_delivery(Unit u, NumericalAnalysis an, Manager man) {
		if (u instanceof JInvokeStmt) {
			Abstract1 in = an.getFlowBefore(u).get();
			try {
				if(!in.isBottom(man)) {
					return (((((JInvokeStmt) u).getInvokeExpr()).getMethod()).getName()).equals("get_delivery");
				} else {
					return false;
				}
			} catch (ApronException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public static MpqScalar upper_bound_of (Value arg, NumericalAnalysis an, Unit u, Manager man) {
		if (arg instanceof IntConstant) {
			return new MpqScalar(((IntConstant) arg).value);
		} else {
			assert(arg instanceof JimpleLocal);
			Abstract1 in = an.getFlowBefore(u).get();
			String arg_name = ((JimpleLocal) arg).getName();
			try {
				return (MpqScalar) in.getBound(man, arg_name).sup();
			} catch (ApronException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new MpqScalar();
			} 
		}
	}


}
