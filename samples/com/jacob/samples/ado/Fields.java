package com.jacob.samples.ado;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Fields extends Dispatch {
	/**
	 * This constructor is used instead of a case operation to turn a Dispatch
	 * object into a wider object - it must exist in every wrapper class whose
	 * instances may be returned from method calls wrapped in VT_DISPATCH
	 * Variants.
	 */
	public Fields(Dispatch d) {
		super(d);
	}

	public int getCount() {
		return Dispatch.get(this, "Count").getInt();
	}

	public Variant _NewEnum() {
		return Dispatch.call(this, "_NewEnum");
	}

	public void Refresh() {
		Dispatch.call(this, "Refresh");
	}

	public Field getItem(int Index) {
		return new Field(Dispatch.call(this, "Item", new Variant(Index))
				.toDispatch());
	}

	public void Append(String Name, int Type, int DefinedSize, int Attrib) {
		Dispatch.call(this, "Append", Name, new Variant(Type), new Variant(
				DefinedSize), new Variant(Attrib));
	}

	public void Delete(Variant Index) {
		Dispatch.call(this, "Delete", Index);
	}

}
