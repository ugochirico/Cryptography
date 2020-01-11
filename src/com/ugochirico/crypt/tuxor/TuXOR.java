package com.ugochirico.crypt.tuxor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.ugochirico.math.BigInteger;
import com.ugochirico.util.Encoder;

public class TuXOR
{
    private byte[] m_key;
    private int m_blocksize;
    private byte[] m_tmpBuffer;

    private boolean _padding;
//    public TuXOR(byte[] key)
//    {
//        this(key, key.length);
//    }

    public TuXOR(byte[] key, int blocksize)
    {
        m_key = key;
        m_blocksize = blocksize;
        m_tmpBuffer = new byte[blocksize];
        _padding = true;
    }

    public void setPadding(boolean padding)
    {
        _padding = padding;
    }
    
    public int getBlockSize()
    {
        return m_blocksize;
    }

    public byte[] encryptCBC(byte[] plaintext, int nOffset, int length, byte[] iv)
    {
        byte[] block;
        if(_padding)
        {
            // pad
            block = pad(plaintext, nOffset, length);
        }
        else
        {
            if(length % m_blocksize != 0)
                throw new IllegalArgumentException("Invalid input size");
            
            block = new byte[length];
            System.arraycopy(plaintext, nOffset, block, 0, length);    
        }
        
        byte[] tmpiv = new byte[m_blocksize];
        System.arraycopy(iv, 0, tmpiv, 0, m_blocksize);

        nOffset = 0;

        ByteArrayOutputStream outsCipherText = new ByteArrayOutputStream();
        while(nOffset < block.length)
        {
            xor(block, nOffset, tmpiv, 0, block, nOffset, m_blocksize);

//            System.out.println("xor: " + Encoder.bytesToHexString(block));

            tuxorBlock(block, nOffset, block, nOffset);

//            System.out.println("tuxor: " + Encoder.bytesToHexString(block));

//            obfuscate(block, nOffset, 0, m_blocksize);

//            System.out.println("obf: " + Encoder.bytesToHexString(block));

            for(int i = 0; i < m_key.length; i++)
            {
                obfuscate(block, nOffset, i, m_blocksize);
            }

//            System.out.println(Encoder.bytesToHexString(encblock));


            outsCipherText.write(block, nOffset, m_blocksize);
            System.arraycopy(block, nOffset, tmpiv, 0, m_blocksize);
            nOffset += m_blocksize;
        }

        return outsCipherText.toByteArray();
    }

    public byte[] decryptCBC(byte[] ciphertext, int nOffset,  int length, byte[] iv)
    {
        if(length % m_blocksize != 0)
            throw new IllegalArgumentException("Invalid input size");
        
        ByteArrayOutputStream outs = new ByteArrayOutputStream();

        byte[] block = new byte[length];
        System.arraycopy(ciphertext, nOffset, block, 0, length);

        nOffset = 0;

        byte[] tmpiv = new byte[m_blocksize];
        System.arraycopy(iv, 0, tmpiv, 0, m_blocksize);

        while(nOffset < block.length)
        {
//            System.out.println("cipher: " + Encoder.bytesToHexString(block));

            for(int i = 0; i < m_key.length; i++)
            {
                deobfuscate(block, nOffset, i, m_blocksize);
            }
//            deobfuscate(ciphertext, nOffset, 0, m_blocksize);

//            System.out.println("deobf: " + Encoder.bytesToHexString(ciphertext));

            detuxorBlock(block, nOffset, block, nOffset);

//            System.out.println("detuxor: " + Encoder.bytesToHexString(ciphertext));

            xor(block, nOffset, tmpiv, 0, block, nOffset, m_blocksize);

//            System.out.println("xor: " + Encoder.bytesToHexString(block));

            outs.write(block, nOffset, m_blocksize);

            System.arraycopy(block, nOffset, tmpiv, 0, m_blocksize);

            nOffset += m_blocksize;
        }

//        System.out.println(Encoder.bytesToHexString(outs.toByteArray()));

        if(_padding)
            return unpad(outs.toByteArray());
        else
            return outs.toByteArray();
    }


