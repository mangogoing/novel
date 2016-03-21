package com.ushaqi.zhuishushenqi.util;

public class ByteUtil
{
  public static byte[] getArrayByte(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramArrayOfByte.length;
    int j = paramInt2 - paramInt1;
    int k = Math.min(j, i - paramInt1);
    byte[] arrayOfByte = new byte[j];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, k);
    return arrayOfByte;
  }
}

/* Location:           C:\Users\newre\Desktop\onekey-decompile-apk-1.0.1\cyzs.1.9.1_signed.apk.jar
 * Qualified Name:     com.ushaqi.zhuishushenqi.util.a
 * JD-Core Version:    0.6.1
 */