JDK = c:\j2sdk1.4.2_07
MSDEVDIR = c:\\"Microsoft Visual Studio"\VC98

JDK_INC = -I$(JDK)\include -I$(JDK)\include\win32
JDK_LIB = $(JDK)\lib\jvm.lib
MS_INC = -I$(MSDEVDIR)\Include -I$(MSDEVDIR)\ATL\Include
SYS_LIB = oleaut32.lib ole32.lib uuid.lib kernel32.lib shell32.lib user32.lib
OPT = /O2
CC = $(MSDEVDIR)\bin\cl $(OPT) $(JDK_INC) $(MS_INC)

OBJFILES = stdafx.obj util.obj EventProxy.obj Variant.obj Dispatch.obj SafeArray.obj DispatchEvents.obj ComThread.obj EnumVariant.obj STA.obj DispatchProxy.obj

all: jacob.dll

jacob.dll: $(OBJFILES)
	$(MSDEVDIR)\bin\link /dll /out:jacob.dll $(OBJFILES) $(JDK_LIB) $(SYS_LIB)

stdafx.obj: stdafx.cpp *.h
  $(CC) -c stdafx.cpp
  
util.obj: util.cpp *.h
  $(CC) -c util.cpp

EventProxy.obj: EventProxy.cpp *.h
  $(CC) -c EventProxy.cpp

Variant.obj: Variant.cpp *.h
  $(CC) -c Variant.cpp

Dispatch.obj: Dispatch.cpp *.h
  $(CC) -c Dispatch.cpp

SafeArray.obj: SafeArray.cpp *.h
  $(CC) -c SafeArray.cpp

DispatchEvents.obj: DispatchEvents.cpp *.h
  $(CC) -c DispatchEvents.cpp

ComThread.obj: ComThread.cpp *.h
  $(CC) -c ComThread.cpp

EnumVariant.obj: EnumVariant.cpp *.h
  $(CC) -c EnumVariant.cpp

STA.obj: STA.cpp *.h
  $(CC) -c STA.cpp

DispatchProxy.obj: DispatchProxy.cpp *.h
  $(CC) -c DispatchProxy.cpp
