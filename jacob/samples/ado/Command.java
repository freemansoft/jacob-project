package samples.ado;

import com.jacob.com.*;

public class Command extends Dispatch
{
  public Command()
  {
    super("ADODB.Command");
  }

	/**
	 * This constructor is used instead of a case operation to
	 * turn a Dispatch object into a wider object - it must exist
	 * in every wrapper class whose instances may be returned from
	 * method calls wrapped in VT_DISPATCH Variants.
	 * @param dispatchTarget
	 */
	public Command(Dispatch dispatchTarget)
	{
		super(dispatchTarget);
	}

  public Variant getProperties()
  {
    return this.getProperty("Properties");
  }

  public Connection getActiveConnection()
  {
    return new Connection(this.getPropertyAsDispatch("ActiveConnection"));
  }

  public void setActiveConnection(Connection ppvObject)
  {
    this.setProperty("ActiveConnection", ppvObject);
  }

  public String getCommandText()
  {
    return this.getPropertyAsString("CommandText");
  }

  public void setCommandText(String pbstr)
  {
    this.setProperty("CommandText", pbstr);
  }

  public int getCommandTimeout()
  {
    return this.getPropertyAsInt("CommandTimeout");
  }

  public void setCommandTimeout(int plTimeout)
  {
    this.setProperty("CommandTimeout", plTimeout);
  }

  public boolean getPrepared()
  {
     return this.getPropertyAsBoolean("Prepared");
  }

  public void setPrepared(boolean pfPrepared)
  {
    this.setProperty("Prepared", pfPrepared);
  }

  public Recordset Execute(Variant RecordsAffected, Variant Parameters, int Options)
  {
    return (Recordset)this.callGetDispatch("Execute", RecordsAffected, Parameters, new Variant(Options));
  }

  public Recordset Execute()
  {
	Variant dummy = new Variant();
	return new Recordset(this.callGetDispatch("Execute",dummy));
  }

  public Variant CreateParameter(String Name, int Type, int Direction, int Size, Variant Value)
  {
    return Dispatch.call(this, "CreateParameter", Name, new Variant(Type), new Variant(Direction), new Variant(Size), Value);
  }

  // need to wrap Parameters
  public Variant getParameters()
  {
    return this.getProperty("Parameters");
  }

  public void setCommandType(int plCmdType)
  {
    this.setProperty("CommandType", plCmdType);
  }

  public int getCommandType()
  {
    return this.getPropertyAsInt("CommandType");
  }

  public String getName()
  {
    return this.getPropertyAsString("Name");
  }

  public void setName(String pbstrName)
  {
    this.setProperty("Name", pbstrName);
  }

  public int getState()
  {
    return this.getPropertyAsInt("State");
  }

  public void Cancel()
  {
    this.call("Cancel");
  }
}
