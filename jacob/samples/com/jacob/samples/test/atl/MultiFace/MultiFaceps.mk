
MultiFaceps.dll: dlldata.obj MultiFace_p.obj MultiFace_i.obj
	link /dll /out:MultiFaceps.dll /def:MultiFaceps.def /entry:DllMain dlldata.obj MultiFace_p.obj MultiFace_i.obj \
		kernel32.lib rpcndr.lib rpcns4.lib rpcrt4.lib oleaut32.lib uuid.lib \

.c.obj:
	cl /c /Ox /DWIN32 /D_WIN32_WINNT=0x0400 /DREGISTER_PROXY_DLL \
		$<

clean:
	@del MultiFaceps.dll
	@del MultiFaceps.lib
	@del MultiFaceps.exp
	@del dlldata.obj
	@del MultiFace_p.obj
	@del MultiFace_i.obj
