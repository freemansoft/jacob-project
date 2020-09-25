VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
  Persistable = 0  'NotPersistable
  DataBindingBehavior = 0  'vbNone
  DataSourceBehavior  = 0  'vbNone
  MTSTransactionMode  = 0  'NotAnMTSObject
END
Attribute VB_Name = "Math"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = True
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True
Attribute VB_Ext_KEY = "SavedWithClassBuilder6" ,"Yes"
Attribute VB_Ext_KEY = "Top_Level" ,"Yes"
'To fire this event, use RaiseEvent with the following syntax:
'RaiseEvent DoneAdd[(arg1, arg2, ... , argn)]
Public Event DoneAdd(result As Variant)
'To fire this event, use RaiseEvent with the following syntax:
'RaiseEvent DoneMult[(arg1, arg2, ... , argn)]
Public Event DoneMult(result As Variant)


Public Function Mult(in1 As Variant, in2 As Variant) As Variant
  Mult = in1 * in2
  RaiseEvent DoneMult(in1 * in2)
End Function

Public Function Add(in1 As Variant, in2 As Variant) As Variant
  Add = in1 + in2
  RaiseEvent DoneAdd(in1 + in2)
End Function

Public Function getNothing() As Variant
  Set getNothing = Nothing
End Function

