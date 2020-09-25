package com.jacob.samples.ado;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Recordset extends Dispatch {
	public Recordset() {
		super("ADODB.Recordset");
	}

	/**
	 * This constructor is used instead of a case operation to turn a Dispatch
	 * object into a wider object - it must exist in every wrapper class whose
	 * instances may be returned from method calls wrapped in VT_DISPATCH
	 * Variants.
	 */
	public Recordset(Dispatch d) {
		super(d);
	}

	public Variant getProperties() {
		return Dispatch.get(this, "Properties");
	}

	public int getAbsolutePosition() {
		return Dispatch.get(this, "AbsolutePosition").getInt();
	}

	public void setAbsolutePosition(int pl) {
		Dispatch.put(this, "AbsolutePosition", new Variant(pl));
	}

	public Connection getActiveConnection() {
		return new Connection(Dispatch.get(this, "ActiveConnection")
				.toDispatch());
	}

	public void setActiveConnection(Connection ppvObject) {
		Dispatch.put(this, "ActiveConnection", ppvObject);
	}

	public void setActiveConnection(Variant ppvObject) {
		Dispatch.put(this, "ActiveConnection", ppvObject);
	}

	public boolean getBOF() {
		return Dispatch.get(this, "BOF").getBoolean();
	}

	public Variant getBookmark() {
		return Dispatch.get(this, "Bookmark");
	}

	public void setBookmark(Variant pvBookmark) {
		Dispatch.put(this, "Bookmark", pvBookmark);
	}

	public int getCacheSize() {
		return Dispatch.get(this, "CacheSize").getInt();
	}

	public void setCacheSize(int pl) {
		Dispatch.put(this, "CacheSize", new Variant(pl));
	}

	public int getCursorType() {
		return Dispatch.get(this, "CursorType").getInt();
	}

	public void setCursorType(int pl) {
		Dispatch.put(this, "CursorType", new Variant(pl));
	}

	public boolean getEOF() {
		return Dispatch.get(this, "EOF").getBoolean();
	}

	public Fields getFields() {
		return new Fields(Dispatch.get(this, "Fields").toDispatch());
	}

	public int getLockType() {
		return Dispatch.get(this, "LockType").getInt();
	}

	public void setLockType(int plLockType) {
		Dispatch.put(this, "LockType", new Variant(plLockType));
	}

	public int getMaxRecords() {
		return Dispatch.get(this, "MaxRecords").getInt();
	}

	public void setMaxRecords(int pl) {
		Dispatch.put(this, "MaxRecords", new Variant(pl));
	}

	public int getRecordCount() {
		return Dispatch.get(this, "RecordCount").getInt();
	}

	public void setSource(Object pvSource) {
		Dispatch.put(this, "Source", pvSource);
	}

	public void setSource(String pvSource) {
		Dispatch.put(this, "Source", pvSource);
	}

	public Variant getSource() {
		return Dispatch.get(this, "Source");
	}

	public void AddNew(Variant FieldList, Variant Values) {
		Dispatch.call(this, "AddNew", FieldList, Values);
	}

	public void CancelUpdate() {
		Dispatch.call(this, "CancelUpdate");
	}

	public void Close() {
		Dispatch.call(this, "Close");
	}

	public void Delete(int AffectRecords) {
		Dispatch.call(this, "Delete", new Variant(AffectRecords));
	}

	public Variant GetRows(int Rows, Variant Start, Variant Fields) {
		return Dispatch.call(this, "GetRows", new Variant(Rows), Start, Fields);
	}

	// get all rows
	public Variant GetRows() {
		return Dispatch.call(this, "GetRows");
	}

	public void Move(int NumRecords, Variant Start) {
		Dispatch.call(this, "Move", new Variant(NumRecords), Start);
	}

	public void MoveNext() {
		Dispatch.call(this, "MoveNext");
	}

	public void MovePrevious() {
		Dispatch.call(this, "MovePrevious");
	}

	public void MoveFirst() {
		Dispatch.call(this, "MoveFirst");
	}

	public void MoveLast() {
		Dispatch.call(this, "MoveLast");
	}

	public void Open(Variant Source, Variant ActiveConnection, int CursorType,
			int LockType, int Options) {
		Dispatch.call(this, "Open", Source, ActiveConnection, new Variant(
				CursorType), new Variant(LockType), new Variant(Options));
	}

	public void Open(Variant Source, Variant ActiveConnection) {
		Dispatch.call(this, "Open", Source, ActiveConnection);
	}

	public void Requery(int Options) {
		Dispatch.call(this, "Requery", new Variant(Options));
	}

	public void Update(Variant Fields, Variant Values) {
		Dispatch.call(this, "Update", Fields, Values);
	}

	public int getAbsolutePage() {
		return Dispatch.get(this, "AbsolutePage").getInt();
	}

	public void setAbsolutePage(int pl) {
		Dispatch.put(this, "AbsolutePage", new Variant(pl));
	}

	public int getEditMode() {
		return Dispatch.get(this, "EditMode").getInt();
	}

	public Variant getFilter() {
		return Dispatch.get(this, "Filter");
	}

	public void setFilter(Variant Criteria) {
		Dispatch.put(this, "Filter", Criteria);
	}

	public int getPageCount() {
		return Dispatch.get(this, "PageCount").getInt();
	}

	public int getPageSize() {
		return Dispatch.get(this, "PageSize").getInt();
	}

	public void setPageSize(int pl) {
		Dispatch.put(this, "PageSize", new Variant(pl));
	}

	public String getSort() {
		return Dispatch.get(this, "Sort").toString();
	}

	public void setSort(String Criteria) {
		Dispatch.put(this, "Sort", Criteria);
	}

	public int getStatus() {
		return Dispatch.get(this, "Status").getInt();
	}

	public int getState() {
		return Dispatch.get(this, "State").getInt();
	}

	public void UpdateBatch(int AffectRecords) {
		Dispatch.call(this, "UpdateBatch", new Variant(AffectRecords));
	}

	public void CancelBatch(int AffectRecords) {
		Dispatch.call(this, "CancelBatch", new Variant(AffectRecords));
	}

	public int getCursorLocation() {
		return Dispatch.get(this, "CursorLocation").getInt();
	}

	public void setCursorLocation(int pl) {
		Dispatch.put(this, "CursorLocation", new Variant(pl));
	}

	public Recordset NextRecordset(Variant RecordsAffected) {
		return new Recordset(Dispatch.call(this, "NextRecordset",
				RecordsAffected).toDispatch());
	}

	public boolean Supports(int CursorOptions) {
		return Dispatch.call(this, "Supports", new Variant(CursorOptions))
				.getBoolean();
	}

	public Variant getCollect(Variant Index) {
		return Dispatch.get(this, "Collect");
	}

	public void setCollect(Variant Index, Variant pvar) {
		Dispatch.call(this, "Collect", Index, pvar);
	}

	public int getMarshalOptions() {
		return Dispatch.get(this, "MarshalOptions").getInt();
	}

	public void setMarshalOptions(int pl) {
		Dispatch.put(this, "MarshalOptions", new Variant(pl));
	}

	public void Find(String Criteria, int SkipRecords, int SearchDirection,
			Variant Start) {
		Dispatch.call(this, "Find", Criteria, new Variant(SkipRecords),
				new Variant(SearchDirection), Start);
	}

	public void Cancel() {
		Dispatch.call(this, "Cancel");
	}

	public Variant getDataSource() {
		return Dispatch.get(this, "DataSource");
	}

	public void setDataSource(Variant ppunkDataSource) {
		Dispatch.put(this, "DataSource", ppunkDataSource);
	}

	public void Save(String FileName, int PersistFormat) {
		Dispatch.call(this, "Save", FileName, new Variant(PersistFormat));
	}

	public Variant getActiveCommand() {
		return Dispatch.get(this, "ActiveCommand");
	}

	public void setStayInSync(boolean pb) {
		Dispatch.put(this, "StayInSync", new Variant(pb));
	}

	public boolean getStayInSync() {
		return Dispatch.get(this, "StayInSync").getBoolean();
	}

	public String GetString(int StringFormat, int NumRows,
			String ColumnDelimeter, String RowDelimeter, String NullExpr) {
		return Dispatch.call(this, "GetString", new Variant(StringFormat),
				new Variant(NumRows), ColumnDelimeter, RowDelimeter, NullExpr)
				.toString();
	}

	public String getDataMember() {
		return Dispatch.get(this, "DataMember").toString();
	}

	public void setDataMember(String pl) {
		Dispatch.put(this, "DataMember", new Variant(pl));
	}

	public int CompareBookmarks(Variant Bookmark1, Variant Bookmark2) {
		return Dispatch.call(this, "CompareBookmarks", Bookmark1, Bookmark2)
				.getInt();
	}

	public Recordset Clone(int LockType) {
		return new Recordset(Dispatch
				.call(this, "Clone", new Variant(LockType)).toDispatch());
	}

	public void Resync(int AffectRecords, int ResyncValues) {
		Dispatch.call(this, "Resync", new Variant(AffectRecords), new Variant(
				ResyncValues));
	}

	public void Seek(Variant KeyValues, int SeekOption) {
		Dispatch.call(this, "Seek", KeyValues, new Variant(SeekOption));
	}

	public void setIndex(String pl) {
		Dispatch.put(this, "Index", new Variant(pl));
	}

	public String getIndex() {
		return Dispatch.get(this, "Index)").toString();
	}
}
