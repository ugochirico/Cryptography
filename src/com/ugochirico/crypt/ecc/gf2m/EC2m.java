/* $Id$
 *
 * Copyright (C) 1995-1999 Systemics Ltd.
 * on behalf of the Cryptix Development Team. All rights reserved.
 * 
 * Use, modification, copying and distribution of this software is subject to
 * the terms and conditions of the Cryptix General Licence. You should have 
 * received a copy of the Cryptix General License along with this library; 
 * if not, you can download a copy from http://www.cryptix.org/ .
 */

package com.ugochirico.crypt.ecc.gf2m;

import com.ugochirico.math.*;

import com.ugochirico.crypt.ecc.EC;
import com.ugochirico.crypt.ecc.ECPoint;
import com.ugochirico.crypt.ecc.GF;
import com.ugochirico.crypt.ecc.InvalidECParamsException;
import com.ugochirico.crypt.ecc.PointNotOnCurveException;

/**
 * @author  Paulo S. L. M. Barreto <pbarreto@cryptix.org>
 * Porting on J2ME MIDP1.0 by Ugo Chirico <ugo.chirico@ugosweb.com>
 */
/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */
public class EC2m extends EC 
{

    /**
     * Convenient GF2m constant
     */
    GF2m gfZero;

    /**
     * Convenient GF2m constant
     */
    GF2m gfOne;

    /**
     * Create a partial description of the elliptic curve over GF(2<SUP>m</SUP>) satisfying
     * the equation y^2 + xy = x^3 + ax^2 + b with near-prime group order u = k*r
     * with a specified base point of prime order r.  The base point is left undefined.
     *
     * @param   m   dimension of the field GF(2^m)
     * @param   a   curve equation coefficient
     * @param   b   curve equation coefficient
     * @param   k   cofactor of the curve group order
     * @param   r   prime order of the cryptographic subgroup
     */
    public EC2m(int m, GF2m a, GF2m b, BigInteger k, BigInteger r) {
        this.q = ZERO.setBit(m);
        this.a = a;
        this.b = b;
        this.k = k;
        this.r = r;
        this.G = null;
        gfZero = new GF2m(m, ZERO);
        gfOne  = new GF2m(m, ONE);
        // CAVEAT: the infinity attribute MUST be set AFTER gfZero and gfOne!
        infinity = new ECPoint2m(this);
    }

    /**
     * Create a description of the elliptic curve over GF(2<SUP>m</SUP>) satisfying
     * the equation y^2 + xy = x^3 + ax^2 + b with near-prime group order u = k*r
     * with a specified base point of prime order r.
     *
     * @param   m   dimension of the field GF(2^m)
     * @param   a   curve equation coefficient
     * @param   b   curve equation coefficient
     * @param   k   cofactor of the curve group order
     * @param   r   prime order of the cryptographic subgroup
     * @param   G   description of base point of order r on the curve
     * 
     * @exception    InvalidECParamsException    if the selected parameters don't define a proper curve
     */
    public EC2m(int m, GF2m a, GF2m b, BigInteger k, BigInteger r, String G)
        throws InvalidECParamsException {
        this(m, a, b, k, r);

        int pc = Integer.parseInt(G.substring(0, 2), 16);
        int octetCount, coordLen, xPos, yPos, zPos;
        switch (pc) {
        case 0x02:
        case 0x03:
            // compressed form:
            try {
                String x = G.substring(2);
                this.G = new ECPoint2m(this,
                    new GF2m(m, G.substring(2)), // x coordinate
                    pc & 1);
            } catch (PointNotOnCurveException e) {
                throw new InvalidECParamsException("Invalid base point description");
            }
            break;
        case 0x04:
            // expanded form:
            try {
                octetCount  = (G.length() >>> 1);
                coordLen    = octetCount - 1; // 2*((octetCount - 1)/2)
                xPos        = 2;
                yPos        = xPos + coordLen;
                zPos        = yPos + coordLen;
                this.G = new ECPoint2m(this,
                    new GF2m(m, G.substring(xPos, yPos)),    // x-coordinate
                    new GF2m(m, G.substring(yPos, zPos)));    // y coordinate
            } catch (PointNotOnCurveException e) {
                throw new InvalidECParamsException("Invalid base point description");
            }
            break;
        case 0x06:
        case 0x07:
            // hybrid form:
            try {
                octetCount    = (G.length() >>> 1);
                coordLen    = octetCount - 1; // 2*((octetCount - 1)/2)
                xPos        = 2;
                yPos        = xPos + coordLen;
                zPos        = yPos + coordLen;
                this.G = new ECPoint2m(this,
                    new GF2m(m, G.substring(xPos, yPos)),    // x-coordinate
                    new GF2m(m, G.substring(yPos, zPos)));    // y coordinate
                // TODO: compare compressed and expanded forms in hybrid representation
            } catch (PointNotOnCurveException e) {
                throw new InvalidECParamsException("Invalid base point description");
            }
            break;
        default:
            throw new InvalidECParamsException("Invalid base point description");
        }
        /*
        if (!this.G.multiply(r).isZero()) {
            throw new InvalidECParamsException("Wrong order");
        }
        */
    }

