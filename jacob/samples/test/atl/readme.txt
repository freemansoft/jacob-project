This example demonstrates how to access multiple interfaces.

To run it, chdir to MultiFace\Debug and run:
regsvr32 MultiFace.dll

Then, run (in this dir):
java MultiFace

As you can see from MultiFace\MultiFace.idl - there are 3 interfaces.

By default JACOB attaches to the default interface.

The file MultiFace.java shows how to use the new Dispatch.QueryInterface
to get access to the other interfaces.
