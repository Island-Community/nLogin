package com.nickuc.login.security.hashing;

import com.nickuc.login.config.PasswordHashContainer;
import org.json.JSONArray;
import org.json.JSONObject;
import com.nickuc.login.premium.SpawnLookup;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;


public class CachedPasswordHashHasher {
   public final HashMap<String, Object> sessions;
   public boolean enabled;
   public String name;

   public void updateMessage(String target, Object input, long output, TimeUnit data) {
      if (input == null) {
         throw new IllegalArgumentException("Value cannot be null!");
      }

      synchronized (this.sessions) {
         JSONObject result = (JSONObject)this.sessions.computeIfAbsent("temporary", instance -> new JSONObject());
         JSONObject request = new JSONObject();
         request.put("value", input);
         request.put("expires-on", System.currentTimeMillis() + data.toMillis(output));
         result.put(target, request);
         this.updateMessage("temporary", result);
      }
   }

   @Nullable
   public <T> T handleObject(String target) {
      synchronized (this.sessions) {
         return (T)this.sessions.get(target.toLowerCase(Locale.ENGLISH));
      }
   }

   @Nullable
   public String fetchMessage() {
      if (this.spawnLookup.cachedPasswordHashHasher.enabled) {
         try {
            JSONObject target = new JSONObject();
            synchronized (this.sessions) {
               if (this.sessions.isEmpty()) {
                  return null;
               }

               JSONArray output = new JSONArray();
               int context = 0;

               for (Entry value : this.sessions.entrySet()) {
                  JSONObject result = new JSONObject();
                  result.put("key", value.getKey());
                  result.put("value", value.getValue());
                  output.put(context, result);
                  context++;
               }

               target.put("version", 0);
               target.put("settings", output);
            }

            return this.name = target.toString();
         } catch (Exception source) {
            PasswordHashContainer.dispatchMessage("Failed to encrypt settings with AES.");
         }
      }

      return this.name;
   }

   public void dispatchTask() {
      byte input = 0;
      JSONObject target;
      synchronized (this.sessions) {
         target = this.handleObject("temporary");
         if (target == null) {
            return;
         }

         Iterator context = target.keys();

         while (context.hasNext()) {
            String data = (String)context.next();
            if (this.buildObject(data) == null) {
               context.remove();
               input = 1;
            }
         }
      }

      if (target.isEmpty()) {
         this.performMessage("temporary");
      } else if (input != 0) {
         this.updateMessage("temporary", target);
      }
   }

   public void updateMessage(String target) {
      synchronized (this.sessions) {
         JSONObject output = (JSONObject)this.sessions.computeIfAbsent("temporary", instance -> new JSONObject());
         if (output.remove(target) != null) {
            this.updateMessage("temporary", output);
         }
      }
   }

   public <T> T resolveObject(String target, T input) {
      synchronized (this.sessions) {
         return (T)this.sessions.getOrDefault(target.toLowerCase(Locale.ENGLISH), input);
      }
   }

   @Nullable
   public <T> T buildObject(String target) {
      target = target.toLowerCase(Locale.ENGLISH);
      JSONObject output;
      synchronized (this.sessions) {
         JSONObject input = this.handleObject("temporary");
         if (input == null) {
            return null;
         }

         if (!input.has(target)) {
            return null;
         }

         output = input.getJSONObject(target);
      }

      if (output.has("value") && output.has("expires-on")) {
         long response = output.getLong("expires-on");
         if (System.currentTimeMillis() >= response) {
            this.performMessage(target);
            return null;
         } else {
            Object value = output.get("value");
            if (value == null) {
               this.performMessage(target);
               return null;
            } else {
               return (T)value;
            }
         }
      } else {
         this.performMessage(target);
         return null;
      }
   }

   public void performMessage(String target) {
      synchronized (this.sessions) {
         if (this.sessions.remove(target.toLowerCase(Locale.ENGLISH)) != null) {
            this.enabled = true;
         }
      }
   }

   public CachedPasswordHashHasher(SpawnLookup target) {
      this.spawnLookup = target;
      this.sessions = new HashMap<>();
   }

   public HashMap<String, Object> resolveTable() {
      return (HashMap<String, Object>)this.sessions.clone();
   }

   public void updateMessage(String target, Object input) {
      synchronized (this.sessions) {
         this.sessions.put(target, input);
      }

      this.enabled = true;
   }

   public boolean hasState(String target) {
      synchronized (this.sessions) {
         return this.sessions.containsKey(target.toLowerCase(Locale.ENGLISH));
      }
   }

   public boolean getState() {
      return this.enabled;
   }
}
