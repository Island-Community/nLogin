package com.nickuc.login.proxy;

import com.nickuc.login.api.nLoginAPI;
import com.nickuc.login.api.enums.DatabaseType;
import com.nickuc.login.api.enums.ImplementationType;
import com.nickuc.login.api.enums.SpawnType;
import com.nickuc.login.api.exception.nLoginRequestUnavailableException;
import com.nickuc.login.api.exception.nLoginRequestUnsupportedException;
import com.nickuc.login.api.nLoginAPI.nLoginInternal;
import com.nickuc.login.api.types.AccountData;
import com.nickuc.login.api.types.Identity;
import com.nickuc.login.api.types.Location;
import com.nickuc.login.auth.login.BusyLoginHandler;
import com.nickuc.login.auth.login.RemoteLoginGate;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.config.PasswordHashContainer;
import org.json.JSONObject;
import com.nickuc.login.listener.bukkit.ProxyGuard;
import com.nickuc.login.listener.bukkit.VerifiedLoginGuard;
import com.nickuc.login.platform.packet.DirectPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.spawn.IndirectLoginKind;
import com.nickuc.login.spawn.LoginKind;
import com.nickuc.login.spawn.LoginResolver;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class StrictSpawnGateway implements nLoginAPI {
   private final BukkitPlatform BukkitPlatform;
   private final Set<String> players;
   private final ProxyGuard proxyGuard = new ProxyGuard(this);

   public boolean performUnregister(@Nonnull Identity target) {
      return this.<Boolean>buildObject(LoginKind.CACHED_LOGINKIND, target);
   }

   public boolean isAuthenticated(String target) {
      if (target == null) {
         throw new IllegalArgumentException("Player name cannot be null!");
      }

      if (target.isEmpty()) {
         throw new IllegalArgumentException("Player name cannot be empty!");
      }

      VerifiedServerAdapter input = this.BukkitPlatform.b().resolveVerifiedServerAdapter(target);
      return input != null && this.BukkitPlatform.fetchPasswordStore().loadLimboRegistry().canState(input);
   }

   @Nonnull
   public String getVersion() {
      return this.BukkitPlatform.getMessage();
   }

   public boolean isAuthenticated(Identity target) {
      if (target == null) {
         throw new IllegalArgumentException("Identity cannot be null!");
      } else if (!(target instanceof BusyLoginHandler)) {
         throw new IllegalArgumentException(
            "Identity is not an instance of " + BusyLoginHandler.class.getCanonicalName() + " class! " + target.getClass().getCanonicalName()
         );
      } else {
         return this.<Boolean>buildObject(LoginKind.PRIMARY_LOGINKIND, target);
      }
   }

   public int getRemainingSeconds(@Nonnull Identity target) {
      return this.<Integer>buildObject(LoginKind.MAIN_LOGINKIND, target);
   }

   @Nonnull
   public Iterator<AccountData> getAccounts() {
      throw new nLoginRequestUnsupportedException("This method is not supported in current implementation! (" + this.getImplementationType() + ")");
   }

   @Nonnull
   public nLoginInternal internal() {
      return this.proxyGuard;
   }

   public int getApiVersion() {
      return 11;
   }

   public boolean setLanguage(@Nonnull Identity target, @Nullable String input) {
      return this.<Boolean>buildObject(LoginKind.SHARED_LOGINKIND, target, input);
   }

   public boolean setEmail(@Nonnull Identity target, @Nullable String input) {
      return this.<Boolean>buildObject(LoginKind.VERIFIED_LOGINKIND, target, input);
   }

   public StrictSpawnGateway(BukkitPlatform target) {
      this.players = new HashSet<>();
      this.BukkitPlatform = target;
   }

   public DatabaseType getDatabaseType() {
      return this.buildObject(LoginKind.LOGIN_KIND);
   }

   public boolean comparePassword(AccountData target, String input) {
      return this.<Boolean>buildObject(LoginKind.LOCAL_LOGINKIND, target, input);
   }

   public boolean performRegister(@Nonnull Identity target, @Nonnull String input, @Nullable String output) {
      return this.<Boolean>buildObject(LoginKind.REMOTE_LOGINKIND, target, input, output);
   }

   private <T> T buildObject(LoginKind target, Object... input) {
      DirectPacketAdapter[] output = target.fetchValues();
      if (output.length != input.length) {
         throw new IllegalArgumentException("Proxy api request arguments size does not match with values size! " + output.length + " != " + input.length);
      }

      for (int context = 0; context < output.length; context++) {
         Object data = input[context];
         if (data != null) {
            DirectPacketAdapter value = output[context];
            Class result = value instanceof RemoteLoginGate ? ((RemoteLoginGate)value).getClass() : value.loadClass();
            Class request = data.getClass();
            if (!result.isAssignableFrom(request)) {
               throw new IllegalArgumentException("Value of argument \"" + result + "\" " + context + " is not assignable from \"" + request + "\"!");
            }
         }
      }

      if (this.BukkitPlatform.getServer().isPrimaryThread()) {
         String item = Thread.currentThread().getStackTrace()[3].toString();
         if (this.players.add(item)) {
            PasswordHashContainer.performMessage(
               "The plugin \""
                  + item
                  + "\" invoked the \""
                  + target
                  + "\" API operation on the server's main thread. This will reduce your server's performance."
            );
         }
      }

      VerifiedServerAdapter element = null;
      Iterator content = this.BukkitPlatform.b().fetchCollection().iterator();
      if (content.hasNext()) {
         VerifiedServerAdapter holder = (VerifiedServerAdapter)content.next();
         element = holder;
      }

      if (element == null) {
         throw new nLoginRequestUnavailableException("The request cannot be performed at this moment. There is no player connected to the server.");
      }

      CompletableFuture payload = this.BukkitPlatform.resolveLimboSupervisor().computeCompletableFuture(element, target, input);

      try {
         JSONObject reference = (JSONObject)payload.get(850L, TimeUnit.MILLISECONDS);
         Object subject = reference != null ? target.findDirectPacketAdapter().loadObject(reference) : null;
         return (T)(target.findState() ? Optional.<Object>ofNullable(subject) : subject);
      } catch (InterruptedException response) {
         throw new nLoginRequestUnavailableException("The request cannot be performed at this moment: InterruptedException", response);
      } catch (ExecutionException source) {
         throw new nLoginRequestUnavailableException("The request cannot be performed at this moment: ExecutionException", source);
      } catch (TimeoutException entry) {
         throw new nLoginRequestUnavailableException(
            "The request cannot be performed at this moment. The API request has reached the response timeout. (850ms)"
         );
      } catch (ClassCastException record) {
         throw new RuntimeException(record);
      }
   }

   public boolean forceLogin(@Nonnull Identity target, boolean input) {
      return this.<Boolean>buildObject(LoginKind.PRIVATE_LOGINKIND, target, input);
   }

   @Nonnull
   public ImplementationType getImplementationType() {
      return ImplementationType.PROXY;
   }

   public Optional<Location> getSpawnLocation(SpawnType target) {
      if (target == null) {
         throw new IllegalArgumentException("Spawn type cannot be null!");
      }

      IndirectLoginKind input = IndirectLoginKind.computeIndirectLoginKind(target);
      String output = this.BukkitPlatform.a().loadPrimaryPasswordHashVerifier().loadMessage(input.fetchMessage());
      if (output == null) {
         return Optional.empty();
      }

      org.bukkit.Location context = VerifiedLoginGuard.resolveLocation(output);
      LoginResolver data = new LoginResolver(context.getWorld().getName(), context.getX(), context.getY(), context.getZ(), context.getYaw(), context.getPitch());
      return Optional.of(data);
   }

   public boolean isAvailable() {
      return !this.BukkitPlatform.fetchPasswordStore().findState() && !BungeeWriter.retrieveCollection().isEmpty();
   }

   public long getAccountCount() {
      return this.<Long>buildObject(LoginKind.CURRENT_LOGINKIND);
   }

   public boolean setDiscord(@Nonnull Identity target, long input) {
      return this.<Boolean>buildObject(LoginKind.AUTHENTICATED_LOGINKIND, target, input);
   }

   public boolean changePassword(@Nonnull Identity target, @Nonnull String input) {
      return this.<Boolean>buildObject(LoginKind.STORED_LOGINKIND, target, input);
   }

   public Optional<AccountData> getAccount(@Nonnull Identity target) {
      return this.buildObject(LoginKind.ACTIVE_LOGINKIND, target);
   }

   public void requestLogin(@Nonnull Identity target, @Nonnull Object input) {
      throw new nLoginRequestUnsupportedException("This method is not supported in current implementation! (" + this.getImplementationType() + ")");
   }

   @Nonnull
   public List<AccountData> getAccountsByIp(@Nonnull String target) {
      return this.buildObject(LoginKind.PENDING_LOGINKIND, target);
   }
}
