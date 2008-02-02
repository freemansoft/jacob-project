package com.jacob.samples.ado;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Field extends Dispatch {
	/**
	 * This constructor is used instead of a case operation to turn a Dispatch
	 * object into a wider object - it must exist in every wrapper class whose
	 * instances may be returned from method calls wrapped in VT_DISPATCH
	 * Variants.
	 */
	public Field(Dispatch d) {
		super(d);
	}

	public Variant getProperties() {
		return Dispatch.get(this, "Properties");
	}

	public int getActualSize() {
		return Dispatch.get(this, "ActualSize").getInt();
	}

	public int getAttributes() {
		return Dispatch.get(this, "Attributes").getInt();
	}

	public int getDefinedSize() {
		return Dispatch.get(this, "DefinedSize").getInt();
	}

	public String getName() {
		return Dispatch.get(this, "Name").toString();
	}

	public int getType() {
		return Dispatch.get(this, "Type").getInt();
	}

	public Variant getValue() {
		return Dispatch.get(this, "Value");
	}

	public void setValue(Variant pvar) {
		Dispatch.put(this, "Value", pvar);
	}

	public byte getPrecision() {
		return Dispatch.get(this, "Precision").getByte();
	}

	public byte getNumericScale() {
		return Dispatch.get(this, "NumericScale").getByte();
	}

	public void AppendChunk(Variant Data) {
		Dispatch.call(this, "AppendChunk", Data);
	}

	public Variant GetChunk(int Length) {
		return Dispatch.call(this, "GetChunk", new Variant(Length));
	}

	public Variant getOriginalValue() {
		return Dispatch.get(this, "OriginalValue");
	}

	public Variant getUnderlyingValue() {
		return Dispatch.get(this, "UnderlyingValue");
	}

	public Variant getDataFormat() {
		return Dispatch.get(this, "DataFormat");
	}

	public void setDataFormat(Variant ppiDF) {
		Dispatch.put(this, "DataFormat", ppiDF);
	}

	public void setPrecision(byte pb) {
		Dispatch.put(this, "Precision", new Variant(pb));
	}

	public void setNumericScale(byte pb) {
		Dispatch.put(this, "NumericScale", new Variant(pb));
	}

	public void setType(int pDataType) {
		Dispatch.put(this, "Type", new Variant(pDataType));
	}

	public void setDefinedSize(int pl) {
		Dispatch.put(this, "DefinedSize", new Variant(pl));
	}

	public void setAttributes(int pl) {
		Dispatch.put(this, "Attributes", new Variant(pl));
	}

}
