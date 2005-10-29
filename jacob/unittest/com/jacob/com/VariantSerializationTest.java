package com.jacob.com;

import java.io.*;

/**
 * Verifies serialization works for variants.
 * Variant serialization is BROKEN and has been since 1.7
 * <pre>-Djava.library.path=d:/jacob/release</pre>
 */
class VariantSerializationTest {

	static Variant vs1 = new Variant("hi");
	static Variant vs2 = new Variant(123.456);

	public static void main(String[] args) throws Exception {
		doJustSerialization();
		compareVariantBytes();
	}

	private static void compareVariantBytes() throws Exception{
		System.out.println("compareVariantBytes");
		Variant var1 = new Variant("hello");
		Variant var2 = new Variant("hello");
		byte[] var1Bytes = var1.SerializationWriteToBytes();
		byte[] var2Bytes = var2.SerializationWriteToBytes();
		for ( int i = 0 ; i < var1Bytes.length; i++){
			if (var1Bytes[i]!=var2Bytes[i]){
				System.out.println("variant strings differ at position "+i);
				return;
			}
		}
		System.out.println("two strings return identical serialization data");
	}
	
	private static void doJustSerialization() throws Exception {
		System.out.println("doJustSerialization");
		// same thing with serialization
		FileOutputStream fos;
		FileInputStream fis;
		fos = new FileOutputStream("foo.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(vs1);
		//oos.writeObject(vs2);
		oos.close();
		fos.close();

		fis = new FileInputStream("foo.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);

		Variant vss1 = null;
		Variant vss2 = null;

		vss1 = (Variant) ois.readObject();
		//vss2 = (Variant) ois.readObject();
		ois.close();
		fis.close();

		System.out.println(vss1);
		System.out.println(vss2);
	}
}