    public ECPoint pointFactory(BigInteger x, BigInteger y)
    {
        // to test
        return new ECPoint2m(this, new GF2m(this.q.getLowestSetBit(), x), new GF2m(this.q.getLowestSetBit(), y));
    }
    
    public ECPoint pointFactory(BigInteger x, int ybit)
    {
//      to test
        return new ECPoint2m(this, new GF2m(this.q.getLowestSetBit(), x), ybit);        
    }
    
    public ECPoint pointFactory(byte[] data)
    {
    	int ybit;
    	BigInteger x;
    	BigInteger y;
    	
    	if(data.length == 1 && data[0] == 0)
    	{
    		x = BigInteger.ZERO;
    		ybit = 0;
    		return new ECPoint2m(this, new GF2m(this.q.getLowestSetBit(), x), ybit);
    	}
    	else
    	{
    		int pc = data[0];
    		
    		if ((pc & ECPoint.COMPRESSED) != 0) 
    		{
    			ybit = pc & 0x01;
    			int len = (data.length - 1);
    			byte[] xdata = new byte[len];
    			System.arraycopy(data, 1, xdata, 0, len);
    			x = new BigInteger(1, xdata);    			
    			return new ECPoint2m(this, new GF2m(this.q.getLowestSetBit(), x), ybit);
    	    }
//    		else if ((pc & ECPoint.EXPANDED) != 0) 
//    		{
//    			int len = (data.length - 1) / 2;
//    			byte xdata
//    			x = new BigInteger(1, data)
//    			osY = y.toByteArray();
//    	            resLen += osY.length;
//    	    }
//    	        result = new byte[resLen];
//    	        result[0] = (byte)pc;
//    	        System.arraycopy(osX, 0, result, 1, osX.length);
//    	        if (osY != null) {
//    	            System.arraycopy(osY, 0, result, 1 + osX.length, osY.length);
//    	        }
    	}
    	
    	return null;
//        return new ECPoint2m(this, new GF2m(this.q.getLowestSetBit(), x), ybit);        
    }
    
    /**
     * Check whether this curve contains a given point
     * (i.e. whether that point satisfies the curve equation)
     * 
     * @param   P   the point whose pertinence or not to this curve is to be determined
     * 
     * @return  true if this curve contains P, otherwise false
     */
    public boolean contains(ECPoint P) {
        if (!(P instanceof ECPoint2m)) {
            return false;
        }
        if (P.isZero()) {
            return true;
        }
        GF x = ((ECPoint2m)P).x;
        GF y = ((ECPoint2m)P).y;
        // check the affine equation (y + x).y = (x + a).x^2 + b:
        return y.add(x).multiply(y).equals(x.add(a).multiply(x.square()).add(b));
    }
    
