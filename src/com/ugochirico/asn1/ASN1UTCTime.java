package com.ugochirico.asn1;

import java.io.*;

// ASN1UTCTime.java: implementation of the CASN1UTCTime class.
//
//////////////////////////////////////////////////////////////////////

public class ASN1UTCTime extends ASN1Object
{
	public static final byte TAG[] = {0x17};
	
//	 costruttori
    public ASN1UTCTime(ASN1Object obj) throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag() + " while needed: " + TAG);
//	    }
	}
    
	public ASN1UTCTime(InputStream in)
		throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
	{
		super(in);
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}

	public ASN1UTCTime(String szUTCTime)
	{
	    super(TAG, szUTCTime.getBytes());	    
	}
		
	public ASN1UTCTime(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}

	public String stringValue()
	{  
	    //uno dei modi in cui  ASN1UTCTime pu� essere formattato � YYMMDDhhmmssZ
	   //Per il momento si utilizzer� solo questo formato.
	   
	   //si ha la variabile di istanza m_pbUTCTime che � un vettore di byte
	   //La trasformiamo in una stringa
	   String strTime = new String(getValue());
	   
	    //Si prepara uno stringBuffer per rielaborare la stringa di base
	   StringBuffer strbTime = new StringBuffer();
	  
	   //Adesso bisogna formattarla
	   	for(int i = 0 ; i < strTime.length()-1 ; i=i+2)
	   	{
			if (i>5)
			{
				// se l'indice � >5 vuol dire che stimao trattando l'ora
				strbTime.append(strTime.substring(i,i+2));
				if(i==(strTime.length()-3))
					strbTime.append(" (U.T.C.)");
				else
					strbTime.append(":");
			}
			if (i<=5)
			{	//se l'indice �<=5 stiamo trattando la data che deve essere incertita
				//rispetto al formato attuale.
				strbTime.insert(0,strTime.substring(i,i+2));
				if(i==0)
					strbTime.append(" ");
				else
					strbTime.insert(2,"/");
			}
		 
	   	}
	     
	   
	   return strbTime.toString(); //il formato adesso � DD/MM/YY hh:mm:ss
	}
}
