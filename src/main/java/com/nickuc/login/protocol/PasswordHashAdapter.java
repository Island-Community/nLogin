package com.nickuc.login.protocol;

import com.nickuc.login.api.nLoginAPI;
import com.nickuc.login.auth.login.BusyLoginGate;
import com.nickuc.login.auth.login.ReadyLoginCheckpoint;
import com.nickuc.login.auth.login.SafeLoginBarrier;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.RemotePremiumState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.command.StrictCommandHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.spawn.LoginKind;
import com.nickuc.login.storage.password.PasswordStore;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PasswordHashAdapter {
   private final StrictCommandHandler strictCommandHandler;
   private byte[] values;
   private final PasswordStore passwordStore;
   private final Set<ReadyLoginCheckpoint> players = new LinkedHashSet<>();
   private final Object object;

   private void saveVerifiedServerAdapter(VerifiedServerAdapter target, String input, String output) {
      JSONObject context = new JSONObject(output);
      if (this.values != null && context.has("secret")) {
         byte[] data = SafeLoginBarrier.loadPayload(context.getString("secret").getBytes(StandardCharsets.UTF_8));
         if (!Arrays.equals(data, this.values)) {
            PasswordHashContainer.performMessage("Unable to decode plugin message packet from \"" + input + "\": invalid secret");
         } else {
            String value = context.getString("player");
            if (!target.getName().equals(value)) {
               throw new IllegalArgumentException("Player name does not match! " + target.getName() + " != " + value);
            }

            this.passwordStore
               .loadLimboRegistry()
               .loadLimboCoordinator(target)
               .updateLenientMessageKind(LenientMessageKind.ACTIVE_REMOTE_LENIENTMESSAGEKIND, true);
            int result = context.getInt("id");
            switch (result) {
               case 0:
                  int reference = context.has("action") ? context.getInt("action") : 0;
                  switch (reference) {
                     case 0:
                        byte[] response = Pbkdf2Linker.resolvePayload();
                        String subject = Pbkdf2Linker.getMessage();
                        if (response != null && subject != null) {
                           SecondarySenderAdapter option = this.passwordStore.findObject();
                           option.getPasswordHashAdapter()
                              .handleVerifiedServerAdapter(target, 0, "stage", 1, "hash", subject, "content", SafeLoginBarrier.resolveMessage(response));
                        }
                     case 1:
                  }
               case 5:
               default:
                  break;
               case 6:
                  long request = context.getLong("requestId");
                  int source = context.getInt("resource");
                  LoginKind entry = LoginKind.handleLoginKind(source);
                  if (entry == null) {
                     PasswordHashContainer.updateMessage(
                        "Unable to handle api request " + request + ": unknown resource id! resource = " + source + ", request id = " + request
                     );
                  } else {
                     JSONArray record = context.getJSONArray("arguments");
                     Object[] item = entry.processValues(record);

                     Object element;
                     try {
                        element = entry.computeObject(nLoginAPI.getApi(), item);
                     } catch (Exception holder) {
                        String payload = holder.getMessage();
                        this.updateVerifiedServerAdapter(target, 6, "requestId", request, "exception", payload != null ? payload : "null");
                        PasswordHashContainer.handleMessage("Unable to handle api request \"" + entry + "\"! request id = " + request, holder);
                        return;
                     }

                     if (element instanceof Optional) {
                        element = ((Optional)element).orElse(null);
                     }

                     this.handleVerifiedServerAdapter(
                        target, 6, "requestId", request, "response", element != null ? entry.findDirectPacketAdapter().buildJSONObject(element) : JSONObject.NULL
                     );
                  }
            }
         }
      } else {
         PasswordHashContainer.performMessage("Unable to decode plugin message packet from \"" + input + "\": missing secret");
      }
   }

   private void saveVerifiedServerAdapter(VerifiedServerAdapter target, JSONObject input) {
      this.executeVerifiedServerAdapter(target, input.toString().getBytes(StandardCharsets.UTF_8));
   }

   private void updateVerifiedServerAdapter(VerifiedServerAdapter target, boolean input, int output, Object... context) {
      if (context.length % 2 != 0) {
         throw new IllegalArgumentException("Not in key and value format!");
      }

      JSONObject data = new JSONObject();
      if (input) {
         data.put("secret", SafeLoginBarrier.resolveMessage(this.values));
      }

      data.put("id", output);
      data.put("player", target.getName());

      for (int value = 0; value < context.length; value++) {
         Object result = context[value++];
         if (!(result instanceof String)) {
            throw new IllegalArgumentException("Key is not a string! " + result);
         }

         Object request = context[value];
         data.put((String)result, request != null ? request : JSONObject.NULL);
      }

      SecondarySenderAdapter holder = this.passwordStore.findObject();
      LimboCoordinator reference = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(target);
      String subject = holder.handleMessage(target);
      if (subject != null && holder.isState(reference, subject)) {
         if (holder.verifyState(target)) {
            if (input) {
               this.saveVerifiedServerAdapter(target, data);
            } else {
               synchronized (this.players) {
                  this.players.add(new ReadyLoginCheckpoint(target, data));
               }
            }
         }
      } else {
         Set response = reference.resolveObject(LenientMessageKind.ACTIVE_CACHED_LENIENTMESSAGEKIND, instance -> new LinkedHashSet());
         synchronized (response) {
            if (input) {
               byte[] entry = data.toString().getBytes(StandardCharsets.UTF_8);
               response.add(new BusyLoginGate(target, entry));
            } else {
               response.add(new ReadyLoginCheckpoint(target, data));
            }
         }
      }
   }

   public void dispatchVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input, String output) {
      SecondarySenderAdapter context = this.passwordStore.findObject();
      if (output == null) {
         throw new IllegalStateException("Provided server name is null when sending the ACK message!");
      }

      JSONObject data = new JSONObject();
      data.put("secret", SafeLoginBarrier.resolveMessage(this.values));
      data.put("id", -1);
      data.put("player", target.getName());
      data.put("server_name", output);
      data.put("auth_server", context.isState(input, output));

      try {
         this.saveVerifiedServerAdapter(target, data);
      } catch (Exception result) {
         if (PasswordHashContainer.findState()) {
            PasswordHashContainer.sendThrowable(result);
         }

         PasswordHashContainer.performMessage("Unable to send ACK message: " + result.getMessage());
      }
   }

   public void handleVerifiedServerAdapter(VerifiedServerAdapter target, int input, Object... output) {
      this.updateVerifiedServerAdapter(target, true, input, output);
   }

   public void executeTask() {
      this.strictCommandHandler.performTask();
   }

   public void updatePayload(byte[] target) {
      if (this.values != null) {
         throw new IllegalStateException("Secret key already defined!");
      }

      this.values = target;
   }

   public PasswordHashAdapter(PasswordStore target, Object input) {
      this.passwordStore = target;
      this.object = input;
      this.strictCommandHandler = target.processLinkedSessionHandler(true)
         .processStrictCommandHandler(
            () -> {
               if (!this.players.isEmpty()) {
                  ReadyLoginCheckpoint[] targetValue;
                  synchronized (this.players) {
                     targetValue = this.players.toArray(new ReadyLoginCheckpoint[0]);
                     this.players.clear();
                  }

                  if (targetValue.length > 0) {
                     HashSet request = new HashSet();

                     for (ReadyLoginCheckpoint value : targetValue) {
                        request.add(value.verifiedServerAdapter);
                     }

                     for (VerifiedServerAdapter source : request) {
                        ReadyLoginCheckpoint[] entry = Arrays.stream(targetValue)
                           .filter(targetValue -> targetValue.verifiedServerAdapter.equals(source))
                           .toArray(ReadyLoginCheckpoint[]::new);
                        this.processVerifiedServerAdapter(source, entry, 0);
                     }
                  }
               }
            },
            0L,
            200L,
            TimeUnit.MILLISECONDS
         );
   }

   public void performVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input) {
      if (input.isState(LenientMessageKind.ACTIVE_CACHED_LENIENTMESSAGEKIND)) {
         SecondarySenderAdapter output = this.passwordStore.findObject();
         if (!output.verifyState(target) || !output.canState(target, input)) {
            input.loadObject(LenientMessageKind.ACTIVE_CACHED_LENIENTMESSAGEKIND);
            return;
         }

         Set context = (Set)input.d(LenientMessageKind.ACTIVE_CACHED_LENIENTMESSAGEKIND);
         if (context != null) {
            synchronized (context) {
               if (!output.verifyState(target) || !output.canState(target, input)) {
                  input.loadObject(LenientMessageKind.ACTIVE_CACHED_LENIENTMESSAGEKIND);
                  return;
               }

               if (!context.isEmpty()) {
                  synchronized (this.players) {
                     context.stream().filter(instance -> instance instanceof ReadyLoginCheckpoint).forEach(targetValue -> this.players.add(targetValue));
                  }

                  context.stream().filter(instance -> instance instanceof BusyLoginGate).forEach(inputValue -> {
                     BusyLoginGate outputValue = (BusyLoginGate)inputValue;
                     this.executeVerifiedServerAdapter(target, BusyLoginGate.loadPayload(outputValue));
                  });
               }

               input.loadObject(LenientMessageKind.ACTIVE_CACHED_LENIENTMESSAGEKIND);
            }
         }
      }
   }

   private void processVerifiedServerAdapter(VerifiedServerAdapter target, ReadyLoginCheckpoint[] input, int output) {
      SecondarySenderAdapter context = this.passwordStore.findObject();
      if (context.verifyState(target)) {
         JSONObject data;
         if (input.length == 1) {
            data = ReadyLoginCheckpoint.buildJSONObject(input[0]);
         } else if (input.length == output + 1) {
            data = ReadyLoginCheckpoint.buildJSONObject(input[output]);
         } else {
            data = new JSONObject();
            JSONArray value = new JSONArray();
            int result = 0;
            int request = output;

            while (true) {
               if (request < input.length) {
                  ReadyLoginCheckpoint response = input[request];
                  if (!response.verifiedServerAdapter.equals(target)) {
                     throw new IllegalArgumentException("Player in queue message does not match with sender! " + response.verifiedServerAdapter + " " + target);
                  }

                  if (ReadyLoginCheckpoint.buildCount(response) > 2048) {
                     throw new IllegalArgumentException("Message too large! size = " + ReadyLoginCheckpoint.buildCount(response) + ", max = " + 2048);
                  }

                  int source = result + ReadyLoginCheckpoint.buildCount(response);
                  if (source <= 2048) {
                     result = source;
                     value.put(ReadyLoginCheckpoint.buildJSONObject(response));
                     request++;
                     continue;
                  }

                  PasswordHashContainer.dispatchMessage("Splitting packet content, size = " + source + ", max = " + 2048 + "...");
                  this.processVerifiedServerAdapter(target, input, request);
               }

               if (value.length() == 0) {
                  return;
               }

               data.put("packets", value);
               break;
            }
         }

         data.put("secret", SafeLoginBarrier.resolveMessage(this.values));
         this.saveVerifiedServerAdapter(target, data);
      }
   }

   private void executeVerifiedServerAdapter(VerifiedServerAdapter target, byte[] input) {
      this.passwordStore.resolveTightConnectionContract().checkState(target, RemotePremiumState.ACTIVE_REMOTEPREMIUMSTATE, this.object, input);
   }

   public void updateVerifiedServerAdapter(VerifiedServerAdapter target, int input, Object... output) {
      this.updateVerifiedServerAdapter(target, false, input, output);
   }

   public void sendVerifiedServerAdapter(VerifiedServerAdapter target, String input, byte[] output) {
      try {
         if (output.length == 0) {
            return;
         }

         String context = new String(output, StandardCharsets.UTF_8);
         if (context.isEmpty()) {
            return;
         }

         try {
            if (context.charAt(0) == '{' && context.charAt(context.length() - 1) == '}') {
               this.saveVerifiedServerAdapter(target, input, context);
               return;
            }

            PasswordHashContainer.updateMessage("Malformed JSON received from backend server: \"%s\"", context.replace("\n", "\\n"));
         } catch (JSONException value) {
            PasswordHashContainer.handleMessage("Malformed JSON received from backend server: \"%s\"", value, context.replace("\n", "\\n"));
         }
      } catch (Exception result) {
         if (output[0] != 123) {
            PasswordHashContainer.updateMessage("Unable to decode plugin message packet. Did you forget to update nLogin on the backend server?");
         } else {
            PasswordHashContainer.handleMessage("Unable to read nLogin plugin message.", result);
         }
      }
   }
}
