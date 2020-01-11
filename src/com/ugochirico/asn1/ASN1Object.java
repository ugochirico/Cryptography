
package com.ugochirico.asn1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ugochirico.util.Encoder;

public class ASN1Object
{
    public byte[] getTag()
    {
        return m_nTag;
    }
    
    public final int getLength()
    {
        return m_btValue.length;
    }
    
    public final byte[] getValue()
    {
        return m_btValue;
    }
    
    public final void setValue(byte[] btValue)
    {
        if(btValue != null)
        {
	        m_btValue = new byte[btValue.length];
		    System.arraycopy(btValue, 0, m_btValue, 0, btValue.length);
        }
        else
        {
            m_btValue = new byte[0];
        }
    }
    
    private byte[] m_nTag;
    private byte[] m_btValue;
        
//    public ASN1Object()
//    {
//        
//    }
    
    public ASN1Object(ASN1Object obj)
	{
	    this(obj.getTag(), obj.getValue());
	}
    
    public ASN1Object(InputStream in)
        throws IOException, ASN1EOFException
    {  
        getTLV(in);        
    }
    
    public ASN1Object(byte[] btContent, int offset, int length)
    	throws ASN1EOFException
	{  
        ByteArrayInputStream ins = new ByteArrayInputStream(btContent, offset, length);
        
	    try
        {
            getTLV(ins);
        }
        catch (IOException e)
        {
            //e.printStackTrace();
            throw new RuntimeException(e.toString());
        }        
	}
 
    public ASN1Object(byte btTag, byte[] btValue)
    {
    	this(new byte[] {btTag}, btValue);
    }
    
    public ASN1Object(byte[] btTag, byte[] btValue)
	{  
	    m_nTag   = btTag;
	    setValue(btValue);
	}
    
    public int getSerializedLength()
    {
        int nLen = getLength();
        int nTLVLen;

        if (nLen < 0x80)  // shortform
        {
            nTLVLen = 1 + nLen;
        }
        else //(nLen > 0x80) // longform
        {
            // long form
			int no_octets = 0x00;
			int nAuxLen = nLen;
			//ByteArrayOutputStream outs = new ByteArrayOutputStream();
			while (nAuxLen != 0) 
			{
			    //outs.write(nAuxLen % 256);
			    nAuxLen /= 256;
				no_octets++;
			}
			
//			System.out.println("nLen " + nLen);			
//			System.out.println("no_octets " + no_octets);
//			
			//int nLenNeeded = outs.toByteArray().length;
			
//			// reverse
//			byte btRevLength[] = outs.toByteArray();
//			byte btLength[] = new byte[btRevLength.length];
//			
//			for(int i = 0; i < btRevLength.length; i++)
//			{
//			    btLength[i] = btRevLength[btRevLength.length - i - 1];
//			}
//			
//            
//            int nLenNeeded = (int)(Math.ceil((Math.log(nLen) / Math.log(2)) / 8));
//            
//            // se multiplo di 8
//            boolean eightmul = Math.ceil( (Math.log(nLen) / Math.log(2) ) % 8) == 0;
//            if(eightmul)
//                nLenNeeded += 1;

            nTLVLen    = 1 + no_octets + nLen;
        }
    
        return nTLVLen + m_nTag.length;
    }
    
    public byte[] toByteArray()
    {
        byte pbtSerialized[] = null;
        int nLen = getLength();
        
        try
        {
	        if(nLen < 0x80)  // shortform
	        {
	//          System.out.println("SHORT FORM");  // DBG
	        
	            // Short Form
	            ByteArrayOutputStream bouts = new ByteArrayOutputStream();
	            
	            bouts.write(m_nTag);
	            bouts.write((byte)nLen);
	            bouts.write(getValue());
	                        
	            pbtSerialized = bouts.toByteArray();
	        }
	        else //if (nLen >= 0x80)  longform
	        {
	            // long form
				int no_octets = 0x00;
				int nAuxLen = nLen;
				while (nAuxLen != 0) 
				{
				    nAuxLen /= 256;
					no_octets++;
				}
				
	//			            nTLVLen    = 2 + no_octets + nLen;
	
	            ByteArrayOutputStream bouts = new ByteArrayOutputStream();
            
           
	            bouts.write(m_nTag);
	            bouts.write((byte)(0x80 + no_octets));
	
	            int nDigit = 0;
	            int i = 0;
	            int nAux = nLen;
	            byte[] pbtLen = new byte[no_octets];
	            for(i = 0; i < no_octets; i++)
	            {
	                nDigit = nAux >> (256 * i);
	            	            
	                pbtLen[(no_octets - i - 1)] = (byte)nDigit;
	                nAux = nAux / 256;
	            }
	            
	            bouts.write(pbtLen);
	            bouts.write(getValue());
	            
	            pbtSerialized = bouts.toByteArray();
	        }
        }
        catch(IOException ex)
        {
        	ex.printStackTrace();
        }
        
        return pbtSerialized;
    }
    
