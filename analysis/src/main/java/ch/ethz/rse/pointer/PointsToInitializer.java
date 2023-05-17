package ch.ethz.rse.pointer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import apron.Texpr1Node;
import ch.ethz.rse.utils.Constants;
import heros.utilities.JsonArray;
import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.JastAddJ.Stmt;
import soot.baf.SpecialInvokeInst;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.spark.pag.Node;

import soot.Local;
import soot.util.Chain;

/**
 * Convenience class which helps determine the {@link StoreInitializer}s
 * potentially used to create objects pointed to by a given variable
 */
public class PointsToInitializer {

	private static final Logger logger = LoggerFactory.getLogger(PointsToInitializer.class);

	/**
	 * Internally used points-to analysis
	 */
	private final PointsToAnalysisWrapper pointsTo;

	/**
	 * class for which we are running points-to
	 */
	private final SootClass c;

	/**
	 * Maps abstract object indices to initializers
	 */
	private final Map<Node, StoreInitializer> initializers = new HashMap<Node, StoreInitializer>();

	/**
	 * All {@link StoreInitializer}s, keyed by method
	 */
	private final Multimap<SootMethod, StoreInitializer> perMethod = HashMultimap.create();

	public PointsToInitializer(SootClass c) {
		this.c = c;
		logger.debug("Running points-to analysis on " + c.getName());
		this.pointsTo = new PointsToAnalysisWrapper(c);
		logger.debug("Analyzing initializers in " + c.getName());
		this.analyzeAllInitializers();
	}

	int uniqueNumber = 4201;

	private void analyzeAllInitializers() {


		for (SootMethod method : this.c.getMethods()) {

			if (method.getName().contains("<init>")) {
				// skip constructor of the class
				continue;
			}

			for(Unit u : method.getActiveBody().getUnits()) {
				logger.debug("points - here: " + u);
				logger.debug("Type: " + u.getClass());
				if (u instanceof JInvokeStmt){
					JInvokeStmt invkStmt = (JInvokeStmt) u; 
					if (((invkStmt.getInvokeExpr()).getMethod()).getName().contains("<init>")){
						JSpecialInvokeExpr spInvkExpr = (JSpecialInvokeExpr) invkStmt.getInvokeExpr();
						
						if (isRelevantInit(spInvkExpr)){
							logger.debug("Is relevant"); 
							Value val1 = u.getUseBoxes().get(0).getValue();
							Value val2 = u.getUseBoxes().get(1).getValue();
							logger.debug("boxes : " + val1 + ", " + val2); 
							// Hey, I found the ugly code that uses a string - and I will do it too
							StoreInitializer storeInit = new StoreInitializer(invkStmt, uniqueNumber, Integer.parseInt(val1.toString()), Integer.parseInt(val2.toString()));
							uniqueNumber++; 
							perMethod.put(method, storeInit); 

							Collection<Node> nodes = getAllocationNodes(spInvkExpr);
							for(Node node : nodes) {
								initializers.put(node, storeInit);
							}


						} else {
							logger.debug("Is not relevant"); 
						}

						
						

					}
				}
				// if(u instanceof JAssignStmt) {
				// 	logger.debug("points: we have a new store");
				// 	// continue; 
				// 	// JSpecialInvokeExpr siExpr = (JSpecialInvokeExpr) u; 

				// 	//TODO: USE getAllocationNodes() 

				// 	// if(isRelevantInit(siExpr)) {
				// 	// 	Value val1 = siExpr.getUseBoxes().get(0).getValue();
				// 	// 	Value val2 = siExpr.getUseBoxes().get(1).getValue();
				// 	// 	logger.debug("WE got some values - " + val1 + val2); 
				// 	// }
				// }
			}
		}
	}
				// //this does not seem to work
				// if(u instanceof JSpecialInvokeExpr) {
				// 	JSpecialInvokeExpr expr = (JSpecialInvokeExpr)u;
				// 	if(isRelevantInit(expr)) {
				// 		Value val1 = expr.getUseBoxes().get(0).getValue();
				// 		Value val2 = expr.getUseBoxes().get(1).getValue();

						

