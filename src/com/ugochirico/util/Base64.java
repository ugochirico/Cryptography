
package com.ugochirico.util;

import java.io.*;

public class Base64 
{

    static final char[] charTab =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();

    public static byte[] encode(byte[] data) {
        return encode(data, 0, data.length, false, false, null).toString().getBytes();
    }
    
    public static String encode(byte[] data, boolean crlf, boolean folded) {
        return encode(data, 0, data.length, crlf, folded, null).toString();
    }

    /** Encodes the part of the given byte array denoted by start and
    len to the Base64 format.  The encoded data is appended to the
    given StringBuffer. If no StringBuffer is given, a new one is
    created automatically. The StringBuffer is the return value of
    this method. */

    public static StringBuffer encode(
        byte[] data,
        int start,
        int len,
        boolean crlf, 
        boolean folded,
        StringBuffer buf) {

        if (buf == null)
            buf = new StringBuffer(data.length * 3 / 2);

        int end = len - 3;
        int i = start;
        int n = 0;

        while (i <= end) {
            int d =
                ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 0x0ff) << 8)
                    | (((int) data[i + 2]) & 0x0ff);

            buf.append(charTab[(d >> 18) & 63]);
            buf.append(charTab[(d >> 12) & 63]);
            buf.append(charTab[(d >> 6) & 63]);
            buf.append(charTab[d & 63]);

            i += 3;

            if (n++ >= 14) 
            {
                n = 0;
                if(crlf)
                	buf.append("\r\n");
                if(folded)
                	buf.append(" ");
            }
        }

        if (i == start + len - 2) {
            int d =
                ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 255) << 8);

            buf.append(charTab[(d >> 18) & 63]);
            buf.append(charTab[(d >> 12) & 63]);
            buf.append(charTab[(d >> 6) & 63]);
            buf.append("=");
        }
        else if (i == start + len - 1) {
            int d = (((int) data[i]) & 0x0ff) << 16;

            buf.append(charTab[(d >> 18) & 63]);
            buf.append(charTab[(d >> 12) & 63]);
            buf.append("==");
        }

        return buf;
    }

    static int decode(byte c) {

        if (c >= 'A' && c <= 'Z')
            return ((int) c) - 65;
        else if (c >= 'a' && c <= 'z')
            return ((int) c) - 97 + 26;
        else if (c >= '0' && c <= '9')
            return ((int) c) - 48 + 26 + 26;
        else
            switch (c) {
                case '+' :
                    return 62;
                case '/' :
                    return 63;
                case '=' :
                    return 0;
                default :
                    throw new RuntimeException(
                        "unexpected code: " + c);
            }
    }

    /** Decodes the given Base64 encoded String to a new byte array.
    The byte array holding the decoded data is returned. */

    public static byte[] decode(String s) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {

            decode(s, bos);

        }
        catch (IOException e) {
            throw new RuntimeException();
        }
        return bos.toByteArray();
    }

    public static byte[] decode(byte[] buffer) {
    	return decode(buffer, 0, buffer.length);
    }

    public static byte[] decode(byte[] buffer, int offset, int len) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {

            decode(buffer, offset, len, bos);

        }
        catch (IOException e) {
            throw new RuntimeException();
        }
        return bos.toByteArray();
    }
    
    public static void decode(byte[] buffer, int offset, int len, OutputStream os)
    throws IOException {
    int i = 0;

    while (true) {
        while (i < len && buffer[i + offset] <= ' ')
            i++;

        if (i == len)
            break;

        int tri =
            (decode(buffer[i + offset]) << 18)
                + (decode(buffer[i + offset + 1]) << 12)
                + (decode(buffer[i + offset + 2]) << 6)
                + (decode(buffer[i + offset + 3]));


        os.write((tri >> 16) & 255);
        if (buffer[i + offset + 2] == '=')
            break;
        os.write((tri >> 8) & 255);
        if (buffer[i + offset + 3] == '=')
            break;
        os.write(tri & 255);

        i += 4;
    }
}
    public static void decode(String s, OutputStream os)
        throws IOException {
        int i = 0;

        int len = s.length();

        while (true) {
            while (i < len && s.charAt(i) <= ' ')
                i++;

            if (i == len)
                break;

            int tri =
                (decode((byte) s.charAt(i)) << 18)
                    + (decode((byte) s.charAt(i + 1)) << 12)
                    + (decode((byte) s.charAt(i + 2)) << 6)
                    + (decode((byte) s.charAt(i + 3)));


            os.write((tri >> 16) & 255);
            if (s.charAt(i + 2) == '=')
                break;
            os.write((tri >> 8) & 255);
            if (s.charAt(i + 3) == '=')
                break;
            os.write(tri & 255);

            i += 4;
        }
    }
    