    protected void getTLV(InputStream in)
        throws IOException, ASN1EOFException
    {
//        System.out.println("getTLV");
        
    	ByteArrayOutputStream tagbouts = new ByteArrayOutputStream();
    	
    	byte tag = (byte)in.read();
    	if(tag == -1)
            throw new ASN1EOFException();
    	
    	tagbouts.write(tag);
    	if((tag & 0x1F) >= 0x1F)
    	{
    		do
    		{
	    		// more octets
	    		tag = (byte)in.read();
	        	if(tag == -1)
	                throw new ASN1EOFException();

	    		tagbouts.write(tag);
    		}
    		while((tag & 0x80) >= 0x80);    		
    	}
        
    	m_nTag = tagbouts.toByteArray();
        
//        System.out.println("m_btTag " + m_btTag);
        
        
        
        // Read Len byte
        int nLenRead = in.read();
        
//        System.out.println("nLenRead " + nLenRead);
        
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        
        if(nLenRead == 0x80)//-128 /*0x80*/)
        {
            // indefinite lenght
            // the object ends with 00 00
            
            // read from inputstream while 00 00 is found

            boolean bEnd = false;
            int c = in.read(); // read first byte
            int c1;
            while(!bEnd && (c != -1))
            {
                if(c == 0) // first 00 found
                {
                    // read next byte
                    c1 = in.read();
                    if(c1 == 0)  // second 00 found
                    {
                        bEnd = true; // End reached
                    }
                    else if(c1 == -1)
                    {
                        c = c1; // EOF reached
                    }
                    else
                    {
                        // add both 
                        outs.write(c);
                        outs.write(c1);
                    }
                }
                else
                {
                    outs.write(c);
                    c = in.read();                    
                }                             
            }                        
        }
        else
        {
            int nLen = 0;
            
            // definite length
            if ((nLenRead & 0x80) == 0x80)  
	        {
                // definite length long form
            	//#ifdef LOG
//            	Logger.logMsg("LONG FORM "+ nLenRead);
                //#endif

	            // Long Form
	            nLenRead = nLenRead & 0x7F;
	            
	            //#ifdef LOG
//            	Logger.logMsg("nLenRead "+ nLenRead);
                //#endif
	            byte btHexLen[] = new byte[nLenRead];
	            
	            // read the correct number of byte defining the length
	            if (in.read(btHexLen) != nLenRead)
	            {
//		            //#ifdef LOG
//	            	Logger.logMsg("invalid nLenRead "+ nLenRead);
//	                //#endif

	                throw new ASN1EOFException();
	            }
	            
//	            System.out.println("nLenRead " + nLenRead);	            
	              
	            // Calculate the length
				for (int j = 0;	j < nLenRead; j++) 
				{
				    nLen = nLen << 8;
				    nLen += btHexLen[j] & 0x000000FF;
				}			
	            
				//#ifdef LOG
//            	Logger.logMsg("nLen "+ nLen);
                //#endif
            	
//				System.out.println("nLen " + intToHexString(nLen));	  				
	        }
	        else // definite length short form
	        {
	            // Short Form
	            nLen = nLenRead & 0x000000FF;
	        }   
        
            int c;
            // read the stream
            for(int i = 0; i < nLen; i++)
            {
                c = in.read();
                if(c == -1)
                {
                	//#ifdef LOG
//                	Logger.logMsg("EOF "+ i);
                    //#endif
                	
                    throw new ASN1EOFException();
                }
                
                outs.write(c);
            }

        }
        
        m_btValue = outs.toByteArray();
    }
            
    public void write(OutputStream out)
        throws IOException
    {
        out.write(toByteArray());
        out.flush();
    }
    
//    /** The hexadecimal digits "0" through "f". */
//    private static char[] NIBBLE = {
//                                      '0', '1', '2', '3', '4', '5', '6', '7',
//                                      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
//                                  };

