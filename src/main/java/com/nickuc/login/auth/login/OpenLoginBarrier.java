package com.nickuc.login.auth.login;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import javax.annotation.Nullable;

public class OpenLoginBarrier implements Closeable {
   private InputStreamReader inputStreamReader;
   private InputStream inputStream;
   private BufferedReader bufferedReader;

   @Override
   public void close() {
      if (this.bufferedReader != null) {
         this.bufferedReader.close();
         this.bufferedReader = null;
      }

      if (this.inputStreamReader != null) {
         this.inputStreamReader.close();
         this.inputStreamReader = null;
      }

      if (this.inputStream != null) {
         this.inputStream.close();
         this.inputStream = null;
      }
   }

   public OpenLoginBarrier(InputStream target, Charset input) {
      this.inputStream = target;
      this.inputStreamReader = new InputStreamReader(target, input);
      this.bufferedReader = new BufferedReader(this.inputStreamReader);
   }

   @Nullable
   public String findMessage() {
      return this.bufferedReader.readLine();
   }
}
