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

	// added fields
	private MpqScalar received_amount;



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
		// added initializer
		this.received_amount = new MpqScalar(0);
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

	public void receive(MpqScalar amount) {
		if(amount.isInfty() != 0) {
			// if delivered amount if infinite, just set the received
			// amount to that amount
			this.received_amount = amount;
		} else if(this.received_amount.isInfty() == 0) {
			// if delivered and received amount are finite,
			// just add them up
			int amount_int = int_of(amount);
			int received_amount_int = int_of(received_amount);
			// logger.debug("Object " + uniqueNumber + " has already received " + received_amount_int); 
			// logger.debug("Object " + uniqueNumber + " is receiving " + amount_int); 
			this.received_amount = new MpqScalar(received_amount_int + amount_int);
		}
		// if the received amount is already +oo or -oo, just keep 
		// that value, i.e., do nothing
	}

	public boolean checkFitsInTrolley(MpqScalar amount) {
		if(amount.isInfty() != 1) {
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

	public boolean satisfiesFitsInReserve() {
		if(received_amount.isInfty() == 1) {
			// if received amount is +oo, it does not satisfy fitsInReserve 
			// logger.debug("case +oo");
			return false;
		} else if(received_amount.isInfty() == -1) {
			// if the received amount is -oo, it satisfies fitsInReserve
			// logger.debug("case -oo");
			return true;
		} else {
			// if the received amount if finite, need to compare with 
			// the reserve_size
			// logger.debug("case finite");
			int received_amt_int = int_of(this.received_amount);
			// logger.debug("received amount is " + received_amt_int);
			// logger.debug("reserve size is" + reserve_size);
			return received_amt_int <= reserve_size; 
		}
	}

	private int int_of(MpqScalar s) {
		assert(s.isInfty() == 0);
		return Integer.valueOf(s.toString());
	}



}