package samples.test;

import com.jacob.com.*;

class sa_test
{
  public static void main(String[] args)
  {
    System.runFinalizersOnExit(true);
    SafeArray sa = new SafeArray(Variant.VariantVariant, 3);

    sa.fromShortArray(new short[] {1,2,3});
    System.out.println("sa short="+sa);
    int[] ai = sa.toIntArray();
    for(int i=0;i<ai.length;i++) {
      System.out.println("toInt="+ai[i]);
    }
    double[] ad = sa.toDoubleArray();
    for(int i=0;i<ad.length;i++) {
      System.out.println("toDouble="+ad[i]);
    }
    sa.fromIntArray(new int[] {100000,200000,300000});
    System.out.println("sa int="+sa);
    ai = sa.toIntArray();
    for(int i=0;i<ai.length;i++) {
      System.out.println("toInt="+ai[i]);
    }
    ad = sa.toDoubleArray();
    for(int i=0;i<ad.length;i++) {
      System.out.println("toDouble="+ad[i]);
    }
    Variant av[] = sa.toVariantArray();
    for(int i=0;i<av.length;i++) {
      System.out.println("toVariant="+av[i]);
    }
    sa.fromDoubleArray(new double[] {1.5,2.5,3.5});
    System.out.println("sa double="+sa);
    sa.fromFloatArray(new float[] {1.5F,2.5F,3.5F});
    System.out.println("sa float="+sa);
    sa.fromBooleanArray(new boolean[] {true, false, true, false});
    System.out.println("sa bool="+sa);
    av = sa.toVariantArray();
    for(int i=0;i<av.length;i++) {
      System.out.println("toVariant="+av[i]);
    }
    sa.fromCharArray(new char[] {'a','b','c','d'});
    System.out.println("sa char="+sa);
    sa.fromStringArray(new String[] {"hello", "from", "java", "com"});
    System.out.println("sa string="+sa);
    av = sa.toVariantArray();
    for(int i=0;i<av.length;i++) {
      System.out.println("toVariant="+av[i]);
    }
    sa.fromVariantArray(new Variant[] {
      new Variant(1), new Variant(2.3), new Variant("hi")});
    System.out.println("sa variant="+sa);
  }
}