    private byte[] pad(byte[] plaintext, int nOffset, int length)
    {
        ByteArrayOutputStream outsPlainText = new ByteArrayOutputStream();

        // check padding

        int remainder = length % m_blocksize;
        int nPadLen = m_blocksize - remainder - 1;

        outsPlainText.write(nPadLen);
        for(int i = 0; i < nPadLen; i++)
        {
            outsPlainText.write((char)0x00);
        }

        // write plaintext
        outsPlainText.write(plaintext, nOffset, length);

        return outsPlainText.toByteArray();
    }

    private static byte[] unpad(byte[] plaintext)
    {
//      check padding
        int nPadLen = plaintext[0];

        // check FF
        int i;
        for(i = 0; i < nPadLen && plaintext[i + 1] == 0x00; i++);

//      #ifdef LOG
//        LogViewer.logMsg("pad len " + nPadLen);
//        LogViewer.logMsg("plainText[i] " + plainText[i]);
//        LogViewer.logMsg("plainText[i + 1] " + plainText[i + 1]);
//        LogViewer.logMsg("i " + i);
        //#endif

        if(nPadLen != i)
            throw new IllegalArgumentException();

//        System.out.println("Padlen:"+ nPadLen);
//        System.out.println(Encoder.bytesToHexString(plainText));

        ByteArrayOutputStream outsPlainText = new ByteArrayOutputStream();
        outsPlainText.write(plaintext, nPadLen + 1, plaintext.length - nPadLen - 1);

        //System.out.write(outsPlainText.toByteArray(), 0, outsPlainText.toByteArray().length);
        return outsPlainText.toByteArray();
    }

    private void xor(byte[] block, int offsetblok, byte[] iv, int offsetiv, byte[] xoredblock, int offserxored, int blocklen)
    {
        for(int i = 0; i < blocklen; i++)
        {
            xoredblock[i + offserxored] = (byte)(block[i + offsetblok] ^ iv[i + offsetiv]);
        }
    }

    public void tuxorBlock(
            byte[] in,
            int inOff,
            byte[] out,
            int outOff)
    {
        for(int i = 0; i < m_blocksize; i++)
        {
            out[i + outOff] = (byte)((in[i + inOff] ^ m_key[i]) ^ (byte)m_blocksize);//(byte)i);
            //out[(i % m_key[i]) + outOff] = (byte)((in[i + inOff] ^ m_key[i]) ^ (byte)i);
        }

//        obfuscate(out, outOff, 0, blocksize);
//        System.arraycopy(in, inOff, out, outOff, blocksize);
//        obfuscate(out,outOff, true, blocksize);
    }

    public void detuxorBlock(
            byte[] in,
            int inOff,
            byte[] out,
            int outOff)
    {
        int blocksize = m_blocksize;

//        deobfuscate(in, inOff, 0, blocksize);

        for(int i = 0; i < m_blocksize; i++)
        {
            out[i + outOff] = (byte)((in[i + inOff] ^ (byte)m_blocksize/*(byte)i*/) ^ m_key[i]);
        }


    }

    private void obfuscate(byte[] m, int offset, int keyIndex, int blocksize)
    {
//        System.out.println("before " + Encoder.bytesToHexString(m));

        for(int i = 0; i < blocksize; i++)
        {
            m_tmpBuffer[i] = m[offset + ((i + Math.abs(m_key[keyIndex])) % blocksize)];
            //swap(m, offset + i, offset + (m_key[keyIndex] % blocksize));
        }

        System.arraycopy(m_tmpBuffer, 0, m, offset, blocksize);

//        System.out.println("after " + Encoder.bytesToHexString(m));
//        for(int i = 0; i < m_key.length; i++)
//        {
//            swap(m, offset + (i % blocksize), offset + m_key[i] % blocksize);
//        }
    }

