import com.jacob.com.*;

public class Connection extends Dispatch
{
  public Connection()
  {
    super("ADODB.Connection");
  }

	/**
	 * This constructor is used instead of a case operation to
	 * turn a Dispatch object into a wider object - it must exist
	 * in every wrapper class whose instances may be returned from
	 * method calls wrapped in VT_DISPATCH Variants.
	 */
	public Connection(Dispatch d)
	{
		// take over the IDispatch pointer
	  m_pDispatch = d.m_pDispatch;
		// null out the input's pointer
		d.m_pDispatch = 0;
	}

  // need to wrap Properties
  public Variant getProperties()
  {
    return Dispatch.get(this, "Properties");
  }

  public String getConnectionString()
  {
    return Dispatch.get(this, "ConnectionString").toString();
  }

  public void setConnectionString(String pbstr)
  {
    Dispatch.put(this, "ConnectionString", pbstr);
  }

  public int getCommandTimeout()
  {
    return Dispatch.get(this, "CommandTimeout").toInt();
  }

  public void setCommandTimeout(int plTimeout)
  {
    Dispatch.put(this, "CommandTimeout", new Variant(plTimeout));
  }

  public int getConnectionTimeout()
  {
    return Dispatch.get(this, "ConnectionTimeout").toInt();
  }

  public void setConnectionTimeout(int plTimeout)
  {
    Dispatch.put(this, "ConnectionTimeout", new Variant(plTimeout));
  }

  public String getVersion()
  {
    return Dispatch.get(this, "Version").toString();
  }

  public void Close()
  {
    Dispatch.call(this, "Close");
  }

  // how to deal with RecordsAffected being output?
  public Variant Execute(String CommandText, Variant RecordsAffected, int Options)
  {
    return Dispatch.call(this, CommandText, RecordsAffected, new Variant(Options));
  }

  public int BeginTrans()
  {
    return Dispatch.call(this, "BeginTrans").toInt();
  }

  public void CommitTrans()
  {
    Dispatch.call(this, "CommitTrans");
  }

  public void RollbackTrans()
  {
    Dispatch.call(this, "RollbackTrans");
  }

  public void Open(String ConnectionString, String UserID, String Password, int Options)
  {
    Dispatch.call(this, "Open", ConnectionString, UserID, Password, new Variant(Options));
  }

  public void Open()
  {
    Dispatch.call(this, "Open");
  }

  public Variant getErrors()
  {
    return Dispatch.get(this, "Errors");
  }

  public String getDefaultDatabase()
  {
    return Dispatch.get(this, "DefaultDatabase").toString();
  }

  public void setDefaultDatabase(String pbstr)
  {
    Dispatch.put(this, "DefaultDatabase", pbstr);
  }

  public int getIsolationLevel()
  {
    return Dispatch.get(this, "IsolationLevel").toInt();
  }

  public void setIsolationLevel(int Level)
  {
    Dispatch.put(this, "IsolationLevel", new Variant(Level));
  }

  public int getAttributes()
  {
    return Dispatch.get(this, "Attributes").toInt();
  }

  public void setAttributes(int plAttr)
  {
    Dispatch.put(this, "Attributes", new Variant(plAttr));
  }

  public int getCursorLocation()
  {
    return Dispatch.get(this, "CursorLocation").toInt();
  }

  public void setCursorLocation(int plCursorLoc)
  {
    Dispatch.put(this, "CursorLocation", new Variant(plCursorLoc));
  }

  public int getMode()
  {
    return Dispatch.get(this, "Mode").toInt();
  }

  public void setMode(int plMode)
  {
    Dispatch.put(this, "Mode", new Variant(plMode));
  }

  public String getProvider()
  {
    return Dispatch.get(this, "Provider").toString();
  }

  public void setProvider(String pbstr)
  {
    Dispatch.put(this, "Provider", pbstr);
  }

  public int getState()
  {
    return Dispatch.get(this, "State").toInt();
  }

  public Variant OpenSchema(int Schema, Variant Restrictions, Variant SchemaID)
  {
    return Dispatch.call(this, "OpenSchema", new Variant(Schema), Restrictions, SchemaID);
  }

  public void Cancel()
  {
    Dispatch.call(this, "Cancel");
  }
}
