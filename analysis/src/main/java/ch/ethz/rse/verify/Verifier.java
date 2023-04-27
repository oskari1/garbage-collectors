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
		// Create the abstracts that will be needed to construct the polyhedron? 
		// Call whatever method will add the constraints to the polyhedron? 
		// Save the result in the this.numericalAnalysis field. 
		List<SootMethod> meths = this.c.getMethods(); 
		for (SootMethod meth : meths){
			NumericalAnalysis num_analysis = new NumericalAnalysis(meth, property, pointsTo); 
			this.numericalAnalysis.put(meth, num_analysis); 
		}
		logger.info("ran runNumericalAnalysis");
	}

	@Override
	public boolean checksNonNegative() {
		// TODO: FILL THIS OUT
		// Retrieve the content of this.numericalAnalysis and check if the property is correct
		logger.info(this.numericalAnalysis.toString()); 
		logger.info("Calling checksNonNegative");
		
		// Check all the methods in the class we are analyzing
		Set<SootMethod> meths = this.numericalAnalysis.keySet(); 
		for (SootMethod meth : meths){

			NumericalAnalysis lfp = (this.numericalAnalysis).get(meth); 
			Environment env = lfp.env; 
			Manager man = lfp.man; 
			Body methBody = meth.getActiveBody(); 
			Chain<Unit> units = methBody.getUnits(); 

			// For all units inside the method we are analyzing, check whether or not they are calls to get_delivery. 
			for (Unit unit : units){
				if (unit instanceof JInvokeStmt){

					// I don't know how to make this nicer, I'm sorry 
					if ((((((JInvokeStmt)unit).getInvokeExpr()).getMethod()).getName()).equals("get_delivery")){

						// Get all the arguments
						List<Value> arguments = ((JInvokeStmt)unit).getInvokeExpr().getArgs(); 
						for (Value argument : arguments){
							logger.info("Argument" + argument.getClass().getName().toString()); 
							// If the arguments are constant - compare to 0. If not, do some other fancy things. 
							if (argument instanceof IntConstant){
								if (((IntConstant)argument).value<0){
									return false;
								}
							
							} else if (argument instanceof JimpleLocal){
								JimpleLocal arg = (JimpleLocal) argument; 
								String arg_name = arg.getName(); 
								logger.info("Arg_name is: " + arg_name); 

								// Get the value of the unit (get_delivery()) after the flow-analysis
								Abstract1 nswa = lfp.getFallFlowAfter(unit).get(); 

								// Print the bound of the variable we are currently looking at - for debugging purposes
								try {
									logger.info("Bound: " + (nswa.getBound(man, arg_name)).toString());
								} catch (ApronException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 

								// Tester is (a representation of) the variable we are currently looking at
								Texpr1Node tester = new Texpr1VarNode(arg_name);
								// encode tester >= 0
								Tcons1 constraint = new Tcons1(env, Tcons1.SUPEQ, tester);
								try {
									if (!(nswa.satisfy(man, constraint))){
										return false; 
									}
								} catch (ApronException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		} 


		return true;
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

}
