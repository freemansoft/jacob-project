package samples.ado;

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
	    super(d);
	}

  // need to wrap Properties
  public Variant getProperties()
  {
    return this.getProperty("Properties");
  }

  public String getConnectionString()
  {
    return this.getPropertyAsString("ConnectionString");
  }

  public void setConnectionString(String pbstr)
  {
    this.setProperty("ConnectionString", pbstr);
  }

  public int getCommandTimeout()
  {
    return this.getPropertyAsInt("CommandTimeout");
  }

  public void setCommandTimeout(int plTimeout)
  {
    this.setProperty("CommandTimeout", new Variant(plTimeout));
  }

  public int getConnectionTimeout()
  {
    return this.getPropertyAsInt("ConnectionTimeout");
  }

  public void setConnectionTimeout(int plTimeout)
  {
    this.setProperty("ConnectionTimeout", new Variant(plTimeout));
  }

  public String getVersion()
  {
    return this.getPropertyAsString("Version");
  }

  public void Close()
  {
    this.call("Close");
  }

  // how to deal with RecordsAffected being output?
  public Variant Execute(String CommandText, Variant RecordsAffected, int Options)
  {
    return Dispatch.call(this, CommandText, RecordsAffected, new Variant(Options));
  }

  public int BeginTrans()
  {
    return this.call("BeginTrans").toInt();
  }

  public void CommitTrans()
  {
    this.call("CommitTrans");
  }

  public void RollbackTrans()
  {
    this.call("RollbackTrans");
  }

  public void Open(String ConnectionString, String UserID, String Password, int Options)
  {
    Dispatch.call(this, "Open", ConnectionString, UserID, Password, new Variant(Options));
  }

  public void Open()
  {
    this.call("Open");
  }

  public Variant getErrors()
  {
    return this.getProperty("Errors");
  }

  public String getDefaultDatabase()
  {
    return this.getPropertyAsString("DefaultDatabase");
  }

  public void setDefaultDatabase(String pbstr)
  {
    this.setProperty("DefaultDatabase", pbstr);
  }

  public int getIsolationLevel()
  {
    return this.getPropertyAsInt("IsolationLevel");
  }

  public void setIsolationLevel(int Level)
  {
    this.setProperty("IsolationLevel", new Variant(Level));
  }

  public int getAttributes()
  {
    return this.getPropertyAsInt("Attributes");
  }

  public void setAttributes(int plAttr)
  {
    this.setProperty("Attributes", new Variant(plAttr));
  }

  public int getCursorLocation()
  {
    return this.getPropertyAsInt("CursorLocation");
  }

  public void setCursorLocation(int plCursorLoc)
  {
    this.setProperty("CursorLocation", new Variant(plCursorLoc));
  }

  public int getMode()
  {
    return this.getPropertyAsInt("Mode");
  }

  public void setMode(int plMode)
  {
    this.setProperty("Mode", new Variant(plMode));
  }

  public String getProvider()
  {
    return this.getPropertyAsString("Provider");
  }

  public void setProvider(String pbstr)
  {
    this.setProperty("Provider", pbstr);
  }

  public int getState()
  {
    return this.getPropertyAsInt("State");
  }

  public Variant OpenSchema(int Schema, Variant Restrictions, Variant SchemaID)
  {
    return Dispatch.call(this, "OpenSchema", 
            new Variant(Schema), 
            Restrictions, 
            SchemaID);
  }

  public void Cancel()
  {
    this.call("Cancel");
  }
}
