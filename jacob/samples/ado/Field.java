package samples.ado;
import com.jacob.com.*;

public class Field extends Dispatch
{
	/**
	 * This constructor is used instead of a case operation to
	 * turn a Dispatch object into a wider object - it must exist
	 * in every wrapper class whose instances may be returned from
	 * method calls wrapped in VT_DISPATCH Variants.
	 */
	public Field(Dispatch d)
	{
		super(d);
	}

  public Variant getProperties()
  {
    return this.getProperty("Properties");
  }

  public int getActualSize()
	{
	  return this.getPropertyAsInt("ActualSize");
	}

  public int getAttributes()
	{
	  return this.getPropertyAsInt("Attributes");
	}

  public int getDefinedSize()
	{
	  return this.getPropertyAsInt("DefinedSize");
	}

  public String getName()
	{
	  return this.getPropertyAsString("Name");
	}

  public int getType()
	{
	  return this.getPropertyAsInt("Type");
	}

  public Variant getValue()
	{
	  return this.getProperty("Value");
	}

  public void setValue(Variant pvar)
	{
	  this.setProperty("Value", pvar);
	}

  public byte getPrecision()
	{
	  return this.getPropertyAsByte("Precision");
	}

  public byte getNumericScale()
	{
	  return this.getPropertyAsByte("NumericScale");
	}

  public void AppendChunk(Variant Data)
	{
	  this.call("AppendChunk", Data);
	}

  public Variant GetChunk(int Length)
	{
	  return this.call("GetChunk", new Variant(Length));
	}

  public Variant getOriginalValue()
	{
	  return this.getProperty("OriginalValue");
	}

  public Variant getUnderlyingValue()
	{
	  return this.getProperty("UnderlyingValue");
	}

  public Variant getDataFormat()
	{
	  return this.getProperty("DataFormat");
	}

  public void setDataFormat(Variant ppiDF)
	{
	  this.setProperty("DataFormat", ppiDF);
	}

  public void setPrecision(byte pb)
	{
	  this.setProperty("Precision", pb);
	}

  public void setNumericScale(byte pb)
	{
	  this.setProperty("NumericScale", pb);
	}

  public void setType(int pDataType)
	{
	  this.setProperty("Type", pDataType);
	}

  public void setDefinedSize(int pl)
	{
	  this.setProperty("DefinedSize", pl);
	}

  public void setAttributes(int pl)
	{
	  this.setProperty("Attributes", pl);
	}

}
