package samples.ado;
import com.jacob.com.*;

public class Recordset extends Dispatch
{
  public Recordset()
  {
    super("ADODB.Recordset");
  }

	/**
	 * This constructor is used instead of a case operation to
	 * turn a Dispatch object into a wider object - it must exist
	 * in every wrapper class whose instances may be returned from
	 * method calls wrapped in VT_DISPATCH Variants.
	 */
	public Recordset(Dispatch d)
	{
		super(d);
	}

  public Variant getProperties()
  {
    return this.getProperty("Properties");
  }

  public int getAbsolutePosition()
	{
    return this.getProperty("AbsolutePosition").toInt();
	}

  public void setAbsolutePosition(int pl)
	{
	  setProperty("AbsolutePosition", pl);
	}

  public Connection getActiveConnection()
  {
    return new Connection(this.getPropertyAsDispatch("ActiveConnection"));
  }

  public void setActiveConnection(Connection ppvObject)
  {
    setProperty("ActiveConnection", ppvObject);
  }

  public void setActiveConnection(Variant ppvObject)
  {
    setProperty("ActiveConnection", ppvObject);
  }

  public boolean getBOF()
	{
	  return this.getPropertyAsBoolean("BOF");
	}

  public Variant getBookmark()
	{
	  return this.getProperty("Bookmark");
	}

  public void setBookmark(Variant pvBookmark)
	{
	  setProperty("Bookmark", pvBookmark);
	}

  public int getCacheSize()
	{
	  return this.getPropertyAsInt("CacheSize");
	}

  public void setCacheSize(int pl)
	{
	  setProperty("CacheSize", pl);
	}

  public int getCursorType()
	{
	  return this.getPropertyAsInt("CursorType");
	}

  public void setCursorType(int pl)
	{
	  setProperty("CursorType", pl);
	}

  public boolean getEOF()
	{
	  return this.getPropertyAsBoolean("EOF");
  }

  public Fields getFields()
	{
	  return new Fields(this.getPropertyAsDispatch("Fields"));
	}

  public int getLockType()
	{
	  return this.getPropertyAsInt("LockType");
	}

  public void setLockType(int plLockType)
	{
	  setProperty("LockType", plLockType);
	}

  public int getMaxRecords()
	{
	  return this.getPropertyAsInt("MaxRecords");
	}

  public void setMaxRecords(int pl)
	{
	  setProperty("MaxRecords", pl);
	}

  public int getRecordCount()
	{
	  return this.getPropertyAsInt("RecordCount");
	}


  public void setSource(String pvSource)
	{
      setProperty("Source", pvSource);
	}

  public Variant getSource()
	{
	  return this.getProperty("Source");
	}

  public void AddNew(Variant FieldList, Variant Values)
	{
	  call("AddNew", FieldList, Values);
	}

  public void CancelUpdate()
	{
	  call("CancelUpdate");
	}

  public void Close()
	{
	  call("Close");
	}

  public void Delete(int AffectRecords)
	{
	  call("Delete", new Variant(AffectRecords));
	}

  public Variant GetRows(int Rows, Variant Start, Variant Fields)
	{
	  return Dispatch.call(this,"GetRows", new Variant(Rows), Start, Fields); 
	}

	// get all rows
  public Variant GetRows()
	{
	  return call("GetRows");
	}

  public void Move(int NumRecords, Variant Start)
	{
      call("Move", new Variant(NumRecords), Start);
	}

  public void MoveNext()
	{
	  call("MoveNext");
	}

  public void MovePrevious()
	{
	  call("MovePrevious");
	}

  public void MoveFirst()
	{
      call("MoveFirst");
	}

  public void MoveLast()
	{
	  call("MoveLast");
	}

  public void Open(Variant Source, Variant ActiveConnection, int CursorType, int LockType, int Options)
	{
	  Dispatch.call(this,"Open", 
	          Source, 
	          ActiveConnection, 
	          new Variant(CursorType), 
	          new Variant(LockType),
	          new Variant(Options));
	}

  public void Open(Variant Source, Variant ActiveConnection)
	{
	  call("Open", Source, ActiveConnection);
	}

  public void Requery(int Options)
	{
	  call("Requery", new Variant(Options));
	}

  public void Update(Variant Fields, Variant Values)
	{
	  call("Update", Fields, Values);
	}

  public int getAbsolutePage()
	{
	  return this.getPropertyAsInt("AbsolutePage");
	}