    private void deobfuscate(byte[] m, int offset, int keyIndex, int blocksize)
    {
//        System.out.println("before " + Encoder.bytesToHexString(m));

        for(int i = 0; i < blocksize; i++)
        {
            m_tmpBuffer[(i + Math.abs(m_key[keyIndex])) % blocksize] = m[offset + i];
            //swap(m, offset + i, offset + (m_key[keyIndex] % blocksize));
        }

        System.arraycopy(m_tmpBuffer, 0, m, offset, blocksize);

//        System.out.println("after " + Encoder.bytesToHexString(m));
//        for(int i = 0; i < m_key.length; i++)
//        {
//            swap(m, offset + (i % blocksize), offset + m_key[i] % blocksize);
//        }
    }


//    // ENCODE-DECODE ROUTINE
//    private void obfuscate1(byte[] m, int offset, boolean action, int blocksize)
//    {
//        int i,j;
//
//        int a; //key 1
//        int b; //key 2
//        int c; //key 3
//        int d; //key 4
//        int z;
//        int n = blocksize - 1;
//
//        a = rand(-9999, 9999);
//        b = rand(-9999, 9999);
//        c = rand(-9999, 9999);
//        d = rand(-9999, 9999);
//
//        z = rand(0, 8);
//
//        z = 3 + (z + (z % 2));  // sempre dispari!
//
//        if (action) // codifica
//        {
//            for (i = 1; i <= n; i++)
//               DirOverLapping(m, i + offset, i + offset - 1, a, b, z);
//
//            for (i = n - 1; i >= 0; i--)
//               DirOverLapping(m, i + offset, i + offset + 1, c, d, z);
//        }
//        else //-------- decodifica -------------------------n
//        {
//            for (i = 0; i <= n - 1; i++)
//                InvOverLapping(m, i + offset, i + offset + 1, c, d, z);
//
//             for (i = n; i >= 1; i--)
//                InvOverLapping(m, i + offset, i + offset - 1, a, b, z);
//
//        }
//    }

    //2pht Byte process in OverLapping  (x overLapping y)
    public void DirOverLapping(byte[] buffer, int i1, int i2, int p, int k, int n)
    {
        buffer[i1] = (byte)(p - (buffer[i1] + n * buffer[i2]) & 255);

        buffer[i2] = (byte)(k - (buffer[i2] + (n + 1) * buffer[i1]) & 255);
    }

    public void swap(byte[] buffer, int i1, int i2)
    {
        byte c = buffer[i1];
        buffer[i1] = buffer[i2];
        buffer[i2] = c;
    }

    //2pht Byte process in OverLapping (x overLapping y)
    public void InvOverLapping(byte[] buffer, int i1, int i2, int p, int k, int n)
    {
        buffer[i2] = (byte)((k - buffer[i2] - (n + 1) * buffer[i1]) & 255);
        buffer[i1] = (byte)(p - (buffer[i1] + n * buffer[i2]) & 255);
    }

    // A random number in the range (low, high)
//    private int rand(int low, int high)
//    {
//       return (int)(Math.random() * (high - low) + low);
//    }



//    public static void main(String[] args)
//    {
////        byte[] bt = new BigInteger("259991497274521704595244386646813648789").toByteArray();
////        System.out.println(bt.length);
////        System.out.println(Encoder.bytesToHexString(bt));
////
//
//
//        TuXOR xor = new TuXOR("iolone doppio cicciolone doppio cicciolone doppio".getBytes(), 6);
//
//        byte[] ba = "ugo chirico".getBytes();
//
//        xor.obfuscate(ba, 0, 0, 5);
//        System.out.println("obf: " + Encoder.bytesToHexString(ba));
//        xor.deobfuscate(ba, 0, 0, 5);
//        System.out.println("deobf: " + Encoder.bytesToHexString(ba));
//
//
//        byte[] b = xor.encryptCBC("ugo".getBytes(), 0, "ugo".getBytes().length, "doppio cicciolone doppio cicciolone doppio".getBytes());
//
//        System.out.println("Enc: " + Encoder.bytesToHexString(b));
//
//        byte[] c = xor.decryptCBC(b, 0, b.length, "doppio cicciolone doppio cicciolone doppio".getBytes());
//
//        System.out.println("Dec: " + Encoder.bytesToHexString(c));
//
//        System.out.println(new String(c));
//        
//        xor.setPadding(false);
//        b = xor.encryptCBC("ciccio".getBytes(), 0, "ciccio".getBytes().length, "doppio cicciolone doppio cicciolone doppio".getBytes());
//
//        c = xor.decryptCBC(b, 0, b.length, "doppio cicciolone doppio cicciolone doppio".getBytes());
//
//        System.out.println("Dec: " + Encoder.bytesToHexString(c));
//
//        System.out.println(new String(c));
//    }
}
