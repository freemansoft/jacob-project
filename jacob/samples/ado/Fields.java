package samples.ado;
import com.jacob.com.*;

public class Fields extends Dispatch
{
	/**
	 * This constructor is used instead of a case operation to
	 * turn a Dispatch object into a wider object - it must exist
	 * in every wrapper class whose instances may be returned from
	 * method calls wrapped in VT_DISPATCH Variants.
	 */
	public Fields(Dispatch d)
	{
		super(d);
	}

  public int getCount()
	{
	  return getPropertyAsInt("Count");
	}

  public Variant _NewEnum()
	{
	  return call("_NewEnum");
	}

  public void Refresh()
	{
	  call("Refresh");
	}

  public Field getItem(int Index)
	{
	  return new Field(call("Item", new Variant(Index)).toDispatch());
	}

  public void Append(String Name, int Type, int DefinedSize, int Attrib)
	{
	  DispatchNative.call(this,"Append", Name, new Variant(Type),
		    new Variant(DefinedSize), new Variant(Attrib));
	}

  public void Delete(Variant Index)
	{
	  call("Delete", Index);
	}

}
