package com.nickuc.login.storage.settings;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.config.SettingsWriter;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.notification.NoticeSender;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.MessageKind;
import com.nickuc.login.updater.NoticeCatalog;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

public class CachedSettingsGateway {
   private static float factor = Float.intBitsToFloat(1097859072);
   private static final EnumMap<LenientPremiumOption, SettingsWriter> sessions = new EnumMap<>(LenientPremiumOption.class);
   public static LenientPremiumOption lenientPremiumOption = LenientPremiumOption.ACTIVE_LENIENTPREMIUMOPTION;

   public static void executeOutgoingSenderAdapter(OutgoingSenderAdapter instance, LoudProxyState target, Object... input) {
      handleOutgoingSenderAdapter(instance, target, null, input);
   }

   public static NoticeSender createNoticeSender(MessageKind instance, VerifiedServerAdapter target) {
      LimboCoordinator input = PasswordStore.resolvePasswordStore().loadLimboRegistry().loadLimboCoordinator(target);
      LenientPremiumOption output = input != null ? input.fetchLenientPremiumOption() : loadLenientPremiumOption();
      return handleSettingsWriter(output).handleNoticeSender(instance);
   }

   public static void saveOutgoingSenderAdapter(OutgoingSenderAdapter instance, NoticeCatalog target, Object... input) {
      saveOutgoingSenderAdapter(instance, target, null, input);
   }

   public static void processOutgoingSenderAdapter(OutgoingSenderAdapter instance, QuickProxyState target, float input, float output) {
      if (instance instanceof VerifiedServerAdapter && SpawnState.PENDING_STORED_SPAWNSTATE.ar()) {
         VerifiedServerAdapter context = (VerifiedServerAdapter)instance;
         if (PasswordStore.resolvePasswordStore().loadState()) {
            SecondarySenderAdapter data = PasswordStore.resolvePasswordStore().findObject();
            data.getPasswordHashAdapter().updateVerifiedServerAdapter(context, 4, "action", 0, "sound", target.name(), "volume", input, "pitch", output);
         } else {
            Player value = context.findObject();
            target.handleConsumer(outputValue -> value.playSound(value.getLocation(), outputValue, input, output));
         }
      }
   }

   public static void saveOutgoingSenderAdapter(OutgoingSenderAdapter instance, NoticeCatalog target, Consumer<String> input, Object... output) {
      if (instance instanceof VerifiedServerAdapter) {
         VerifiedServerAdapter context = (VerifiedServerAdapter)instance;
         String data = context.getName();
         LimboCoordinator value = PasswordStore.resolvePasswordStore().loadLimboRegistry().loadLimboCoordinator(context);
         LenientPremiumOption result = value.fetchLenientPremiumOption();
         SecondaryAccountHandler request = value.getSecondaryAccountHandler();
         if (input == null) {
            input = request::executeMessage;
         }

         if (target.enabled) {
            for (String entry : handleSettingsWriter(result).processCollection(target, output)) {
               input.accept(entry.replace("@player", data).replace("@address", context.resolveMessage()));
            }
         } else {
            String payload = handleSettingsWriter(result).processMessage(target, output);
            if (!payload.isEmpty()) {
               input.accept(payload.replace("@player", data).replace("@address", context.resolveMessage()));
            }
         }
      } else {
         if (input == null) {
            input = instance::dispatchMessage;
         }

         if (target.enabled) {
            for (String content : handleSettingsWriter(lenientPremiumOption).processCollectionForCollection(target, output)) {
               input.accept(content);
            }
         } else {
            String item = handleSettingsWriter(lenientPremiumOption).processMessage(target, output);
            if (!item.isEmpty()) {
               input.accept(item);
            }
         }
      }
   }

   public static String computeMessage(LoudProxyState instance, VerifiedServerAdapter target, Object... input) {
      return loadMessage(instance, target.getName(), target, input);
   }

   public static boolean loadState() {
      return lenientPremiumOption == LenientPremiumOption.LENIENT_PREMIUM_OPTION || lenientPremiumOption == LenientPremiumOption.INCOMING_LENIENTPREMIUMOPTION;
   }

   public static SettingsWriter handleSettingsWriter(LenientPremiumOption instance) {
      SettingsWriter target = sessions.get(instance);
      if (target == null) {
         throw new IllegalStateException(instance + " lang not loaded!");
      } else {
         return target;
      }
   }

   public static List<String> computeCollection(LoudProxyState instance, VerifiedServerAdapter target, Object... input) {
      return resolveCollection(instance, target.getName(), target, input);
   }

   public static void dispatchPasswordStore(PasswordStore instance, LenientPremiumOption target) {
      SettingsWriter input = new SettingsWriter(instance, target, target == LenientPremiumOption.ROOT_LENIENTPREMIUMOPTION ? loadMessage() : target.pendingName);
      sessions.put(target, input);
   }

