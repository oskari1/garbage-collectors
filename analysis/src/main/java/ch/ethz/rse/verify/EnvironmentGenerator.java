package ch.ethz.rse.verify;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Iterables;

import apron.Environment;
import ch.ethz.rse.pointer.StoreInitializer;
import ch.ethz.rse.pointer.PointsToInitializer;
import ch.ethz.rse.utils.Constants;
import soot.IntegerType;
import soot.Local;
import soot.PointsToAnalysis;
import soot.SootMethod;
import soot.Value;
import soot.jimple.ParameterRef;
import soot.jimple.internal.JimpleLocal;
import soot.util.Chain;

import org.slf4j.Logger;  //NEW
import org.slf4j.LoggerFactory; //NEW

/**
 * Generates an environment which holds all variable names needed for the
 * numerical analysis of a method
 *
 */
public class EnvironmentGenerator {

	private final SootMethod method;

	private final PointsToInitializer pointsTo;

	private static final Logger logger = LoggerFactory.getLogger(EnvironmentGenerator.class);

	/**
	 * List of names for integer variables relevant when analyzing the program
	 */
	private List<String> ints = new LinkedList<String>();

	private final Environment env;

	/**
	 * 
	 * @param method
	 */
	public EnvironmentGenerator(SootMethod method, PointsToInitializer pointsTo) {
		this.method = method;
		this.pointsTo = pointsTo;

		// populate this.ints
		// TODO: FILL THIS OUT
		// note that List<String> ints stores the names of all integer variables
		// Once I have successfully implemented this, the test case Var_Test_Safe.java
		// might work since the environment includes the variables at that point
		// However, it might be that I still have to implement parts of NumericalAnalysis.java
		// such that the analysis captures the fact that v -> [0,0] after "int v = 0;""

		// more concretely, here we probably just have to iterate through the CFG
		// and for each DefinitionStmt we have to add the lhs to the ints-list
		// it's easier to just use getActiveBody()

		Chain<Local> locals = method.getActiveBody().getLocals();
		for(Local local : locals) {
			this.ints.add(local.getName());
		}
		// logger.debug("Added locals are " + locals);

		// for debugging purposes, we try to print out the Jimple code:
		// logger.debug(body.toString());

		// This should add the method-parameters to the ints
		// In the description, we assume they can only be of type int, thus we just add all
		// String param = method.getBytecodeParms();
		// logger.debug("Added method parameters are " + param);
		// this.ints.add(param);

		// Chain<Local> locals = method.getActiveBody().getLocals(); -- merge confilc
		// Iterator localItr = locals.iterator(); 

		// while (localItr.hasNext()){
		// 	Local curLocal = (Local)localItr.next(); 
		// 	this.ints.add(curLocal.getName()); 
		// }
		// logger.info("Environment was generated"); 

		String ints_arr[] = Iterables.toArray(this.ints, String.class);
		
		String reals[] = {}; // we are not analyzing real numbers
		this.env = new Environment(ints_arr, reals);
	}

	public Environment getEnvironment() {
		return this.env;
	}

	// TODO: MAYBE FILL THIS OUT: add convenience methods

}
