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
	 */
	public Command(Dispatch d)
	{
		// take over the IDispatch pointer
	  m_pDispatch = d.m_pDispatch;
		// null out the input's pointer
		d.m_pDispatch = 0;
	}

  public Variant getProperties()
  {
    return Dispatch.get(this, "Properties");
  }

  public Connection getActiveConnection()
  {
    return new Connection(Dispatch.get(this, "ActiveConnection").toDispatch());
  }

  public void setActiveConnection(Connection ppvObject)
  {
    Dispatch.put(this, "ActiveConnection", ppvObject);
  }

  public String getCommandText()
  {
    return Dispatch.get(this, "CommandText").toString();
  }

  public void setCommandText(String pbstr)
  {
    Dispatch.put(this, "CommandText", pbstr);
  }

  public int getCommandTimeout()
  {
    return Dispatch.get(this, "CommandTimeout").toInt();
  }

  public void setCommandTimeout(int plTimeout)
  {
    Dispatch.put(this, "CommandTimeout", new Variant(plTimeout));
  }

  public boolean getPrepared()
  {
     return Dispatch.get(this, "Prepared").toBoolean();
  }

  public void setPrepared(boolean pfPrepared)
  {
    Dispatch.put(this, "Prepared", new Variant(pfPrepared));
  }

  public Recordset Execute(Variant RecordsAffected, Variant Parameters, int Options)
  {
    return (Recordset)Dispatch.call(this, "Execute", RecordsAffected, Parameters, new Variant(Options)).toDispatch();
  }

  public Recordset Execute()
  {
	  Variant dummy = new Variant();
    return new Recordset(Dispatch.call(this, "Execute", dummy).toDispatch());
  }

  public Variant CreateParameter(String Name, int Type, int Direction, int Size, Variant Value)
  {
    return Dispatch.call(this, "CreateParameter", Name, new Variant(Type), new Variant(Direction), new Variant(Size), Value);
  }

  // need to wrap Parameters
  public Variant getParameters()
  {
    return Dispatch.get(this, "Parameters");
  }

  public void setCommandType(int plCmdType)
  {
    Dispatch.put(this, "CommandType", new Variant(plCmdType));
  }

  public int getCommandType()
  {
    return Dispatch.get(this, "CommandType").toInt();
  }

  public String getName()
  {
    return Dispatch.get(this, "Name").toString();
  }

  public void setName(String pbstrName)
  {
    Dispatch.put(this, "Name", pbstrName);
  }

  public int getState()
  {
    return Dispatch.get(this, "State").toInt();
  }

  public void Cancel()
  {
    Dispatch.call(this, "Cancel");
  }
}
