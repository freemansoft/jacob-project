import com.ms.com.*;
import com.ms.wfc.data.*;

// an ms-only version of test.java
public class testms
{
  public static void printRS(Recordset rs)
  {
    Fields fs = rs.getFields();

    for (int i=0;i<fs.getCount();i++)
    {
      System.out.print(fs.getItem(i).getName() + " ");
    }
    System.out.println("");

    rs.moveFirst();
    while (!rs.getEOF())
    {
      for(int i=0;i<fs.getCount();i++)
      {
        Field f = fs.getItem(i);
        Variant v = f.getValue();
        System.out.print(v + " ");
      }
      System.out.println("");
      rs.moveNext();
    }
  }

	// open a recordset directly
  public static void getRS(String con, String query)
	{
	  System.out.println("Recordset Open");
		Recordset rs = new Recordset();
		rs.open(new Variant(query), new Variant(con));
	  printRS(rs);
	}

	// create connection and command objects and use them
	// to get a recordset
  public static void getCommand(String con, String query)
	{
	   System.out.println("Command+Connection -> Recordset");
	   Connection c = new Connection();
		 c.setConnectionString(con);
		 c.open();
	   Command comm = new Command();
		 comm.setActiveConnection(c);
		 comm.setCommandType(AdoEnums.CommandType.TEXT);
		 comm.setCommandText(query);
	   Recordset rs = comm.execute();
	   printRS(rs);
		 c.close();
	}

	public static void main(String[] args)
	{
		 String connectStr = "DRIVER=SQL Server;SERVER=DANADLER;UID=sa;PWD=;WSID=DANADLER;DATABASE=pubs";
		 String queryStr = "select * from authors";
		 getCommand(connectStr, queryStr);
		 getRS(connectStr, queryStr);
	}
}
