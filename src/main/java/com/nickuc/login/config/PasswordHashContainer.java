package com.nickuc.login.config;

import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.auth.locale.LocaleFlow;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.auth.locale.PendingLocaleBarrier;
import com.nickuc.login.auth.login.SafeLoginCheckpoint;
import com.nickuc.login.model.IncomingLoginOption;
import com.nickuc.login.platform.connection.ConnectionContract;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.proxy.SilentProxyState;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;


public final class PasswordHashContainer {
   private static boolean enabled;
   private static IndirectSessionHandler<?> indirectSessionHandler;
   private static boolean activeEnabled = true;
   private static PrintWriter printWriter;
   private static boolean pendingEnabled = true;
   private static String name = "";
   private static final Set<ConnectionContract> players = ConcurrentHashMap.newKeySet();

   public static void saveConnectionContract(ConnectionContract instance, ConnectionContract... target) {
      players.add(instance);
      players.addAll(Arrays.asList(target));
   }

   public static void handleMessage(String instance, Object... target) {
      performIncomingLoginOption(IncomingLoginOption.INCOMING_LOGIN_OPTION, instance, null, target);
   }

   public static void processMessage(String instance, @Nullable Throwable target, Object... input) {
      if (input.length > 0) {
         instance = String.format(instance, input);
      }

      if (pendingEnabled) {
         performIncomingLoginOption(IncomingLoginOption.PENDING_INCOMINGLOGINOPTION, instance, target);
      } else {
         if (!activeEnabled) {
            instance = LocalLocaleFlow.createMessage(instance, true);
         }

         indirectSessionHandler.fetchSenderAdapter().processMessage(instance, target);
         handleIncomingLoginOption(IncomingLoginOption.PENDING_INCOMINGLOGINOPTION, instance, target, activeEnabled);
      }
   }

   public static void saveState(boolean instance) {
      enabled = instance;
   }

   private static void handleIncomingLoginOption(IncomingLoginOption instance, String target, @Nullable Throwable input, boolean output) {
      if (printWriter != null) {
         try {
            if (input != null) {
               input.printStackTrace(printWriter);
            }

            LocaleFlow context = new LocaleFlow();
            String data = "["
               + context.findMessage()
               + "/"
               + context.resolveMessage()
               + "/"
               + context.retrieveMessage()
               + " "
               + context.fetchMessage()
               + ":"
               + context.getMessageForMessage()
               + ":"
               + context.loadMessageForMessage()
               + " "
               + instance.toString()
               + "]: "
               + (output ? LocalLocaleFlow.createMessage(target, true) : target);
            printWriter.println(data);
         } finally {
            printWriter.flush();
         }
      }
   }

   public static void processMessage(String instance, Object... target) {
      sendMessage(instance, null, target);
   }

   public static void handleIndirectSessionHandler(IndirectSessionHandler<?> instance, boolean target, boolean input) {
      if (indirectSessionHandler != null) {
         throw new IllegalStateException("Logger already set!");
      }

      int output = instance.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE ? 1 : 0;
      String context = instance.retrieveMessage();
      players.add(
         (inputValue, outputValue, contextValue) -> {
            String data = outputValue.toLowerCase(Locale.ENGLISH);
            return output && context.equals(inputValue) && data.contains("loaded class") && data.contains("which is not a depend or softdepend of this plugin")
               ? true
               : data.contains("://") && data.contains("nickuc.com");
         }
      );
      players.add(new PendingLocaleBarrier(ConcurrentHashMap.newKeySet(), "/ncore-"));
      indirectSessionHandler = instance;
      name = "[" + context + "] ";
      pendingEnabled = target;
      activeEnabled = input;
   }

   public static void processMessage(String instance, String... target) {
      players.stream()
         .filter(instanceValue -> instanceValue instanceof PendingLocaleBarrier)
         .findFirst()
         .ifPresent(input -> ((PendingLocaleBarrier)input).buildPendingLocaleBarrier(instance, target));
   }

   public static void sendThrowable(Throwable instance) {
      instance.printStackTrace();
      if (printWriter != null) {
         instance.printStackTrace(printWriter);
         printWriter.flush();
      }
   }

   public static void handleMessage(String instance, @Nullable Throwable target, Object... input) {
      if (input.length > 0) {
         instance = String.format(instance, input);
      }

      if (pendingEnabled) {
         performIncomingLoginOption(IncomingLoginOption.CURRENT_INCOMINGLOGINOPTION, instance, target);
      } else {
         if (!activeEnabled) {
            instance = LocalLocaleFlow.createMessage(instance, true);
         }

         indirectSessionHandler.fetchSenderAdapter().handleMessage(instance, target);
         handleIncomingLoginOption(IncomingLoginOption.CURRENT_INCOMINGLOGINOPTION, instance, target, activeEnabled);
      }
   }

   public static void close() {
      if (printWriter != null) {
         printWriter.flush();
         printWriter.close();
         printWriter = null;
      }
   }

   public static void dispatchMessage(String instance, Object... target) {
      updateMessage(instance, null, target);
   }

   public static void sendMessage(String instance, @Nullable Throwable target, Object... input) {
      if (input.length > 0) {
         instance = String.format(instance, input);
      }

      if (pendingEnabled) {
         performIncomingLoginOption(IncomingLoginOption.INCOMING_LOGIN_OPTION, instance, target);
      } else {
         if (!activeEnabled) {
            instance = LocalLocaleFlow.createMessage(instance, true);
         }

         indirectSessionHandler.fetchSenderAdapter().updateMessage(instance);
         handleIncomingLoginOption(IncomingLoginOption.INCOMING_LOGIN_OPTION, instance, target, activeEnabled);
      }
   }

   public static void updateMessage(String instance, @Nullable Throwable target, Object... input) {
      if (input.length > 0) {
         instance = String.format(instance, input);
      }

      if (enabled) {
         sendMessage(IncomingLoginOption.ACTIVE_INCOMINGLOGINOPTION.activeName + IncomingLoginOption.ACTIVE_INCOMINGLOGINOPTION.name + instance, target);
      } else {
         handleIncomingLoginOption(IncomingLoginOption.ACTIVE_INCOMINGLOGINOPTION, instance, target, true);
      }
   }

   public static boolean findState() {
      return enabled;
   }

   public static void performIncomingLoginOption(IncomingLoginOption instance, String target, @Nullable Throwable input, Object... output) {
      if (output.length > 0) {
         target = String.format(target, output);
      }

      if (input != null) {
         input.printStackTrace();
      }

      if (indirectSessionHandler == null) {
         String context = LocalLocaleFlow.createMessage(target, true);
         System.out.println(instance.resolveMessage(context, false));
         handleIncomingLoginOption(instance, context, input, false);
      } else {
         indirectSessionHandler.retrieveParentAccountHandler().findAuthenticatedServerAdapter().k(instance.resolveMessage(target, true));
         handleIncomingLoginOption(instance, target, input, true);
      }
   }

   public static void handleFile(File instance) {
      if (printWriter != null) {
         throw new IllegalStateException("Logger already configured!");
      }

      if (!MessageProcessor.isState(instance)) {
         throw new IOException("Unable to create " + instance.getPath() + " file!");
      }

      printWriter = SafeLoginCheckpoint.processPrintWriter(instance, StandardCharsets.UTF_8, false);
   }

   public static Set<ConnectionContract> retrieveSet() {
      return players;
   }

   public static void updateMessage(String instance, Object... target) {
      handleMessage(instance, null, target);
   }

   public static void performMessage(String instance, Object... target) {
      processMessage(instance, null, target);
   }
}
