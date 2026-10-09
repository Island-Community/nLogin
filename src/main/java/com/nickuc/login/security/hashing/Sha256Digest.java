package com.nickuc.login.security.hashing;

import com.github.retrooper.packetevents.util.crypto.SignatureData;
import com.nickuc.login.session.Sha256Tracker;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PublicKey;
import java.time.Instant;
import java.util.UUID;


public class Sha256Digest {
   private final SignatureData signatureData;
   private final UUID uniqueId;

   public boolean retrieveState() {
      byte[] target = this.signatureData.getSignature();
      if (this.uniqueId != null) {
         byte[] data = this.signatureData.getPublicKey().getEncoded();
         byte[] output = new byte[data.length + 24];
         ByteBuffer context = ByteBuffer.wrap(output).order(ByteOrder.BIG_ENDIAN);
         context.putLong(this.uniqueId.getMostSignificantBits());
         context.putLong(this.uniqueId.getLeastSignificantBits());
         context.putLong(this.signatureData.getTimestamp().toEpochMilli());
         context.put(data);
         return Sha256Tracker.isState("SHA1withRSA", Sha256Tracker.resolvePublicKey(), target, output);
      } else {
         byte[] input = this.signatureData.getSignature();
         return Sha256Tracker.isState("SHA1withRSA", Sha256Tracker.resolvePublicKey(), target, input);
      }
   }

   public boolean resolveState() {
      return this.signatureData.getTimestamp().isBefore(Instant.now());
   }

   private byte[] computePayload(long target) {
      byte[] output = new byte[8];

      for (int context = 7; context >= 0; context--) {
         output[context] = (byte)(target & 255L);
         target >>= 8;
      }

      return output;
   }

   public boolean isState(byte[] target, byte[] input, long output) {
      try {
         PublicKey data = this.signatureData.getPublicKey();
         return Sha256Tracker.isState("SHA256withRSA", data, target, input, this.computePayload(output));
      } catch (IllegalArgumentException value) {
         return false;
      }
   }

   public Sha256Digest(SignatureData target, UUID input) {
      this.signatureData = target;
      this.uniqueId = input;
   }
}
