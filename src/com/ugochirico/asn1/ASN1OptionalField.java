package com.ugochirico.asn1;


public class ASN1OptionalField extends ASN1Object
{
    private static final byte TAG = (byte)0xA0;
    private ASN1Object m_asn1Obj;
    private byte m_btClass;
    
    public ASN1OptionalField(ASN1Object asn1Obj, byte btClass)
    {
        super(new byte[] {(byte)(TAG | btClass)}, asn1Obj.toByteArray());
        m_asn1Obj = asn1Obj;
        m_btClass = btClass;
    }
    
    public ASN1OptionalField(ASN1Object obj) throws ASN1ObjectNotFoundException
    {
        //super(asn1Obj.serialize(), 0, asn1Obj.getSerializedLength());
        super(obj);
        
        m_asn1Obj = new ASN1Object(obj.getValue(), 0, obj.getLength());
    }
    
//    public byte getTag()
//    {
//        return (byte)((TAG | m_btClass) & 0x00000000FF);
//    }
    
    public static boolean isOptionaField(byte btClass, byte[] btContent, int offset)
    {
        return btContent[offset] == (TAG | btClass);        
    }
    
    public ASN1Object getObject()
    {
        return m_asn1Obj;
    }
}