				// 		logger.debug("points: --> " + val1 + " " + val2);

				// 	}
				// }

				// if(u instanceof JInvokeStmt) {
				// 	logger.debug("points: we have a statement");

				// 	JInvokeStmt jInvStmt = (JInvokeStmt) u;
				// 	JSpecialInvokeExpr specialInvokeExpr = jInvStmt.getInvokeExpr(); // .getInvokeExpr() has to be called on a store object. 
				// 	// JSpecialInvokeExpr specialInvokeWExpr = (JSpecialInvokeExpr) invokeExpr; 

				// 	String name = jInvStmt.getInvokeExpr().getMethod().getName();

				// 	if(name.equals("get_delivery")) {
				// 		//get delivery is called (no idea if this is needed at one point)
				// 	}

				// 	if(isRelevantInit(specialInvokeExpr)) {
				// 	//else if(name.equals("<init>")) { //this assumes that no other objects are deifined - maybe use the given function from below

				// 		//the variables
				// 		IntConstant val1 = (IntConstant) specialInvokeWExpr.getArg(0);
				// 		IntConstant val2 = (IntConstant) specialInvokeWExpr.getArg(1);

				// 		logger.debug("points:" + specialInvokeWExpr); 
				// 		logger.debug("points: #1: " + specialInvokeWExpr.getBase()); // this probably has to be converted to node

				// 		// IntConstant val1 = (IntConstant) stmt.getUseBoxes().get(0).getValue();
				// 		// IntConstant val2 = (IntConstant) stmt.getUseBoxes().get(1).getValue();




				// 		logger.debug("points " + jInvStmt.getUseBoxes().get(2)); //where the reference is stored
				// 		//gives -> JimpleLocalBox($r0)

				// 		StoreInitializer storeInit = new StoreInitializer(jInvStmt, uniqueNumber, val1.value, val2.value);
				// 		uniqueNumber += 1;


				// 		perMethod.put(method, storeInit); //not sure if this is the correct method


				// 		logger.debug("points: --> " + val1 + " " + val2);

				// 	}
				// }
			//}

	

			// populate data structures perMethod and initializers
			// TODO: FILL THIS OUT
		//}
	//}

	// TODO: MAYBE FILL THIS OUT: add convenience methods

	public Collection<StoreInitializer> getInitializers(SootMethod method) {
		return this.perMethod.get(method);
	}

	public List<StoreInitializer> pointsTo(Local base) {
		logger.debug("points: pointsTo local base: " + base);
		Collection<Node> nodes = this.pointsTo.getNodes(base);
		List<StoreInitializer> initializers = new LinkedList<StoreInitializer>();
		for (Node node : nodes) {
			StoreInitializer initializer = this.initializers.get(node);
			if (initializer != null) {
				// ignore nodes that were not initialized
				initializers.add(initializer);
			}
		}
		return initializers;
	}

	/**
	 * Returns all allocation nodes that could correspond to the given invokeExpression, which must be a call to Store init function
	 * Note that more than one node can be returned.
	 */
	public Collection<Node> getAllocationNodes(JSpecialInvokeExpr invokeExpr){
		logger.debug("points: getAllocatinoNodes " + invokeExpr);
		if(!isRelevantInit(invokeExpr)){
			throw new RuntimeException("Call to getAllocationNodes with " + invokeExpr.toString() + "which is not an init call for the Store class");
		}
		Local base = (Local) invokeExpr.getBase();
		Collection<Node> allocationNodes = this.pointsTo.getNodes(base);
		return allocationNodes;
	}

	public boolean isRelevantInit(JSpecialInvokeExpr invokeExpr){
		logger.debug("points: isRelevantInit " + invokeExpr);
		Local base = (Local) invokeExpr.getBase();
		boolean isRelevant = base.getType().toString().equals(Constants.StoreClassName);
		boolean isInit = invokeExpr.getMethod().getName().equals("<init>");
		return isRelevant && isInit;
	}
}
