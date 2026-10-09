package com.nickuc.login.security.hashing;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.sha.Sha256Service;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.PlatformState;
import com.nickuc.login.model.UpstreamSpawnState;
import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import at.favre.lib.crypto.bcrypt.BCrypt.Version;
import com.nickuc.login.platform.server.LinkedServerAdapter;
import com.nickuc.login.premium.SafePasswordLookup;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordVerifier implements LinkedServerAdapter {
   public static final MessageOption messageOption = MessageOption.LOCAL_MESSAGEOPTION;
   public static final String name = "BARONESS";

   @Override
   public boolean verifyState(String target, String input) {
      if (input.length() < "BARONESS".length() + 1) {
         return false;
      }

      try {
         byte[] output = Base64.getDecoder().decode(input.substring("BARONESS".length() + 2).getBytes(StandardCharsets.UTF_8));
         DataInputStream context = new DataInputStream(new ByteArrayInputStream(output));

         int reference;
         label63: {
            boolean attribute;
            label62: {
               label61: {
                  try {
                     int data = Math.toIntExact(context.readLong());
                     PlatformState value = PlatformState.resolvePlatformState(data);
                     if (value == null) {
                        PasswordHashContainer.performMessage("[" + messageOption.getName() + "] Unsupported hashing algorithm! hash id = " + data);
                        reference = 0;
                        break label63;
                     }

                     switch (value) {
                        case LOCAL_PLATFORMSTATE:
                           String holder = context.readUTF();
                           String subject = context.readUTF();
                           int option = context.readInt();
                           String setting = target;

                           for (int property = 0; property < option; property++) {
                              int record = context.readInt();
                              setting = loadMessage(record, setting, holder);
                           }

                           attribute = subject.equals(setting);
                           break label62;
                        case STORED_PLATFORMSTATE:
                           reference = context.read();
                           byte[] request = SafePasswordLookup.buildPayload(context, 16);
                           byte[] response = SafePasswordLookup.buildPayload(context, 24);
                           Result source = BCrypt.verifyer(Version.VERSION_BC).verify(loadPayload(target), reference, request, response);
                           attribute = source.verified;
                           break label61;
                        default:
                           PasswordHashContainer.performMessage("[" + messageOption.getName() + "] Unsupported hashing algorithm! type = " + value);
                           reference = (byte)0;
                     }
                  } catch (Throwable element) {
                     try {
                        context.close();
                     } catch (Throwable item) {
                        element.addSuppressed(item);
                     }

                     throw element;
                  }

                  context.close();
                  return (boolean)reference;
               }

               context.close();
               return attribute;
            }

            context.close();
            return attribute;
         }

         context.close();
         return (boolean)reference;
      } catch (Exception content) {
         PasswordHashContainer.processMessage("[" + messageOption.getName() + "] Unable to verify the password!", content);
         return false;
      }
   }

   private static byte[] loadPayload(String instance) {
      byte[] target = instance.getBytes(StandardCharsets.UTF_8);
      if (target.length > 72) {
         target = MessageDigest.getInstance("SHA-512/256").digest(target);
      }

      return target;
   }

   private static String handleMessage(String instance, String target) {
      MessageDigest input;
      try {
         input = MessageDigest.getInstance(target);
      } catch (NoSuchAlgorithmException context) {
         throw new RuntimeException(context);
      }

      input.reset();
      input.update(instance.getBytes(StandardCharsets.UTF_8));
      byte[] output = input.digest();
      return String.format("%0" + (output.length << 1) + "x", new BigInteger(1, output));
   }

   private static String loadMessage(int instance, String target, String input) {
      switch (instance) {
         case 1:
            return target;
         case 2:
            return target + input;
         case 3:
            return handleMessage(target, "MD2");
         case 4:
            return handleMessage(target, "MD4");
         case 5:
            return ((PendingPasswordHashDigest)UpstreamSpawnState.CACHED_UPSTREAMSPAWNSTATE.loadIndirectPlayerContract()).loadMessage(target);
         case 6:
            return handleMessage(target, "SHA-0");
         case 7:
            return handleMessage(target, "SHA-1");
         case 8:
            return handleMessage(target, "SHA-224");
         case 9:
            return ((Sha256Hasher)UpstreamSpawnState.REMOTE_UPSTREAMSPAWNSTATE.loadIndirectPlayerContract()).handleMessage(target);
         case 10:
            return handleMessage(target, "SHA-384");
         case 11:
            return ((Sha256Service)UpstreamSpawnState.LOCAL_UPSTREAMSPAWNSTATE.loadIndirectPlayerContract()).handleMessage(target);
         case 12:
            return handleMessage(target, "SHA3-224");
         case 13:
            return handleMessage(target, "SHA3-256");
         case 14:
            return handleMessage(target, "SHA3-384");
         case 15:
            return handleMessage(target, "SHA3-512");
         case 16:
            return handleMessage(target, "Tiger");
         case 17:
            return handleMessage(target, "Tiger2");
         case 18:
            return handleMessage(target, "Tiger-128");
         case 19:
            return handleMessage(target, "Tiger-160");
         case 20:
            throw new UnsupportedOperationException("Unsupported algorithm: Blake2b160");
         case 21:
            throw new UnsupportedOperationException("Unsupported algorithm: Blake2b256");
         case 22:
            throw new UnsupportedOperationException("Unsupported algorithm: Blake2b384");
         case 23:
            throw new UnsupportedOperationException("Unsupported algorithm: Blake2b512");
         case 24:
            return handleMessage(target, "Whirlpool-0");
         case 25:
            return handleMessage(target, "Whirlpool-1");
         case 26:
            return handleMessage(target, "Whirlpool");
         case 27:
            return handleMessage(target, "CRC-8");
         case 28:
            return handleMessage(target, "CRC-16");
         case 29:
            return handleMessage(target, "CRC-24");
         case 30:
            return handleMessage(target, "CRC-32");
         case 31:
            return handleMessage(target, "CRC-64");
         default:
            throw new UnsupportedOperationException("Unsupported algorithm: " + instance);
      }
   }
}