    private static final int c_TwoCurve = 0x0000;    
    /**
     * X9F1 named curves
     */
    public static final int
        c2pnb163v1 = c_TwoCurve |  1, // J.4.1, example 1
        c2pnb163v2 = c_TwoCurve |  2, // J.4.1, example 2
        c2pnb163v3 = c_TwoCurve |  3, // J.4.1, example 3
        c2pnb176w1 = c_TwoCurve |  4, // J.4.2, example 1
        c2tnb191v1 = c_TwoCurve |  5, // J.4.3, example 1
        c2tnb191v2 = c_TwoCurve |  6, // J.4.3, example 2
        c2tnb191v3 = c_TwoCurve |  7, // J.4.3, example 3
    //  c2onb191v4 = c_TwoCurve |  8, // J.4.3, example 4 -- not supported (ONB)
    //  c2onb191v5 = c_TwoCurve |  9, // J.4.3, example 5 -- not supported (ONB)
        c2pnb208w1 = c_TwoCurve | 10, // J.4.4, example 1
        c2tnb239v1 = c_TwoCurve | 11, // J.4.5, example 1
        c2tnb239v2 = c_TwoCurve | 12, // J.4.5, example 2
        c2tnb239v3 = c_TwoCurve | 13, // J.4.5, example 3
    //  c2onb239v4 = c_TwoCurve | 14, // J.4.5, example 4 -- not supported (ONB)
    //  c2onb239v5 = c_TwoCurve | 15, // J.4.5, example 5 -- not supported (ONB)
        c2pnb272w1 = c_TwoCurve | 16, // J.4.6, example 1
        c2pnb304w1 = c_TwoCurve | 17, // J.4.7, example 1
        c2tnb359v1 = c_TwoCurve | 18, // J.4.8, example 1
        c2pnb368w1 = c_TwoCurve | 19, // J.4.9, example 1
        c2tnb431r1 = c_TwoCurve | 20; // J.4.10,example 1
    
