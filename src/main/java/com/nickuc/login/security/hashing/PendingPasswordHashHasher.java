package com.nickuc.login.security.hashing;

import com.nickuc.login.auth.message.FastMessageHandler;
import com.nickuc.login.auth.login.LocalLoginBarrier;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.auth.login.VerifiedLoginGate;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class PendingPasswordHashHasher {
   private int count;
   private int activeCount;
   private int pendingCount;
   private static String name = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
   private final Map<String, String> sessions = new HashMap<>();

   private void processHttpURLConnection(HttpURLConnection target, String input) {
      target.setInstanceFollowRedirects(false);
      if (input != null && !input.isEmpty()) {
         try {
            target.setRequestMethod(input);
         } catch (ProtocolException context) {
            throw new IllegalArgumentException("HTTP method \"" + input + "\" does not exists!", context);
         }
      }

      this.sessions.putIfAbsent("User-Agent", name);
      this.sessions.forEach(target::setRequestProperty);
      target.setConnectTimeout(this.pendingCount);
      target.setReadTimeout(this.activeCount);
   }

   public static PendingPasswordHashHasher getPendingPasswordHashHasher() {
      return new PendingPasswordHashHasher();
   }

   public PendingPasswordHashHasher computePendingPasswordHashHasher(int target) {
      this.activeCount = target;
      return this;
   }

   public Map<String, String> findTable() {
      return this.sessions;
   }

   public int resolveCount() {
      return this.pendingCount;
   }

   private boolean isState(int target) {
      return target == 302 || target == 301 || target == 303;
   }

   public VerifiedLoginGate processVerifiedLoginGate(String target, byte[] input) {
      HttpURLConnection output = null;

      try {
         this.sessions.putIfAbsent("Connection", "close");
         this.sessions.putIfAbsent("Content-Length", Integer.toString(input.length));
         output = (HttpURLConnection)new URL(target).openConnection();
         this.processHttpURLConnection(output, "POST");
         output.setDoOutput(true);
         DataOutputStream context = new DataOutputStream(output.getOutputStream());

         try {
            context.write(input);
            context.flush();

            int data;
            do {
               int value = output.getResponseCode();
               data = this.isState(value) && this.count++ < 20 ? 1 : 0;
               if (data != 0) {
                  output.disconnect();
                  String result = output.getHeaderField("Location");
                  output = (HttpURLConnection)new URL(result).openConnection();
                  this.processHttpURLConnection(output, null);
                  output.setDoOutput(true);
                  DataOutputStream request = new DataOutputStream(output.getOutputStream());

                  try {
                     request.write(input);
                     request.flush();
                  } finally {
                     if (Collections.singletonList(request).get(0) != null) {
                        request.close();
                     }
                  }
               }
            } while (data != 0);

            return this.handleVerifiedLoginGate(output);
         } finally {
            if (Collections.singletonList(context).get(0) != null) {
               context.close();
            }
         }
      } catch (SocketTimeoutException message) {
         throw new SocketTimeoutException("[POST] The connection took too long to be answered.");
      } finally {
         if (output != null) {
            output.disconnect();
         }
      }
   }

   public int fetchCount() {
      return this.count;
   }

   public VerifiedLoginGate buildVerifiedLoginGate(String target) {
      HttpURLConnection input = null;

      try {
         input = (HttpURLConnection)new URL(target).openConnection();
         this.processHttpURLConnection(input, "GET");

         int output;
         do {
            int context = input.getResponseCode();
            output = this.isState(context) && this.count++ < 20 ? 1 : 0;
            if (output != 0) {
               input.disconnect();
               String data = input.getHeaderField("Location");
               input = (HttpURLConnection)new URL(data).openConnection();
               this.processHttpURLConnection(input, "GET");
            }
         } while (output != 0);

         return this.handleVerifiedLoginGate(input);
      } catch (SocketTimeoutException response) {
         throw new SocketTimeoutException("[GET] The connection took too long to be answered.");
      } finally {
         if (input != null) {
            input.disconnect();
         }
      }
   }

   public void updateMessage(String target, String input) {
      this.sessions.put(target, input);
   }

   private VerifiedLoginGate handleVerifiedLoginGate(HttpURLConnection target) {
      this.count = 0;
      int input = target.getResponseCode();

      try {
         InputStream output = target.getErrorStream();

         try {
            if (output == null) {
               output = target.getInputStream();
            }

            byte[] context = FastMessageHandler.handlePayload(output);
            return new VerifiedLoginGate(context, input);
         } finally {
            if (Collections.singletonList(output).get(0) != null) {
               output.close();
            }
         }
      } catch (IOException source) {
         return new VerifiedLoginGate(null, input, source);
      }
   }

   public LocalLoginBarrier buildLocalLoginBarrier(String target, File input, int output) {
      try {
         return this.computeLocalLoginBarrier(target, input, output);
      } catch (IOException data) {
         return new LocalLoginBarrier(false, 0, 0L, data);
      }
   }

   public void sendMessage(String target, Object input) {
      this.sessions.put(target, input.toString());
   }

   public static void processMessage(String instance, String target) {
      name = instance + "/" + target + " on nCore";
   }

   public LocalLoginBarrier computeLocalLoginBarrier(String target, File input, int output) {
      HttpURLConnection context = null;

      try {
         context = (HttpURLConnection)new URL(target).openConnection();
         this.processHttpURLConnection(context, "GET");

         int data;
         do {
            int value = context.getResponseCode();
            data = this.isState(value) && this.count++ < 20 ? 1 : 0;
            if (data != 0) {
               context.disconnect();
               String result = context.getHeaderField("Location");
               context = (HttpURLConnection)new URL(result).openConnection();
               this.processHttpURLConnection(context, "GET");
            }
         } while (data != 0);

         this.count = 0;
         long task = context.getContentLengthLong();
         int request = context.getResponseCode();
         if (output > 0 && output > task) {
            return new LocalLoginBarrier(false, request, task, null);
         }

         if (input.exists() && !input.delete()) {
            throw new IOException("Unable to delete " + input + " file.");
         }

         try {
            InputStream response = context.getErrorStream();

            try {
               if (response == null) {
                  response = context.getInputStream();
                  if (!input.exists() && !input.createNewFile()) {
                     throw new IOException("Unable to create " + input + " file.");
                  }

                  BufferedInputStream source = new BufferedInputStream(response);

                  try {
                     BufferedOutputStream entry = MessageProcessor.computeBufferedOutputStream(input);

                     try {
                        FastMessageHandler.processInputStream(source, entry);
                        return new LocalLoginBarrier(input.exists() && input.length() > 0L, request, task);
                     } finally {
                        if (Collections.singletonList(entry).get(0) != null) {
                           entry.close();
                        }
                     }
                  } finally {
                     if (Collections.singletonList(source).get(0) != null) {
                        source.close();
                     }
                  }
               } else {
                  return new LocalLoginBarrier(false, request, task, null);
               }
            } finally {
               if (Collections.singletonList(response).get(0) != null) {
                  response.close();
               }
            }
         } catch (IOException state) {
            return new LocalLoginBarrier(false, request, task, state);
         }
      } catch (SocketTimeoutException status) {
         throw new SocketTimeoutException("[GET] The connection took too long to be answered.");
      } finally {
         if (context != null) {
            context.disconnect();
         }
      }
   }

   public VerifiedLoginGate handleVerifiedLoginGate(String target, byte[] input) {
      try {
         return this.processVerifiedLoginGate(target, input);
      } catch (IOException context) {
         return new VerifiedLoginGate(null, 0, context);
      }
   }

   public PendingPasswordHashHasher processPendingPasswordHashHasher(int target) {
      this.pendingCount = target;
      return this;
   }

   public LocalLoginBarrier loadLocalLoginBarrier(String target, File input) {
      return this.buildLocalLoginBarrier(target, input, 0);
   }

   public VerifiedLoginGate processVerifiedLoginGate(String target) {
      try {
         return this.buildVerifiedLoginGate(target);
      } catch (IOException output) {
         return new VerifiedLoginGate(null, 0, output);
      }
   }

   private PendingPasswordHashHasher() {
      this.pendingCount = 7500;
      this.activeCount = this.pendingCount * 3;
   }

   public int retrieveCount() {
      return this.activeCount;
   }
}
