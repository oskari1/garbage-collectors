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
				if(is_reachable_call_to_get_delivery(u, an, man)) {

					// get_delivery only has single argument
					Value arg = ((JInvokeStmt) u).getInvokeExpr().getArg(0);
					// logger.debug("entered while-loop while is_call_to_get_delivery with arg = " + arg.toString());

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
				if (u instanceof JVirtualInvokeExpr){
					logger.debug(" " + ((JVirtualInvokeExpr) u).getBase()); 
				}
				if(is_reachable_call_to_get_delivery(u, an, man)) {

					// get_delivery only has single argument
					if (u instanceof JVirtualInvokeExpr){
						logger.debug("Got call to delivery: " + ((JVirtualInvokeExpr) u).getBase()); 
					}
					ValueBox store_reference = u.getUseBoxes().get(1); 
					logger.debug("store_reference is " + store_reference.getValue().toString());
					logger.debug("HERE123" + u.getUseBoxes().get(1).toString()); 
					for(StoreInitializer store : pointsTo.pointsTo((Local) store_reference.getValue())) {
						logger.debug(String.valueOf(store.trolley_size));
						logger.debug("StoreInitializer store with id " + store.getUniqueLabel());

						Value arg = ((JInvokeStmt) u).getInvokeExpr().getArg(0);
						// logger.debug("entered while-loop while is_call_to_get_delivery with arg = " + arg.toString());
						
						if (arg instanceof IntConstant) {
							if (((IntConstant) arg).value > store.trolley_size){
								valid = false; 
							} 
						} else if (arg instanceof JimpleLocal) {
							Abstract1 in = an.getFlowBefore(u).get();
							String arg_name = ((JimpleLocal) arg).getName();
							try {
								// idea: safe iff arg_var <= trolley_size iff trolley_size - arg_var >= 0
								Texpr1Node arg_var = new Texpr1VarNode(arg_name); 
								Texpr1CstNode trolley_const = new Texpr1CstNode(new MpqScalar(store.trolley_size));
								Texpr1BinNode binop = new Texpr1BinNode(Texpr1BinNode.OP_SUB, trolley_const, arg_var);
								Tcons1 constraint = new Tcons1(env, Tcons1.SUPEQ, binop);
								logger.debug("Constructed constraint: " + constraint.toString());
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
		}
		return valid;
	}

	@Override
	public boolean checkFitsInReserve() {
		// TODO: FILL THIS OUT
		boolean valid = true; 
		for(Map.Entry<SootMethod, NumericalAnalysis> entry : numericalAnalysis.entrySet()) {
			// goal: iterate through CFG of the analyzed method
			// in a DFS-like manner and associate to each node of the CFG how much each Store-object 
			// has received at most until that point
			SootMethod m = entry.getKey();
			NumericalAnalysis an = entry.getValue();
			Manager man = an.man;
			Environment env = an.env;
			UnitGraph g = SootHelper.getUnitGraph(m);

			// define visited and active map for BFS
			HashMap<Unit,Boolean> visited = new HashMap<Unit,Boolean>(g.size()); 
			HashMap<Unit,Boolean> active = new HashMap<Unit,Boolean>(g.size()); 
			// define map that assigns to each unit, how much each Store-object has
			// received up to that point

			// in general need HashMap<Unit, HashMap<StoreInitializer, Integer>> since we need to store for each
			// node, how much each object has received at most up to that point
			HashMap<Unit,Integer> received_amt = new HashMap<Unit,Integer>(g.size());
			// initialize maps
			Iterator<Unit> i = g.iterator();
			while(i.hasNext()) {
				Unit v = (Unit) i.next();
				visited.put(v,new Boolean(false));
				active.put(v,new Boolean(false));
				received_amt.put(v,new Integer(0));
			}
			// logger.debug("CFG: " + g.toString());


			// traverse CFG in BFS-order
			for(Unit u : g.getHeads()) {
				if(!visited.get(u).booleanValue()) {
					ArrayDeque<Unit> toVisit = new ArrayDeque<Unit>();
					active.put(u,new Boolean(true));
					toVisit.add(u);
					while(!toVisit.isEmpty()) {
						Unit w = toVisit.poll();
						visited.put(w, new Boolean(true));

						// update the map received_amt 
						// by taking the maximum amounts among the predecessor nodes
						int max_amt_preds = 0;
						for(Unit pred : g.getPredsOf(w)) {
							max_amt_preds = Math.max(received_amt.get(pred).intValue(), max_amt_preds);
						} 
						received_amt.put(w, new Integer(max_amt_preds));

						// if we have a call to get_delivery, add the received amount to the appropriate object 
						// todo: handle case of get_delivery called within a loop
						if(is_reachable_call_to_get_delivery(w, an, man)) {
							Value arg = ((JInvokeStmt) w).getInvokeExpr().getArg(0);
							MpqScalar delivered_amt = upper_bound_of(arg, an, w, man); 
							if(delivered_amt.isInfty() != 0) {
								// if received amount is unbounded, FITS_IN_RESERVE is certainly not SAFE 
								return false;
							} else {
								// if received amount is finite, need to compare with reserve_size
								int prev_amt = received_amt.get(w).intValue();
								int new_amt = prev_amt + Integer.valueOf(delivered_amt.toString());
								received_amt.put(w, new Integer(new_amt));
							}

							// check if any of the store objects has received more than its reserve_size 
							ValueBox store_reference = w.getUseBoxes().get(1);
							for(StoreInitializer store : pointsTo.pointsTo((Local) store_reference.getValue())) {
								if(store.reserve_size < received_amt.get(w)) {
									// received amount exceeds reserve_size, so FITS_IN_RESERVE is UNSAFE
									return false;
								}
							}
						} 

						

						// continue with ordinary BFS
						for(Unit x : g.getSuccsOf(w)) {
							if(!visited.get(x).booleanValue() && !active.get(x).booleanValue()) {
								active.put(x, new Boolean(true));
								toVisit.add(x);
							}
						}
					}
				}
			}
		}
		return valid;
	}

	// TODO: MAYBE FILL THIS OUT: add convenience methods
	private boolean is_reachable_call_to_get_delivery(Unit u, NumericalAnalysis an, Manager man) {
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

	private MpqScalar upper_bound_of (Value arg, NumericalAnalysis an, Unit u, Manager man) {
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
