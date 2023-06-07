package ch.ethz.rse.pointer;

import apron.MpqScalar;
import soot.jimple.internal.JInvokeStmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Contains information about the initializer of a Store object
 *
 */
public class StoreInitializer {
	private static final Logger logger = LoggerFactory.getLogger(StoreInitializer.class);

	/**
	 * statement that performs the initialization
	 */
	private final JInvokeStmt statement;

	/**
	 * Unique identifier of the initializer
	 */
	private final int uniqueNumber;

	/**
	 * first argument in the constructor
	 */
	public final int trolley_size;

	/**
	 * second argument in the constructor
	 */
	public final int reserve_size;


	/**
	 * 
	 * @param statement    piece of code running the initializer
	 * @param uniqueNumber unique identifier of the initializer
	 * @param argment      argument in the constructor
	 */
	public StoreInitializer(JInvokeStmt statement, int uniqueNumber, int trolley_size, int reserve_size) {
		this.statement = statement;
		this.uniqueNumber = uniqueNumber;
		this.trolley_size = trolley_size;
		this.reserve_size = reserve_size;
	}

	/**
	 * 
	 * @return piece of code running the initializer
	 */
	public JInvokeStmt getStatement() {
		return statement;
	}

	/**
	 * 
	 * @return unique identifier of the initializer
	 */
	private int getUniqueNumber() {
		return this.uniqueNumber;
	}

	/**
	 * 
	 * @return unique label of this initializer
	 */
	public String getUniqueLabel() {
		return "AbstractObject" + this.getUniqueNumber() + ".end";
	}

	public String toString() {
		return "AbstractObject" + this.getUniqueNumber();
	}

	public boolean checkFitsInTrolley(MpqScalar amount) {
		if(amount.isInfty() == 1) {
			return false;
		} else if(amount.isInfty() == -1) {
			return true;
		} else {
			int amount_int = int_of(amount);
			if(amount_int > trolley_size) {
				// logger.debug("trolley size is " + trolley_size);
				return false;
			} else {
				return true;
			}
		}
	}

	private int int_of(MpqScalar s) {
		assert(s.isInfty() == 0);
		return Integer.valueOf(s.toString());
	}



}