package com.jacob.samples.ado;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * Custom dispatch object to make it easy for us to provide application specific
 * API.
 * 
 */
public class Command extends Dispatch {
	/**
	 * standard constructor
	 */
	public Command() {
		super("ADODB.Command");
	}

	/**
	 * This constructor is used instead of a case operation to turn a Dispatch
	 * object into a wider object - it must exist in every wrapper class whose
	 * instances may be returned from method calls wrapped in VT_DISPATCH
	 * Variants.
	 * 
	 * @param dispatchTarget
	 */
	public Command(Dispatch dispatchTarget) {
		super(dispatchTarget);
	}

	/**
	 * runs the "Properties" command
	 * 
	 * @return the properties
	 */
	public Variant getProperties() {
		return Dispatch.get(this, "Properties");
	}

	/**
	 * runs the "ActiveConnection" command
	 * 
	 * @return a Connection object
	 */
	public Connection getActiveConnection() {
		return new Connection(Dispatch.get(this, "ActiveConnection")
				.toDispatch());
	}

	/**
	 * Sets the "ActiveConnection" object
	 * 
	 * @param ppvObject
	 *            the new connection
	 */
	public void setActiveConnection(Connection ppvObject) {
		Dispatch.put(this, "ActiveConnection", ppvObject);
	}

	/**
	 * 
	 * @return the results from "CommandText"
	 */
	public String getCommandText() {
		return Dispatch.get(this, "CommandText").toString();
	}

	/**
	 * 
	 * @param pbstr
	 *            the new "CommandText"
	 */
	public void setCommandText(String pbstr) {
		Dispatch.put(this, "CommandText", pbstr);
	}

	/**
	 * 
	 * @return the results of "CommandTimeout"
	 */
	public int getCommandTimeout() {
		return Dispatch.get(this, "CommandTimeout").getInt();
	}

	/**
	 * 
	 * @param plTimeout
	 *            the new "CommandTimeout"
	 */
	public void setCommandTimeout(int plTimeout) {
		Dispatch.put(this, "CommandTimeout", new Variant(plTimeout));
	}

	/**
	 * 
	 * @return results from "Prepared"
	 */
	public boolean getPrepared() {
		return Dispatch.get(this, "Prepared").getBoolean();
	}

	/**
	 * 
	 * @param pfPrepared
	 *            the new value for "Prepared"
	 */
	public void setPrepared(boolean pfPrepared) {
		Dispatch.put(this, "Prepared", new Variant(pfPrepared));
	}

	/**
	 * "Execute"s a command
	 * 
	 * @param RecordsAffected
	 * @param Parameters
	 * @param Options
	 * @return
	 */
	public Recordset Execute(Variant RecordsAffected, Variant Parameters,
			int Options) {
		return (Recordset) Dispatch.call(this, "Execute", RecordsAffected,
				Parameters, new Variant(Options)).toDispatch();
	}

	/**
	 * "Execute"s a command
	 * 
	 * @return
	 */
	public Recordset Execute() {
		Variant dummy = new Variant();
		return new Recordset(Dispatch.call(this, "Execute", dummy).toDispatch());
	}

	/**
	 * creates a parameter
	 * 
	 * @param Name
	 * @param Type
	 * @param Direction
	 * @param Size
	 * @param Value
	 * @return
	 */
	public Variant CreateParameter(String Name, int Type, int Direction,
			int Size, Variant Value) {
		return Dispatch.call(this, "CreateParameter", Name, new Variant(Type),
				new Variant(Direction), new Variant(Size), Value);
	}

	// need to wrap Parameters
	/**
	 * @return "Parameters"
	 */
	public Variant getParameters() {
		return Dispatch.get(this, "Parameters");
	}

	/**
	 * 
	 * @param plCmdType
	 *            new "CommandType"
	 */
	public void setCommandType(int plCmdType) {
		Dispatch.put(this, "CommandType", new Variant(plCmdType));
	}

	/**
	 * 
	 * @return current "CommandType"
	 */
	public int getCommandType() {
		return Dispatch.get(this, "CommandType").getInt();
	}

	/**
	 * 
	 * @return "Name"
	 */
	public String getName() {
		return Dispatch.get(this, "Name").toString();
	}

	/**
	 * 
	 * @param pbstrName
	 *            new "Name"
	 */
	public void setName(String pbstrName) {
		Dispatch.put(this, "Name", pbstrName);
	}

	/**
	 * 
	 * @return curent "State"
	 */
	public int getState() {
		return Dispatch.get(this, "State").getInt();
	}

	/**
	 * cancel whatever it is we're doing
	 */
	public void Cancel() {
		Dispatch.call(this, "Cancel");
	}
}