//    public static void main(String args[])
//    {
//    	   String base = "LzlqLzRBQVFTa1pKUmdBQkFnQUFBUUFCQUFELzJ3Q0VBQWdHQmdjR0JRZ0hC\n" +
//           " d2NKQ1FnS0RCUU5EQXNMREJrU0V3OFVIUm9mSGgwYUhCd2dKQzRuSUNJc0l4\n" +
//           " d2NLRGNwTERBeE5EUTBIeWM1UFRneVBDNHpORElCQ1FrSkRBc01HQTBOR0RJ\n" +
//           " aEhDRXlNakl5TWpJeU1qSXlNakl5TWpJeU1qSXlNakl5TWpJeU1qSXlNakl5\n" +
//           " TWpJeU1qSXlNakl5TWpJeU1qSXlNakl5TXYvRUFhSUFBQUVGQVFFQkFRRUJB\n" +
//           " QUFBQUFBQUFBQUJBZ01FQlFZSENBa0tDd0VBQXdFQkFRRUJBUUVCQVFBQUFB\n" +
//           " QUFBQUVDQXdRRkJnY0lDUW9MRUFBQ0FRTURBZ1FEQlFVRUJBQUFBWDBCQWdN\n" +
//           " QUJCRUZFaUV4UVFZVFVXRUhJbkVVTW9HUm9RZ2pRckhCRlZMUjhDUXpZbktD\n" +
//           " Q1FvV0Z4Z1pHaVVtSnlncEtqUTFOamM0T1RwRFJFVkdSMGhKU2xOVVZWWlhX\n" +
//           " RmxhWTJSbFptZG9hV3B6ZEhWMmQzaDVlb09FaFlhSGlJbUtrcE9VbFphWG1K\n" +
//           " bWFvcU9rcGFhbnFLbXFzck8wdGJhM3VMbTZ3c1BFeGNiSHlNbkswdFBVMWRi\n" +
//           " WDJObmE0ZUxqNU9YbTUranA2dkh5OC9UMTl2ZjQrZm9SQUFJQkFnUUVBd1FI\n" +
//           " QlFRRUFBRUNkd0FCQWdNUkJBVWhNUVlTUVZFSFlYRVRJaktCQ0JSQ2thR3h3\n" +
//           " UWtqTTFMd0ZXSnkwUW9XSkRUaEpmRVhHQmthSmljb0tTbzFOamM0T1RwRFJF\n" +
//           " VkdSMGhKU2xOVVZWWlhXRmxhWTJSbFptZG9hV3B6ZEhWMmQzaDVlb0tEaElX\n" +
//           " R2g0aUppcEtUbEpXV2w1aVptcUtqcEtXbXA2aXBxckt6dExXMnQ3aTV1c0xE\n" +
//           " eE1YR3g4akp5dExUMU5YVzE5aloydUxqNU9YbTUranA2dkx6OVBYMjkvajUr\n" +
//           " di9BQUJFSUFGQUFaQU1CRVFBQ0VRRURFUUgvMmdBTUF3RUFBaEVERVFBL0FM\n" +
//           " T1Rqbk5kSmlBWTg1NW9HSm5ta0lYZFF4amxiam1wc080bS93QnFkZ3VHL3dC\n" +
//           " cUF1SVh5UFNpd1hFREVVV0JEU2V0TVFiamlnWmFzdnNKVjJ2bldOQ2Rxczdo\n" +
//           " RkJQVEo5ZTJQZXVQRXprbnl4T21oRlNUbGE1eXVwMk52SmZ5TkdWQ24rNjNC\n" +
//           " K2xjcWJTT2hvNm9JUjJyMXJubTJJbkh0aW1nWXpiUllWeHlwNmlnWklFVHVL\n" +
//           " UUMrWEhTMUFVSkY2R21BN0VRWDd1VDZVaGpDSXovQU1zOFVBaG15UDBOTUJt\n" +
//           " MUFldEFHZnJsdEpQWlloaVdWUVZMSTV6amFTUVI2SExIbXVQRXcrMGQrRXF0\n" +
//           " ZTZ0em10eGkrUlhCVWVneVB3cmliT25sTzQrMWUzNjE3UEtlTHpFYlhHZTFO\n" +
//           " SUxnSnNIa1U3QmNjYm5qR0tubEM0Z25HZVJUc0Z4M25KNzBXQzRoblgwcFdD\n" +
//           " NDM3UXY5MmdkdyswTDZVQmNQUFQwb0M0d3lyUUE5cmVSbzhzb0tNY0JXSFg4\n" +
//           " SzVwVjZiMDNPcW5oNmoxV2hnYWpwRndsNDNsUWwxYjV2a0hBOXE4KzZ1enZj\n" +
//           " V2ErNnZhdXp4TkEzQ2tGMEp1b0M2SENPNWNBeFdWMUlwNk1zTGJUOUd4Zy9u\n" +
//           " VTh5S3N4eldtcHFtODZOcVpUKzlIYXMvd0Q2RG1qbWlGbVdyVFNyMjl0UmNR\n" +
//           " UlpqeVFkeDJsU09vSU9DRDdHam1Rck1iZDZkTloyOGNrc3R1R2NrZVg1eWdy\n" +
//           " ajF5UVB5bzU0ajVKUFlvQnljNGpkc2RTaTd3UHhYTkhQRUhHUzNRRmlPRHdm\n" +
//           " ZXJST29ialJaQUVlWGxWQjFKeFdkVjJnN0cxQ1BOTlJOdDVFVTI4Y1JTU1NS\n" +
//           " dGc1NllQUCtOZWJVZzRQa1BZazdhRWw1NUVWd1UyZ2tkY0h2VGhoNXpWMGhK\n" +
//           " U2FPWENrL3dETFE0cjFqd2l0ZDNkdllvcjNOeUlsWTRHNDlhVGRnU0tkM3FF\n" +
//           " M252YTJNNWphTWZ2N2dIL1Yvd0N5cC92ZS9iNjFMZHdlaHhFNSsxVDNOeXhk\n" +
//           " a1Q3dTVpU1NlQVQvQURyTjdsbTk4UDhBUy9FTjM0Z3RMblJvYnN0RktyTktn\n" +
//           " WVJMODNJZHVtTURvZXRRVTlEMFQ0dzZCNGdHdVJhcHByYWhjMk0wWUR4UXM3\n" +
//           "ckM2OEg1UjBCR0RuMXpUUUhtTTBPb09FRXRsZU00NmlTSmovTVZlZ0p5VEVY\n" +
//           " N1hiNGFDRzV0Mlk0NFFybWw3b1hrOTJhK202anFQbUJabzd5YUk4RmlqTVYv\n" +
//           " U3FqSklsbzZJQUtraXllWUgyL0p6amFjanFNYzhaOUt1OTlpUm9ab2cwbTgv\n" +
//           " S3BQV3VmRXk1WVhYZEhWZ2wrOXVYTk90VHBzU1hrdDI3eXpSNzhLQUFpa2RP\n" +
//           " ZXA2VngxSzN0SHFqMDVQbmV4emQzSmNUWEx2SGVrcVR3WlBsUCtmZXRsaUhZ\n" +
//           " Zk8xME9rMHV4L3RHNkVjdDFCWjI0d1pMaVo4S2dKeDN4a25zSzdaU3Nqd1Vy\n" +
//           " bmUzSHczOE0zVmlrRjdhU1hzOFlKV1V5dW5KOWxJR1AxckdVcm1pVmlEU1Bo\n" +
//           " ajRiMDYwZ2huMHRiaHRtWlpKcnFSdHo5L2t6dHhTdTBGa2I4WGhUUUlBRGFh\n" +
//           " Tm84TEEvZkZpak5qMko3MFhHYjI5WTRkaU1vd01EQzhEOEtRSHo5cXR0OFRZ\n" +
//           " TmVHbzNUMzF5YmVRc3FLNU1MWTR5RVFnWUkrbFlmdkZxZXMxZzVXVVhaSFQ2\n" +
//           " VnJtdnoyYzl4cTJrcmJpSkMyN3ppR2ZBendoQkkvRTFkT1VwYU5ISGlxVkds\n" +
//           " L0RuZit2NjZETGp4RWt5UVNpM2tSVllTY1Njc09tQ01aeHpWdE5ObzVsWnE2\n" +
//           " SnA5VnZiclM1SjlLMDZPZVFIYnNlZlljOEhqSXdldnFLbHVYUTFwcUVwV2s3\n" +
//           " STVHS0xXVGMzVjNyTU1jRWx3eTdZb3psVndvQnh5Y1p3RDE5YTFvdVRUNWhZ\n" +
//           " cU5LTFNwTzVGcVZ4NVZyNWEvZmtPUHdybnhVcnRSTjhGQ3ljamF2b2ltbndX\n" +
//           " eFB6ZVQ1Wi9BWXJpVDFPdlk0OXRMTzQ1a0dmcFZoZGRUcS9HWGhLS2FmVGJP\n" +
//           " RjV4YXNaSlp3U1d5VjJoZUJqdTV6N1pydlZSeTNQS2NWRTlHOEkzejNmaGpU\n" +
//           " eTJWbFdMeVNyTms1akpROWY5Mm1TYm0yVGNNNEErdEFIbkh4ZDFqVnJTejBq\n" +
//           " UjlIbGtpbjFLZHd6eE5oc0tGNHo2Zk5uOEtHTWorR0d1YWpETGVhRnF0ODkz\n" +
//           " SkNOOFRTSExLQWNNTjJUa2NqSHBnMFhFYWZpM3h4cFdqVHh3enZOSk0yVDVj\n" +
//           " SUJLajN5UmlwYjdGeGltOVhZNU4vaWRZY2hMVzQyOWd5ai93Q0txa0tXanNu\n" +
//           " Y3JOOFM3TVoyMk1uNGtEK3RNa2hiNG94SURzc0dQMWVsb0JyK0ZQRkZwNHMx\n" +
//           " VjdDWFRXVVJ4Tk1YZVRjQnlCNkQxcVpTYTJORW90RkRXN1pHOGJRMmx0RURI\n" +
//           " RzZzVUdPRlVibS9rYTQ1TnlrMnowYVNVS1NKOVV1bEYwckhuYUR0NTZFLy9y\n" +
//           " ckpJcTV6elM1WmkyY2sxZGhIcDJxV3JTeENiekxtYllDQWR1UXVjSElFYTdq\n" +
//           " a2dBOWV2dFhWQ1N1ZWRLSjUxWVdtcHlhemMvYm0xZlM0SkRKTWhqdDVqaGkr\n" +
//           " Y0FEMkpQNFZUa0pMdVRYMnNMcHBieVBGK3Nram9zbHRNTWZYZFFteE5Jb2FS\n" +
//           " ck0rditKZFBXN3ZialVtaFM0S1FrRlhBTVJMWUpHQVNGNDY4aXJWM3VJWjRu\n" +
//           " VXg2bkg1VEZXRnF2bWhlQ0d5Y2hpT0MyYzV4M3pSTFFFYzFHTFJwVU54ZFF4\n" +
//           " cG41Z1g1SDVBNHFOUjZFMGQxQnVrRU5yWkVIL1ZzOG44OHNUVmFpc0xkcEpi\n" +
//           " eHl5RzQwaDBUR0dnalpnMmZRRUE5ZU9sRjJCbFFhakxOSVkvczlvVGpJUGxE\n" +
//           " bWhpVFBidkFlZzJHajJYMnhyaUY3NjhoUjNWTUR5MTY3UVBxZWZwV2JsYzBS\n" +
//           " RHBVVnVOYzFxK3VIVnBaUVZoVUhrS2M1UDZDc3FTM1oyMW0xeXhPUzFWcDdh\n" +
//           " NjJ1VktjN1d5VG42ODlhNllUcHk2SFVuRm5SZUU5UGptMGg1WkltZmZNeERN\n" +
//           " T3ZBSEh0a0dpVFY5Q1pwSm5vaVNydExvNEtyM0hldWRIbkE3QXg3c2dET01Z\n" +
//           " SE5NQkxpT054NWNrYU11TTRZWlg4alNBb3krSHRLbUN2SFpXMFRrNVdXS0pR\n" +
//           " Nm5zUVFQNThVMUpyWWx4UnkrcC9EYUcvdVpaSTlYbmlubWZjN1NRcTVJeDZM\n" +
//           " dEEvbFZPVGJ1eGNxUzBNOVBneHByT0JMcTl5U0Q4d1dJTC9NbWpuWXVWRWlm\n" +
//           " QmZTaHRENmxLNVA4UlVESjlnRFJ6c09VbFB3YTBuYVZXOGt6NkhQL0FNVlQ1\n" +
//           " MkhLZ2grRDJud3Z4Y0JmY0t4L3JTNTJOUU5KdkFFVm5HWjJ2MllKL2RpNi93\n" +
//           "RGoxTk8rZ2ZEcVJRMnYyKytFY0VNVVhsQmszN1FBcWYzbUkrOGZjODU3MWM0\n" +
//           " cU1kTkNvMUhKKzlxY1ZxNUxUUEZLQ0Nwd1FleDlLNDBkNjFkenFOT3ZVMC9T\n" +
//           " N1MzUWNMRXA2a2RSVXVUdnVQbFoxazl5Vk81Qkx0VC9BRmdVN1FQVSsvcndh\n" +
//           " M3VjQStPOXRwMkNSeXBLNEJHUS9HUFdsb0E3N1RERklFQklPTTRJMjgvbnor\n" +
//           " VlBRUVMzbTFtd3E3Q3Vjc3dLdHg3ZjFvdU1aWlh6UGNTSVVkR1U0Wm5BRzcy\n" +
//           " SHJ4NzBYQ3hka3VXUWZLcXNnNEkzYzVvRllZSjVwSll3REdIR2QzYzQ2ZTJP\n" +
//           " MU1kaVl6YlluYmF6WTZqUDNqN1pvRU1XNkhuQkNTV0F5TVlKWDJwWEFXV1h6\n" +
//           " QTBUeWtxZmxLQUhrSDZjMDAybmRDY1UxWmxUN0ZiMjdyNUZ1eWdZUHlTTno5\n" +
//           " Um5CcXBTY3R3akJSMlBOL0cxbDlrMTJiYU1MTWZNVWY3M1g5YzFnMGR0SjNp\n" +
//           " aU82dVZpbEVlUjhxZ1ZDUnU5RC8vMlE9PQ==";
//
//    	   byte[] b = Base64.decode(base);
//    	   
//    	   System.out.write(b, 0, b.length);
//    }
}
