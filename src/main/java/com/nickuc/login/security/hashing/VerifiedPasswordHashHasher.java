package com.nickuc.login.security.hashing;

import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.auth.login.ParentLoginHandler;
import com.nickuc.login.auth.login.RemoteLoginBarrier;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import org.json.JSONObject;
import com.nickuc.login.platform.listener.InternalListenerContract;
import com.nickuc.login.platform.session.SessionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;


public abstract class VerifiedPasswordHashHasher {
   private static boolean enabled;
   public static ParentLoginHandler[][] values;

   public static void executeInternalListenerContract(InternalListenerContract instance, SecureLoginGate target, Object input) {
      updateInternalListenerContract(instance, target, input, true);
   }

   public static void performValues(InternalListenerContract[] instance, SecureLoginGate target, PasswordHashLoader input) {
      for (InternalListenerContract value : instance) {
         executeInternalListenerContract(value, target, input, true);
      }
   }

   public static void executeInternalListenerContract(InternalListenerContract instance, SecureLoginGate target, PasswordHashLoader input, boolean output) {
      String[] context = instance.retrieveBusyLoginProcessor().fetchNames();
      if (context == null) {
         throw new IllegalArgumentException("Keys cannot be null!");
      }

      if (context.length == 0) {
         throw new IllegalArgumentException("Keys cannot be empty!");
      }

      Object data = instance.getObject();
      if (data == null) {
         throw new IllegalArgumentException("Default value cannot be null! " + instance);
      }

      Object value = handleObject(context, data, input, 0);
      updateInternalListenerContract(instance, target, value, output);
   }

   @Nullable
   public static Object computeObject(InternalListenerContract instance, SecureLoginGate target) {
      int input = target.count;
      if (input == -1) {
         return null;
      } else {
         ParentLoginHandler[] output = values[input];
         int context = instance.fetchCount();
         if (context < output.length) {
            ParentLoginHandler data = output[context];
            return data != null ? ParentLoginHandler.computeObject(data) : null;
         } else {
            throw new ArrayIndexOutOfBoundsException(String.format("Setting %s out of bounds! %s >= %s", instance, context, output.length));
         }
      }
   }

   public static boolean as() {
      return enabled;
   }

   public static void updateState(boolean instance) {
      PasswordHashContainer.saveState(instance);
      enabled = instance;
   }

   private static synchronized void performSecureLoginGate(SecureLoginGate instance) {
      if (values != null) {
         ParentLoginHandler[][] target = values;
         ParentLoginHandler[][] input = new ParentLoginHandler[target.length + 1][];
         System.arraycopy(target, 0, input, 0, target.length);
         int output = input.length - 1;
         instance.count = output;
         input[output] = new ParentLoginHandler[instance.activeCount];
         values = input;
      } else {
         instance.count = 0;
         values = new ParentLoginHandler[][]{new ParentLoginHandler[instance.activeCount]};
      }
   }

   public static void processInternalListenerContract(InternalListenerContract instance, Object target) {
      updateInternalListenerContract(instance, instance.loadSecureLoginGate(), target, true);
   }

   public static void updateInternalListenerContract(InternalListenerContract instance, SecureLoginGate target, Object input, boolean output) {
      if (target.count == -1) {
         performSecureLoginGate(target);
      }

      if (input == null) {
         throw new IllegalArgumentException("Value cannot be null!");
      }

      Object context = instance.processObject(instance, input);
      if (context == null) {
         throw new IllegalArgumentException("Value after handle define cannot be null!");
      }

      if (output) {
         if (context instanceof String) {
            context = LocalLocaleFlow.loadMessage((String)context);
         } else if (context instanceof List) {
            ArrayList data = new ArrayList((List)context);
            if (!data.isEmpty()) {
               data.replaceAll(instanceValue -> {
                  if (instanceValue instanceof String) {
                     instanceValue = LocalLocaleFlow.loadMessage((String)instanceValue);
                  }

                  return instanceValue;
               });
            }

            context = RemoteLoginBarrier.createRemoteLoginBarrier(data);
         }
      }

      ParentLoginHandler[] result = values[target.count];
      int value = instance.fetchCount();
      if (value < result.length) {
         result[value] = new ParentLoginHandler(instance, target, context, null);
      } else {
         throw new ArrayIndexOutOfBoundsException(String.format("Setting %s out of bounds! %s >= %s", instance, value, result.length));
      }
   }

   private static Object handleObject(String[] instance, Object target, PasswordHashLoader input, int output) {
      if (output >= instance.length) {
         return target;
      }

      Object context = input.e(instance[output++]);
      return context != null && (target == null || target instanceof Iterable || target.getClass().isAssignableFrom(context.getClass()))
         ? context
         : handleObject(instance, target, input, output);
   }

   public static JSONObject resolveJSONObject(boolean instance) {
      JSONObject target = new JSONObject();
      if (values == null) {
         return target;
      }

      HashMap input = new HashMap();

      for (ParentLoginHandler[] value : values) {
         for (ParentLoginHandler source : value) {
            if (source != null) {
               InternalListenerContract entry = ParentLoginHandler.computeInternalListenerContract(source);
               if (instance && entry instanceof SessionHandler) {
                  break;
               }

               String record = ParentLoginHandler.resolveSecureLoginGate(source).name;
               JSONObject item = input.computeIfAbsent(record, inputValue -> {
                  JSONObject output = new JSONObject();
                  target.put(record, output);
                  return output;
               });
               String element = entry.retrieveBusyLoginProcessor().fetchNames()[0];
               String[] content = element.split("\\.");
               JSONObject payload = item;

               for (int holder = 0; holder < content.length - 1; holder++) {
                  JSONObject reference = payload;
                  int subject = holder;
                  payload = input.computeIfAbsent(holder + content[holder], output -> {
                     JSONObject context = new JSONObject();
                     reference.put(content[subject], context);
                     return context;
                  });
               }

               payload.put(content[content.length - 1], ParentLoginHandler.computeObject(source) != null ? ParentLoginHandler.computeObject(source) : JSONObject.NULL);
            }
         }
      }

      return target;
   }
}
