import com.jacob.com.*;

public class test
{
  public static void printRS(Recordset rs)
  {
    Fields fs = rs.getFields();

    for (int i=0;i<fs.getCount();i++)
    {
      System.out.print(fs.getItem(i).getName() + " ");
    }
    System.out.println("");

    rs.MoveFirst();
    while (!rs.getEOF())
    {
      for(int i=0;i<fs.getCount();i++)
      {
        Field f = fs.getItem(i);
        Variant v = f.getValue();
        System.out.print(v + " ");
      }
      System.out.println("");
      rs.MoveNext();
    }
  }

	// open a recordset directly
  public static void getRS(String con, String query)
	{
	  System.out.println("Recordset Open");
		Recordset rs = new Recordset();
		rs.Open(new Variant(query), new Variant(con));
	  printRS(rs);
	}

	// create connection and command objects and use them
	// to get a recordset
  public static void getCommand(String con, String query)
	{
	   System.out.println("Command+Connection -> Recordset");
	   Connection c = new Connection();
		 c.setConnectionString(con);
		 c.Open();
	   Command comm = new Command();
		 comm.setActiveConnection(c);
		 comm.setCommandType(CommandTypeEnum.adCmdText);
		 comm.setCommandText(query);
	   Recordset rs = comm.Execute();
	   printRS(rs);
		 c.Close();
	}

	public static void main(String[] args)
	{
		 String connectStr = "DRIVER=SQL Server;SERVER=DANADLER;UID=sa;PWD=;WSID=DANADLER;DATABASE=pubs";
		 String queryStr = "select * from authors";
		 getCommand(connectStr, queryStr);
		 getRS(connectStr, queryStr);
	}
}