    /**
     * Convert a byte to a string of hexadecimal digits.
     */
//    private static final String byteToHexString(byte a)
//    {
//        StringBuffer sb = new StringBuffer(2);
//        sb.append(NIBBLE[(a>>>4)&0xf]);
//        sb.append(NIBBLE[a&0xf]);
//        return sb.toString();
//    }
    
//    /**
//     * Convert an int to a string of hexadecimal digits.
//     */
//    public static final String intToHexString(int a)
//    {
//        StringBuffer sb = new StringBuffer(8);
//        for (int i=0; i<8; i+=2)
//        {
//            int nibble1 = (int)(a >>> (60-4*i)) & 0xf;
//            int nibble2 = (int)(a >>> (60-4*(i+1))) & 0xf;
//            
//            if(nibble1 > 0 || nibble2 > 0)
//            {
//                sb.append(NIBBLE[nibble1]);
//                sb.append(NIBBLE[nibble2]);
//            }
//        }
//        return sb.toString();
//    }
//    
//    /**
//     * Convert a byte array to a string of hexadecimal digits.
//     */
//    private static final String bytesToHexString(byte[] buf)
//    {
//        return bytesToHexString(buf, 0, buf.length);
//    }
//
//
//    /**
//     * Convert a byte array to a string of hexadecimal digits.
//     * The bytes <code>buf[i..i+length-1]</code> are used.
//     */
//    private static final String bytesToHexString(byte[] buf, int i, int length)
//    {
//        StringBuffer sb = new StringBuffer(length*2);
//        for (int j=i; j<i+length; j++) {
//            sb.append(NIBBLE[(buf[j]>>>4)&15]);
//            sb.append(NIBBLE[ buf[j]     &15]);
//        }
//        return sb.toString().toUpperCase();
//    }
    
//    protected static int DerExtractLength(byte [] data) 
//    {
//		int length = 0;
//		if ((data[1] & 0x80) != 0)
//		{ 
//		    // Long form 
//			for (int j = 2;
//				j <= (data[1] & 0x7f);
//				j++) 
//			{
//				length *= 0x100;
//				length += data[j];
//			}
//		} 
//		else 
//		{ 
//		    // Short form
//			length = data[1];
//		}
//
//		return length;
//	}

//	protected static byte[] DerMakeLength(byte data[]) 
//	{
//	    ByteArrayOutputStream outs = new ByteArrayOutputStream();
//		if (data.length > 127) 
//		{ 
//		    // long form
//			long length = data.length;
//			int no_octets = 0x80;
//			
//			while (length != 0) 
//			{
//			    outs.write((int)(length % 256));
//				length /= 256;
//				no_octets++;
//			}
//			outs.write(no_octets);
//	
//			// reverse
//			byte btRevLength[] = outs.toByteArray();
//			byte btLength[] = new byte[btRevLength.length];
//			
//			for(int i = 0; i < btRevLength.length; i++)
//			{
//			    btLength[i] = btRevLength[btRevLength.length - i - 1];
//			}
//			
//			return btLength;
//		} 
//		else 
//		{ 
//		    // short form
//		    outs.write(data.length);
//		    
//		    return outs.toByteArray();
//		}
//	}
	
    public String toString()
    {
        return Encoder.bytesToHexString(toByteArray());
    }
    
    public static void main(String[] args)
    {
    	//byte[] val = Encoder.hexStringToBytes("3082010A0282010100C92F857F16CD51DFA2A756A1186A68C52E0BCC7E82466A10EFEDC9CA7E2FB5805C000D7695B7CACFA1E6EFFA66D8F15B6620061E20FDB90DA8F4C85B289566867D0E7C801378C50D52A9C5136C909D3D286DC9620A8FE0133FC32E1648C79625160F046E56EF2C73A83A7F1AE86E5777C60D0186369A1948BC442D2891CFD12C7BC37D0071D08EF6ABD4C98C19E6091490B1E527DFF53469A1A27E9DA32904E878113EDFDBA64BCF85407CF6C8F1B1F97CE6BD6E167DB738248449B833A7B8F721297799A516E35038753A0C209D2F9AFC9723CA6DF82FC62FA8D5743B098D1926B182A77C38BCB3DA13DDF40FC195624DDB3902DE3702C2CEC031B3DC9A05030203010001");
    	
    	byte[] val = Encoder.hexStringToBytes("7082010EBFA101820108A3820104978201006A8B2EF584ECA04336FD009853195CAE1C9A5B5E883943666A44465199676C7F7EAC08F229B4640E70E5949769E542245B9D34C871A04CBF3E2E68A6C4F1CE03E44D4BD0CAB13C8C998A488299C98911FD029F04C8C33BC784495C69B6873C79DD0195DE6EE3763FECDCA4D0ED66153E3106DA0A74B8C4ED70E681220AFF9F1712E4CEC0AD25EC4786FA35454FCC0283995684C760BABDCBD3FC49A0887D1C7837F040BA70F093E91A3F7BFD2F2F368EF4B3E1D642E55967F50623D95EA333E8C9F2A08A53632B1A1B8E71000C5352B91D5B216DCFF2912AF542E66C586802C47423DF57FD130F0A73B6222E123E906E59D1546596E797AB1F38151C1C28AE35");
    	
    	try {
			ASN1Sequence s = new ASN1Sequence(val, 0, val.length);
			System.out.println(s.toString());
			
			ASN1Object o1 = s.elementAt(0);
			
			System.out.println(o1.toString());
			
			ASN1Object o2 = s.elementWithTag(new byte[] {(byte)0xBF, (byte)0xA1, 0x01});
			System.out.println(o2.toString());
			
		} catch (ASN1EOFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ASN1ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
}

















