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
import ch.ethz.rse.numerical.IntegerWrapper;
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
			logger.debug("CFG: " + g.toString());
			Iterator<Unit> i = g.iterator();
			
			while(i.hasNext()) {

				Unit u = (Unit) i.next();
				logger.debug("entered while-loop with node " + u.toString());
				if(is_call_to_get_delivery(u)) {

					// get_delivery only has single argument
					Value arg = ((JInvokeStmt) u).getInvokeExpr().getArg(0);
					// logger.debug("entered while-loop while is_call_to_get_delivery with arg = " + arg.toString());

					if (arg instanceof IntConstant) {
						// logger.debug("Issa int ayy " + ((IntConstant) arg).value); 
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
			logger.debug("CFG: " + g.toString());
			Iterator<Unit> i = g.iterator();
   
			// Also get loopHeadState, then use the bound calculated by loopHeads and the number of iterations by using loopheadState
			
			while(i.hasNext()) {

				Unit u = (Unit) i.next();
				logger.debug("entered while-loop with node " + u.toString());

				if (u instanceof JVirtualInvokeExpr){
					logger.debug(" " + ((JVirtualInvokeExpr) u).getBase()); 
				}
				if(is_call_to_get_delivery(u)) {
					int lowest_capacity = Integer.MAX_VALUE; 
					ValueBox store_reference = u.getUseBoxes().get(1); 
					// logger.debug("HERE123" + u.getUseBoxes().get(1).toString()); 
					for(StoreInitializer store : pointsTo.pointsTo((Local) store_reference.getValue())) {
						logger.debug(String.valueOf(store.trolley_size));
						if (store.trolley_size<lowest_capacity){
							lowest_capacity = store.trolley_size; 
						}
					}
					
					logger.debug("Lowest capacity found: " + lowest_capacity); 

					Value arg = ((JInvokeStmt) u).getInvokeExpr().getArg(0);
					// logger.debug("entered while-loop while is_call_to_get_delivery with arg = " + arg.toString());
					
					if (arg instanceof IntConstant) {
						if (((IntConstant) arg).value > lowest_capacity){
							valid = false; 
						} 
					
					} else if (arg instanceof JimpleLocal) {
						Abstract1 in = an.getFlowBefore(u).get();
						String arg_name = ((JimpleLocal) arg).getName();
						try {
							logger.debug("Bound: " + in.getBound(man, arg_name).toString());
							logger.debug("Abstract state in: " + in.toString(man));
							String upperboundstring = in.getBound(man, arg_name).sup.toString(); 
							if (upperboundstring == "+oo"){
								valid = false; 
								logger.debug("Upper bound of argument is +oo"); 
							} else if (upperboundstring == "-oo"){ 
								logger.debug("Upper bound of argument is -oo"); 
							} else {
								int upperbound = Integer.valueOf(upperboundstring);
								
								logger.debug("Upper bound: " + upperbound); 
								if (upperbound > lowest_capacity){
									valid = false; 
								}
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
	public boolean checkFitsInReserve() {
		boolean valid = true; 
		Map<String, Integer> remaining_capacity = new HashMap<String,Integer>();
		for(Map.Entry<SootMethod, NumericalAnalysis> entry : numericalAnalysis.entrySet()) {
			// goal: iterate through CFG of the analyzed method
			// and in each node of the CFG, check if it's a
			// call to get_delivery(v) and if so, check that v >= 0
			SootMethod m = entry.getKey();
			NumericalAnalysis an = entry.getValue();
			Manager man = an.man;
			Environment env = an.env;
			UnitGraph g = SootHelper.getUnitGraph(m);
			logger.debug("CFG: " + g.toString());
			Iterator<Unit> i = g.iterator();
			// HashMap<Unit, IntegerWrapper> loopHeads = an.getLoopHeads();
			// HashMap<Unit, NumericalStateWrapper> loopHeadState = an.getLoopHeadState();
			
			while(i.hasNext()) {

				Unit u = (Unit) i.next();
				logger.debug("entered while-loop with node " + u.toString());
				// logger.debug("loopHead: " + loopHeads.get(u).toString()); 
				// logger.debug("loopHeadState: " + loopHeadState.get(u).toString()); 
				if (u instanceof JVirtualInvokeExpr){
					logger.debug(" " + ((JVirtualInvokeExpr) u).getBase()); 
				}
				if(is_call_to_get_delivery(u)) {
					
					Value arg = ((JInvokeStmt) u).getInvokeExpr().getArg(0);
					// logger.debug("entered while-loop while is_call_to_get_delivery with arg = " + arg.toString());
					
					int upperbound = Integer.MAX_VALUE; 
					int lowerbound = Integer.MIN_VALUE; 

					if (arg instanceof IntConstant) {
						upperbound = ((IntConstant) arg).value; 
						lowerbound = ((IntConstant) arg).value; 
						
					} else if (arg instanceof JimpleLocal) {
						Abstract1 in = an.getFlowBefore(u).get();
						String arg_name = ((JimpleLocal) arg).getName();
						try {
							logger.debug("Bound: " + in.getBound(man, arg_name).toString());
							logger.debug("Abstract state in: " + in.toString(man));
							String upperboundstring = in.getBound(man, arg_name).sup.toString(); 
							String lowerboundstring = in.getBound(man, arg_name).inf.toString(); 
							if (upperboundstring == "+oo"){
								upperbound = Integer.MAX_VALUE;  
							} else if (upperboundstring == "-oo"){ 
								upperbound = Integer.MIN_VALUE; 
							} else {
								upperbound = Integer.valueOf(upperboundstring);
							}

							if (lowerboundstring == "+oo"){
								lowerbound = Integer.MAX_VALUE;  
							} else if (lowerboundstring == "-oo"){ 
								lowerbound = Integer.MIN_VALUE; 
							} else {
								lowerbound = Integer.valueOf(lowerboundstring);
							}

						} catch (ApronException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					} else {
						throw new RuntimeException("Unhandled case for arg of get_delivery");
					}

					
					if (upperbound == Integer.MAX_VALUE){
						valid=false; 
					} else if (lowerbound == Integer.MIN_VALUE){
						continue; 
					}

					// Calculating how much capacity is needed, assuming all the values inside the bound are used once to call get_delivery. This is an unsound assumption, thus it is not used. 
					int total = (int) (((1.0+((double)upperbound - (double)lowerbound))/2.0) * ((double)lowerbound + (double)upperbound)); 
					logger.debug("Upper bound: " + upperbound + " lowerbound: " + lowerbound + " total: " + total);

					ValueBox store_reference = u.getUseBoxes().get(1); 
					for(StoreInitializer store : pointsTo.pointsTo((Local) store_reference.getValue())) {
						logger.debug(String.valueOf(store.trolley_size));
						String uniqueLabel = store.getUniqueLabel(); 
						if (!remaining_capacity.containsKey(uniqueLabel)){
							remaining_capacity.put(uniqueLabel, store.reserve_size); 
						}
												
						remaining_capacity.put(uniqueLabel, (remaining_capacity.get(uniqueLabel)-upperbound)); 
						if (remaining_capacity.get(uniqueLabel)<0){
							valid = false;
						}
						logger.debug("Remaining capacity in store " + uniqueLabel + " is " + remaining_capacity.get(uniqueLabel)); 
					}



				}

			}
		}
		return valid;
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
