package com.nickuc.login.auth.login;

import java.io.DataOutput;
import java.io.IOException;


public class ParentLoginCheckpoint {
   private final DataOutput dataOutput;

   public void savePayload(byte[] target) {
      try {
         this.dataOutput.write(target);
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public void executeCount(int target) {
      try {
         this.dataOutput.write(target);
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public void processMessage(String target) {
      try {
         this.dataOutput.writeUTF(target);
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public void performCount(int target) {
      try {
         this.dataOutput.writeByte(target);
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public void sendState(boolean target) {
      try {
         this.dataOutput.writeBoolean(target);
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public void performPayload(byte[] target, int input, int output) {
      try {
         this.dataOutput.write(target, input, output);
      } catch (IOException data) {
         throw new RuntimeException(data);
      }
   }

   public void dispatchFactor(float target) {
      try {
         this.dataOutput.writeFloat(target);
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public void saveCount(int target) {
      try {
         this.dataOutput.writeChar(target);
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public void updateCount(int target) {
      try {
         this.dataOutput.writeInt(target);
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public void dispatchCount(int target) {
      try {
         this.dataOutput.writeShort(target);
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public void executeRatio(double target) {
      try {
         this.dataOutput.writeDouble(target);
      } catch (IOException context) {
         throw new RuntimeException(context);
      }
   }

   public void sendMessage(String target) {
      try {
         this.dataOutput.writeBytes(target);
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public DataOutput retrieveDataOutput() {
      return this.dataOutput;
   }

   public ParentLoginCheckpoint(DataOutput target) {
      this.dataOutput = target;
   }

   public void handleMessage(String target) {
      try {
         this.dataOutput.writeChars(target);
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public void handleTime(long target) {
      try {
         this.dataOutput.writeLong(target);
      } catch (IOException context) {
         throw new RuntimeException(context);
      }
   }
}
