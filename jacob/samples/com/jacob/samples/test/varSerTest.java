package com.jacob.samples.test;

import com.jacob.com.*;
import java.io.*;

class varSerTest 
{
  public static void main(String[] args) throws Exception
  {
    Variant vs1 = new Variant("hi");
    Variant vs2 = new Variant(123.456);

    FileOutputStream fos = new FileOutputStream("foo.foo");
    vs1.Save(fos);
    vs2.Save(fos);
    fos.close();

    Variant  vl1 = new Variant();
    Variant  vl2 = new Variant();
    FileInputStream fis = new FileInputStream("foo.foo");
    vl1.Load(fis);
    vl2.Load(fis);
    System.out.println(vl1);
    System.out.println(vl2);

    // same thing with serialization

    fos = new FileOutputStream("foo.ser");
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(vs1);
    oos.writeObject(vs2);
    oos.close();
    fos.close();


    fis = new FileInputStream("foo.ser");
    ObjectInputStream ois = new ObjectInputStream(fis);

    Variant vss1, vss2;

    vss1 = (Variant)ois.readObject();
    vss2 = (Variant)ois.readObject();
    ois.close();
    fis.close();

    System.out.println(vss1);
    System.out.println(vss2);
  }
}