  public void setAbsolutePage(int pl)
	{
	  this.setProperty("AbsolutePage", pl);
	}

  public int getEditMode()
	{
	  return this.getPropertyAsInt("EditMode");
	}

  public Variant getFilter()
	{
	  return this.getProperty("Filter");
	}

  public void setFilter(Variant Criteria)
	{
	  this.setProperty("Filter", Criteria);
	}

  public int getPageCount()
	{
	  return this.getPropertyAsInt("PageCount");
	}

  public int getPageSize()
	{
	  return this.getPropertyAsInt("PageSize");
	}

  public void setPageSize(int pl)
	{
	  this.setProperty("PageSize", pl);
	}

  public String getSort()
	{
	  return this.getPropertyAsString("Sort");
	}

  public void setSort(String Criteria)
	{
	  this.setProperty("Sort", Criteria);
	}

  public int getStatus()
	{
	  return this.getPropertyAsInt("Status");
	}

  public int getState()
	{
	  return this.getPropertyAsInt("State");
	}

  public void UpdateBatch(int AffectRecords)
	{
	  call("UpdateBatch", new Variant(AffectRecords));
	}

  public void CancelBatch(int AffectRecords)
	{
	  call("CancelBatch", new Variant(AffectRecords));
	}

  public int getCursorLocation()
	{
	  return this.getPropertyAsInt("CursorLocation");
	}

  public void setCursorLocation(int pl)
	{
	  this.setProperty("CursorLocation", pl);
	}

  public Recordset NextRecordset(Variant RecordsAffected)
	{
	  return new Recordset(call("NextRecordset", RecordsAffected).toDispatch());
	}

  public boolean Supports(int CursorOptions)
	{
	  return call("Supports", new Variant(CursorOptions)).toBoolean();
	}

  public Variant getCollect(Variant Index)
	{
	  return this.getProperty("Collect");
	}

  public void setCollect(Variant Index, Variant pvar)
	{
	  call("Collect", Index, pvar);
	}

  public int getMarshalOptions()
	{
	  return this.getPropertyAsInt("MarshalOptions");
	}

  public void setMarshalOptions(int pl)
	{
	  this.setProperty("MarshalOptions", pl);
  }

  public void Find(String Criteria, 
          int SkipRecords, int SearchDirection, 
          Variant Start)
	{
	  Dispatch.call(this,"Find", Criteria, 
	          new Variant(SkipRecords), 
	          new Variant(SearchDirection), 
	          Start);
	}

  public void Cancel()
	{
	  call("Cancel");
	}

  public Variant getDataSource()
	{
	  return this.getProperty("DataSource");
	}

  public void setDataSource(Variant ppunkDataSource)
  {
	  this.setProperty("DataSource", ppunkDataSource);
	}

  public void Save(String FileName, int PersistFormat)
	{
	  call("Save", FileName, PersistFormat);
	}

  public Variant getActiveCommand()
	{
	  return this.getProperty("ActiveCommand");
	}

  public void setStayInSync(boolean pb)
	{
	  this.setProperty("StayInSync", pb);
	}

  public boolean getStayInSync()
	{
	  return this.getPropertyAsBoolean("StayInSync");
	}

  public String GetString(int StringFormat, int NumRows, String ColumnDelimeter, String RowDelimeter, String NullExpr)
	{
	  return Dispatch.call(this,"GetString", new Variant(StringFormat),
		    new Variant(NumRows), ColumnDelimeter, RowDelimeter, NullExpr).toString();
	}

  public String getDataMember()
	{
	  return this.getPropertyAsString("DataMember");
	}

  public void setDataMember(String pl)
	{
	  this.setProperty("DataMember", pl);
	}

  public int CompareBookmarks(Variant Bookmark1, Variant Bookmark2)
	{
	  return call("CompareBookmarks", Bookmark1, Bookmark2).toInt();
	}

  public Recordset Clone(int LockType)
	{
	  return new Recordset(call("Clone", 
		           new Variant(LockType)).toDispatch());
	}

  public void Resync(int AffectRecords, int ResyncValues)
	{
	  call("Resync", AffectRecords, ResyncValues);
	}

  public void Seek(Variant KeyValues, int SeekOption)
	{
	  call("Seek", KeyValues, new Variant(SeekOption));
	}

  public void setIndex(String pl)
	{
	  setProperty("Index", pl);
	}

  public String getIndex()
	{
	  return this.getPropertyAsString("Index");
	}
}
