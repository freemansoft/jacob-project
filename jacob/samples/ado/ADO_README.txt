- ADO Wrapper for JACOB - Copyright 1999, Dan Adler

This sample shows how to generate more strongly typed wrapper classes
for the JACOB automation classes. These are pure java classes which
extend com.jacob.com.Dispatch and delegate all the methods to the
unedrlying IDispatch pointer. This methodology is similar to the way
MFC does automation wrappers, rather than using the @com directives
to invisibly delegate the calls, as the Microsoft VM does.

The ADO wrappers in this directory are not a part of the JACOB
distribution, however, they demonstrate the preferred way to create
wrappers around the core functionality. The wrappers included here are
not a complete set, but they could easily be extended to provide all
the functionality of the com.ms.wfc.data classes.

The code in test.java demonstrates two ways to get a Recordset
from SQL Server. In this case, I query for the authors in the 'pubs'
database once by opening a Recordset object directly, and once by
using the Command and Connection objects. The same code, using the WFC
wrappers can be found in ms\testms.java in case you want to compare
the performace. You can run the test.java demo in the MS VM as well.

The constructor of the wrapper is used to create an instance.
For example, the user can write:

  Connection c = new Connection();

The code for the Connection constructor is shown here:

  public Connection()
  {
    super("ADODB.Connection");
  }

it simply delegates to the com.jacob.com.Dispatch constructor which
takes a ProgID.

Since I don't have a tool like JACTIVEX yet to create the wrappers
automatically from the type library, I created them by hand by using
the JACTIVEX'ed version as a starting point, and replacing the @com
calls with delegated calls to JACOB classes. A simple PERL program
could probably be used to automate this step.

In order to return strongly typed wrappers from method calls, I had to
create a special constructor which constructs the wrapper class instance
and copies over the IDispatch pointer. This is because I can't cast a
java Dispatch object to a super class object.

For example, the Command class has a method like this:

  public Connection getActiveConnection();

Ideally, I would like the wrapper code to say:

  public Connection getActiveConnection()
  {
    // this doesn't work
    return (Connection)Dispatch.get(this, "ActiveConnection").toDispatch());
  }

Thereby wrapping the returned Dispatch pointer in a Connection object.
But, since I can't cast in this way, I use the following construct:

  public Connection getActiveConnection()
  {
    // this works
    return new Connection(Dispatch.get(this, "ActiveConnection").toDispatch());
  }

Which uses a special constructor inserted into the Connection class:

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

I have to add this constructor to any class whose instances I want
to return from wrapped calls.

