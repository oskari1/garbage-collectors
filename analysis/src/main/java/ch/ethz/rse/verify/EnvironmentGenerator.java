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

		String ints_arr[] = Iterables.toArray(this.ints, String.class);
		
		String reals[] = {}; // we are not analyzing real numbers
		this.env = new Environment(ints_arr, reals);
	}

	public Environment getEnvironment() {
		return this.env;
	}

	// TODO: MAYBE FILL THIS OUT: add convenience methods

}