    /**
     * Build an X9.62 named curve
     * 
     * @param   curveName   a constant representing the X9.62 curve
     * 
     * @return  the desired X9.62 curve, or null if the curve name is invalid or unsupported
     */
    public static EC getNamedCurve(int curveName) {
        BigInteger p;
        switch (curveName) {
        case c2pnb163v1:
            return new EC2m(163,
                            new GF2m(163, "072546B5435234A422E0789675F432C89435DE5242"),
                            new GF2m(163, "00C9517D06D5240D3CFF38C74B20B6CD4D6F9DD4D9"),
                            BigInteger.valueOf(2L),
                            new BigInteger("0400000000000000000001E60FC8821CC74DAEAFC1", 16),
                            "0307AF69989546103D79329FCC3D74880F33BBE803CB");
        case c2pnb163v2:
            return new EC2m(163,
                            new GF2m(163, "0108B39E77C4B108BED981ED0E890E117C511CF072"),
                            new GF2m(163, "0667ACEB38AF4E488C407433FFAE4F1C811638DF20"),
                            BigInteger.valueOf(2L),
                            new BigInteger("03FFFFFFFFFFFFFFFFFFFDF64DE1151ADBB78F10A7", 16),
                            "030024266E4EB5106D0A964D92C4860E2671DB9B6CC5");
        case c2pnb163v3:
            return new EC2m(163,
                            new GF2m(163, "07A526C63D3E25A256A007699F5447E32AE456B50E"),
                            new GF2m(163, "03F7061798EB99E238FD6F1BF95B48FEEB4854252B"),
                            BigInteger.valueOf(2L),
                            new BigInteger("03FFFFFFFFFFFFFFFFFFFE1AEE140F110AFF961309", 16),
                            "0202F9F87B7C574D0BDECF8A22E6524775F98CDEBDCB");
        case c2pnb176w1:
            return new EC2m(176,
                            new GF2m(176, "E4E6DB2995065C407D9D39B8D0967B96704BA8E9C90B"),
                            new GF2m(176, "5DDA470ABE6414DE8EC133AE28E9BBD7FCEC0AE0FFF2"),
                            BigInteger.valueOf(0xFF6EL),
                            new BigInteger("010092537397ECA4F6145799D62B0A19CE06FE26AD", 16),
                            "038D16C2866798B600F9F08BB4A8E860F3298CE04A5798");
        case c2tnb191v1:
            return new EC2m(191,
                            new GF2m(191, "2866537B676752636A68F56554E12640276B649EF7526267"),
                            new GF2m(191, "2E45EF571F00786F67B0081B9495A3D95462F5DE0AA185EC"),
                            BigInteger.valueOf(2L),
                            new BigInteger("40000000000000000000000004A20E90C39067C893BBB9A5", 16),
                            "0236B3DAF8A23206F9C4F299D7B21A9C369137F2C84AE1AA0D");
        case c2tnb191v2:
            return new EC2m(191,
                            new GF2m(191, "401028774D7777C7B7666D1366EA432071274F89FF01E718"),
                            new GF2m(191, "0620048D28BCBD03B6249C99182B7C8CD19700C362C46A01"),
                            BigInteger.valueOf(4L),
                            new BigInteger("20000000000000000000000050508CB89F652824E06B8173", 16),
                            "023809B2B7CC1B28CC5A87926AAD83FD28789E81E2C9E3BF10");
        case c2tnb191v3:
            return new EC2m(191,
                            new GF2m(191, "6C01074756099122221056911C77D77E77A777E7E7E77FCB"),
                            new GF2m(191, "71FE1AF926CF847989EFEF8DB459F66394D90F32AD3F15E8"),
                            BigInteger.valueOf(6L),
                            new BigInteger("155555555555555555555555610C0B196812BFB6288A3EA3", 16),
                            "03375D4CE24FDE434489DE8746E71786015009E66E38A926DD");
        /*
        case c2onb191v4:
            return null; // ONB not yet supported
        case c2onb191v5:
            return null; // ONB not yet supported
        */
        case c2pnb208w1:
            return new EC2m(208,
                            new GF2m(208, "0000000000000000000000000000000000000000000000000000"),
                            new GF2m(208, "C8619ED45A62E6212E1160349E2BFA844439FAFC2A3FD1638F9E"),
                            BigInteger.valueOf(0xFE48L),
                            new BigInteger("0101BAF95C9723C57B6C21DA2EFF2D5ED588BDD5717E212F9D", 16),
                            "0289FDFBE4ABE193DF9559ECF07AC0CE78554E2784EB8C1ED1A57A");
        case c2tnb239v1:
            return new EC2m(239,
                            new GF2m(239, "32010857077C5431123A46B808906756F543423E8D27877578125778AC76"),
                            new GF2m(239, "790408F2EEDAF392B012EDEFB3392F30F4327C0CA3F31FC383C422AA8C16"),
                            BigInteger.valueOf(4L),
                            new BigInteger("2000000000000000000000000000000F4D42FFE1492A4993F1CAD666E447", 16),
                            "0257927098FA932E7C0A96D3FD5B706EF7E5F5C156E16B7E7C86038552E91D");
        case c2tnb239v2:
            return new EC2m(239,
                            new GF2m(239, "4230017757A767FAE42398569B746325D45313AF0766266479B75654E65F"),
                            new GF2m(239, "5037EA654196CFF0CD82B2C14A2FCF2E3FF8775285B545722F03EACDB74B"),
                            BigInteger.valueOf(6L),
                            new BigInteger("1555555555555555555555555555553C6F2885259C31E3FCDF154624522D", 16),
                            "0228F9D04E900069C8DC47A08534FE76D2B900B7D7EF31F5709F200C4CA205");
        case c2tnb239v3:
            return new EC2m(239,
                            new GF2m(239, "01238774666A67766D6676F778E676B66999176666E687666D8766C66A9F"),
                            new GF2m(239, "6A941977BA9F6A435199ACFC51067ED587F519C5ECB541B8E44111DE1D40"),
                            BigInteger.valueOf(10L),
                            new BigInteger("0CCCCCCCCCCCCCCCCCCCCCCCCCCCCCAC4912D2D9DF903EF9888B8A0E4CFF", 16),
                            "0370F6E9D04D289C4E89913CE3530BFDE903977D42B146D539BF1BDE4E9C92");
        /*
        case c2onb239v4:
            return null; // ONB not yet supported
        case c2onb239v5:
            return null; // ONB not yet supported
        */
        case c2pnb272w1:
            return new EC2m(272,
                            new GF2m(272, "91A091F03B5FBA4AB2CCF49C4EDD220FB028712D42BE752B2C40094DBACDB586FB20"),
                            new GF2m(272, "7167EFC92BB2E3CE7C8AAAFF34E12A9C557003D7C73A6FAF003F99F6CC8482E540F7"),
                            BigInteger.valueOf(0xFF06L),
                            new BigInteger("0100FAF51354E0E39E4892DF6E319C72C8161603FA45AA7B998A167B8F1E629521", 16),
                            "026108BABB2CEEBCF787058A056CBE0CFE622D7723A289E08A07AE13EF0D10D171DD8D");
        case c2pnb304w1:
            return new EC2m(304,
                            new GF2m(304, "FD0D693149A118F651E6DCE6802085377E5F882D1B510B44160074C1288078365A0396C8E681"),
                            new GF2m(304, "BDDB97E555A50A908E43B01C798EA5DAA6788F1EA2794EFCF57166B8C14039601E55827340BE"),
                            BigInteger.valueOf(0xFE2EL),
                            new BigInteger("0101D556572AABAC800101D556572AABAC8001022D5C91DD173F8FB561DA6899164443051D", 16),
                            "02197B07845E9BE2D96ADB0F5F3C7F2CFFBD7A3EB8B6FEC35C7FD67F26DDF6285A644F740A2614");
        case c2tnb359v1:
            return new EC2m(359,
                            new GF2m(359, "5667676A654B20754F356EA92017D946567C46675556F19556A04616B567D223A5E05656FB549016A96656A557"),
                            new GF2m(359, "2472E2D0197C49363F1FE7F5B6DB075D52B6947D135D8CA445805D39BC345626089687742B6329E70680231988"),
                            BigInteger.valueOf(0x4CL),
                            new BigInteger("01AF286BCA1AF286BCA1AF286BCA1AF286BCA1AF286BC9FB8F6B85C556892C20A7EB964FE7719E74F490758D3B", 16),
                            "033C258EF3047767E7EDE0F1FDAA79DAEE3841366A132E163ACED4ED2401DF9C6BDCDE98E8E707C07A2239B1B097");
        case c2pnb368w1:
            return new EC2m(368,
                            new GF2m(368, "E0D2EE25095206F5E2A4F9ED229F1F256E79A0E2B455970D8D0D865BD94778C576D62F0AB7519CCD2A1A906AE30D"),
                            new GF2m(368, "FC1217D4320A90452C760A58EDCD30C8DD069B3C34453837A34ED50CB54917E1C2112D84D164F444F8F74786046A"),
                            BigInteger.valueOf(0xFF70L),
                            new BigInteger("010090512DA9AF72B08349D98A5DD4C7B0532ECA51CE03E2D10F3B7AC579BD87E909AE40A6F131E9CFCE5BD967", 16),
                            "021085E2755381DCCCE3C1557AFA10C2F0C0C2825646C5B34A394CBCFA8BC16B22E7E789E927BE216F02E1FB136A5F");
        case c2tnb431r1:
            return new EC2m(431,
                            new GF2m(431, "1A827EF00DD6FC0E234CAF046C6A5D8A85395B236CC4AD2CF32A0CADBDC9DDF620B0EB9906D0957F6C6FEACD615468DF104DE296CD8F"),
                            new GF2m(431, "10D9B4A3D9047D8B154359ABFB1B7F5485B04CEB868237DDC9DEDA982A679A5A919B626D4E50A8DD731B107A9962381FB5D807BF2618"),
                            BigInteger.valueOf(0x2760L),
                            new BigInteger("0340340340340340340340340340340340340340340340340340340323C313FAB50589703B5EC68D3587FEC60D161CC149C1AD4A91", 16),
                            "02120FC05D3C67A99DE161D2F4092622FECA701BE4F50F4758714E8A87BBF2A658EF8C21E7C5EFE965361F6C2999C0C247B0DBD70CE6B7");        
        default:
            return null;
        }
    }

}