   public static String loadMessage() {
      return SpawnState.SPAWN_STATE.a(new Object[0]);
   }

   public static String loadMessage(LoudProxyState instance, String target, @Nullable VerifiedServerAdapter input, Object... output) {
      LimboCoordinator context = input != null ? PasswordStore.resolvePasswordStore().loadLimboRegistry().buildLimboCoordinator(input) : null;
      LenientPremiumOption data = context != null ? context.fetchLenientPremiumOption() : loadLenientPremiumOption();
      if (instance.enabled) {
         List value = handleSettingsWriter(data).resolveCollection(instance, output);
         return String.join(" §r\n", value).replace("@player", target) + " ";
      } else {
         return handleSettingsWriter(data).computeMessage(instance, output).replace("@player", target);
      }
   }

   public static void handleOutgoingSenderAdapter(OutgoingSenderAdapter instance, String target, Object... input) {
      if (input.length > 0) {
         target = String.format(target, input);
      }

      if (instance instanceof VerifiedServerAdapter) {
         LimboCoordinator output = PasswordStore.resolvePasswordStore().loadLimboRegistry().loadLimboCoordinator((VerifiedServerAdapter)instance);
         output.getSecondaryAccountHandler().executeMessage(target, null, null, null, null);
      } else {
         instance.dispatchMessage(target);
      }
   }

   public static boolean resolveState() {
      return lenientPremiumOption == LenientPremiumOption.PRIMARY_LENIENTPREMIUMOPTION;
   }

   public static String handleMessage(LenientPremiumOption instance, LoudProxyState target, Object... input) {
      return target.enabled
         ? String.join(" §r\n", handleSettingsWriter(instance).resolveCollection(target, input)) + " "
         : handleSettingsWriter(instance).computeMessage(target, input);
   }

   public static LenientPremiumOption loadLenientPremiumOption() {
      return lenientPremiumOption;
   }

   public static void processOutgoingSenderAdapter(OutgoingSenderAdapter instance, QuickProxyState target) {
      processOutgoingSenderAdapter(instance, target, factor, 1.0F);
   }

   public static List<String> resolveCollection(LoudProxyState instance, String target, @Nullable VerifiedServerAdapter input, Object... output) {
      LimboCoordinator context = input != null ? PasswordStore.resolvePasswordStore().loadLimboRegistry().buildLimboCoordinator(input) : null;
      LenientPremiumOption data = context != null ? context.fetchLenientPremiumOption() : loadLenientPremiumOption();
      List value = handleSettingsWriter(data).loadCollection(instance, output);
      value.replaceAll(targetValue -> targetValue.replace("@player", target));
      return value;
   }

   public static List<String> handleCollection(LoudProxyState instance, Object... target) {
      return handleSettingsWriter(lenientPremiumOption).loadCollection(instance, target);
   }

   public static String computeMessage(LoudProxyState instance, Object... target) {
      return handleMessage(lenientPremiumOption, instance, target);
   }

   public static void updateVerifiedServerAdapter(VerifiedServerAdapter instance, SharedLoginOption target, Object... input) {
      LimboCoordinator output = PasswordStore.resolvePasswordStore().loadLimboRegistry().loadLimboCoordinator(instance);
      handleSettingsWriter(output.fetchLenientPremiumOption()).loadSettingsPublisher(target).sendVerifiedServerAdapter(instance, input);
   }

   public static void handleOutgoingSenderAdapter(OutgoingSenderAdapter instance, LoudProxyState target, Consumer<String> input, Object... output) {
      if (instance instanceof VerifiedServerAdapter) {
         VerifiedServerAdapter context = (VerifiedServerAdapter)instance;
         String data = context.getName();
         LimboCoordinator value = PasswordStore.resolvePasswordStore().loadLimboRegistry().loadLimboCoordinator(context);
         LenientPremiumOption result = value.fetchLenientPremiumOption();
         SecondaryAccountHandler request = value.getSecondaryAccountHandler();
         if (input == null) {
            input = request::executeMessage;
         }

         if (target.enabled) {
            for (String entry : handleSettingsWriter(result).loadCollection(target, output)) {
               input.accept(entry.replace("@player", data).replace("@address", context.resolveMessage()));
            }
         } else {
            String payload = handleSettingsWriter(result).computeMessage(target, output);
            if (!payload.isEmpty()) {
               input.accept(payload.replace("@player", data).replace("@address", context.resolveMessage()));
            }
         }
      } else {
         if (input == null) {
            input = instance::dispatchMessage;
         }

         if (target.enabled) {
            for (String content : handleSettingsWriter(lenientPremiumOption).resolveCollection(target, output)) {
               input.accept(content);
            }
         } else {
            String item = handleSettingsWriter(lenientPremiumOption).computeMessage(target, output);
            if (!item.isEmpty()) {
               input.accept(item);
            }
         }
      }
   }
}
