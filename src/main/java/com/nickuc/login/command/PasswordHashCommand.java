package com.nickuc.login.command;

import com.nickuc.login.config.PasswordHashContainer;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.server.NestedServerAdapter;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.account.ParentAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;


public abstract class PasswordHashCommand<T extends IndirectSessionHandler<?>> {
   private static double ratio = Double.longBitsToDouble(4602678819172646912L);
   public T indirectSessionHandler;
   private static final Set<String> players = ConcurrentHashMap.newKeySet();
   private double activeRatio = ratio;
   private boolean enabled;
   private boolean activeEnabled;
   private static final Cache<String, Long> cache = Caffeine.newBuilder().expireAfterWrite(5L, TimeUnit.SECONDS).build();
   private List<String> entries;
   private List<String> activeEntries;
   private double pendingRatio;
   private static double currentRatio = Double.longBitsToDouble(4596373779694328218L);
   private ParentAccountHandler parentAccountHandler;
   private String name;
   private String activeName;

   public PasswordHashCommand<T> handlePasswordHashCommand(double target) {
      this.activeRatio = target;
      return this;
   }

   public boolean findState() {
      return this.activeEnabled;
   }

   public final void saveObject(Object target, String input, boolean output, String context, String[] data) {
      String value = "E" + input.toLowerCase(Locale.ENGLISH);
      if (output) {
         Long result = (Long)cache.getIfPresent(value);
         long request = System.currentTimeMillis();
         if (result != null && request - result <= this.activeRatio) {
            return;
         }

         cache.put(value, request);
      }

      if (!this.activeEnabled || players.add(value)) {
         try {
            OutgoingSenderAdapter item = output
               ? this.parentAccountHandler.processVerifiedServerAdapter(target)
               : this.parentAccountHandler.findAuthenticatedServerAdapter();
            this.performOutgoingSenderAdapter(item, context, data);
         } finally {
            players.remove(value);
         }
      }
   }

   public PasswordHashCommand<T> getPasswordHashCommand() {
      this.enabled = true;
      return this;
   }

   public PasswordHashCommand<T> computePasswordHashCommand(String target) {
      this.name = target;
      return this;
   }

   public PasswordHashCommand<T> buildPasswordHashCommand(String target) {
      this.activeName = target;
      return this;
   }

   public List<String> computeCollection(OutgoingSenderAdapter target, String input, String[] output) {
      return null;
   }

   public abstract void saveOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output);

   public String findMessage() {
      return this.name;
   }

   public boolean resolveState() {
      return this.enabled;
   }

   public void executeMessage(String target) {
      this.activeEntries.add(target.toLowerCase(Locale.ENGLISH));
   }

   public PasswordHashCommand<T> buildPasswordHashCommand(boolean target) {
      this.activeEnabled = target;
      return this;
   }

   public void performNames(String... target) {
      for (String data : target) {
         this.entries.add(data.toLowerCase(Locale.ENGLISH));
      }
   }

   public String loadMessage() {
      return this.activeName;
   }

   public T getIndirectSessionHandler() {
      return this.indirectSessionHandler;
   }

   public ParentAccountHandler retrieveParentAccountHandler() {
      return this.parentAccountHandler;
   }

   public boolean verifyState(OutgoingSenderAdapter target, String input, String[] output, boolean context) {
      if (this.activeEntries.isEmpty()) {
         return true;
      }

      for (String value : this.activeEntries) {
         if (target.hasState(value)) {
            return true;
         }
      }

      return false;
   }

   public NestedServerAdapter handleNestedServerAdapter(IndirectSessionHandler<?> target) {
      this.indirectSessionHandler = (T)target;
      this.parentAccountHandler = this.indirectSessionHandler.retrieveParentAccountHandler();
      NestedServerAdapter input = this.parentAccountHandler.processNestedServerAdapter(this);
      input.updateTask();
      this.executeTask();
      return input;
   }

   public void executeTask() {
   }

   public double getRatio() {
      return this.activeRatio;
   }

   private List<String> resolveCollection(OutgoingSenderAdapter target, String input, String[] output) {
      if (!this.verifyState(target, input, output, true)) {
         return null;
      }

      try {
         return this.computeCollection(target, input, output);
      } catch (Exception data) {
         target.dispatchMessage("§cSorry, but an error occurred during the tab complete. More information on the console.");
         PasswordHashContainer.handleMessage("Unable to run tab complete: /" + input.toLowerCase(Locale.ENGLISH), data);
         return null;
      }
   }

   public double loadRatio() {
      return this.pendingRatio;
   }

   public final void dispatchOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      String context = "E" + target.getName().toLowerCase(Locale.ENGLISH);
      if (target instanceof VerifiedServerAdapter) {
         Long data = (Long)cache.getIfPresent(context);
         long value = System.currentTimeMillis();
         if (data != null && value - data <= this.activeRatio) {
            return;
         }

         cache.put(context, value);
      }

      if (!this.activeEnabled || players.add(context)) {
         try {
            this.performOutgoingSenderAdapter(target, input, output);
         } finally {
            players.remove(context);
         }
      }
   }

   public List<String> fetchCollection() {
      return this.entries;
   }

   @Nullable
   public final List<String> handleCollection(Object target, String input, boolean output, String context, String[] data) {
      String value = "S" + input.toLowerCase(Locale.ENGLISH);
      if (output) {
         Long result = (Long)cache.getIfPresent(value);
         long request = System.currentTimeMillis();
         if (result != null && request - result <= this.activeRatio) {
            return null;
         }

         cache.put(value, request);
      }

      if (this.activeEnabled && !players.add(value)) {
         return null;
      }

      try {
         OutgoingSenderAdapter item = output
            ? this.parentAccountHandler.processVerifiedServerAdapter(target)
            : this.parentAccountHandler.findAuthenticatedServerAdapter();
         return this.resolveCollection(item, context, data);
      } finally {
         players.remove(value);
      }
   }

   public PasswordHashCommand(String target) {
      this.pendingRatio = currentRatio;
      this.activeEnabled = true;
      this.activeName = target;
      this.entries = new ArrayList<>();
      this.activeEntries = new ArrayList<>();
   }

   public final void performOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      if (this.verifyState(target, input, output, false)) {
         Runnable context = () -> {
            if (!(target instanceof VerifiedServerAdapter) || ((VerifiedServerAdapter)target).loadState()) {
               try {
                  this.saveOutgoingSenderAdapter(target, input, output);
               } catch (Exception data) {
                  target.dispatchMessage("§cSorry, but an error occurred while executing this command. More information on the console.");
                  PasswordHashContainer.handleMessage(
                     "Unable to run command " + input.toLowerCase(Locale.ENGLISH) + "! sender = " + target.getName() + ", async = " + this.enabled, data
                  );
               }
            }
         };
         if (this.enabled) {
            this.indirectSessionHandler.processLinkedSessionHandler(true).buildStrictCommandHandler(context);
         } else {
            context.run();
         }
      }
   }

   public PasswordHashCommand<T> computePasswordHashCommand(double target) {
      this.pendingRatio = target;
      return this;
   }

   public List<String> getCollection() {
      return this.activeEntries;
   }

   public PasswordHashCommand<T> loadPasswordHashCommand(List<String> target) {
      this.entries = target;
      return this;
   }

   public PasswordHashCommand<T> buildPasswordHashCommand(List<String> target) {
      this.activeEntries = target;
      return this;
   }
}
