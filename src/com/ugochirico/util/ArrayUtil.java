package com.ugochirico.util;

public class ArrayUtil 
{
	public static byte arrayCompare(byte[] arg1, int offset1, byte[] arg2, int offset2, int length)
	{
		int i;
		for(i = 0; i < length && arg1[i + offset1] == arg2[i + offset2]; i++);
		
		return (byte)(i == length ?
						0 : (arg1[i + offset1] < arg2[i + offset2] ? -1 : 1));
	}  
	
	public static byte[] suburray(byte[] src, int start, int length)
	{
		byte[] data = new byte[length];
		
		System.arraycopy(src, start, data, 0, length);
		
		return data;
	}
	
	public static byte[] suburray(byte[] src, int start)
	{
		byte[] data = new byte[src.length - start];
		
		System.arraycopy(src, start, data, 0, data.length);
		
		return data;
	}
	
}
