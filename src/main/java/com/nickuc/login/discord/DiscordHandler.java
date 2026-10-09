package com.nickuc.login.discord;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.account.StrictPremiumOption;
import com.nickuc.login.api.enums.TwoFactorType;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.auth.login.SecureLoginHandler;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.model.SecondaryMessageKind;
import com.nickuc.login.model.SecondaryPlatformCatalog;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import com.nickuc.login.platform.player.LoudPlayerContract;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.session.LocalSessionTable;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.session.SessionTable;
import com.nickuc.login.storage.session.SharedSessionTable;
import com.nickuc.login.storage.spawn.SpawnState;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;


public class DiscordHandler extends ListenerAdapter {
   private final FastDiscordNotifier fastDiscordNotifier;
   public static Cache<String, String> cache = Caffeine.newBuilder().expireAfterWrite(15L, TimeUnit.MINUTES).build();
   private final PasswordStore passwordStore;

   public DiscordHandler(PasswordStore target, FastDiscordNotifier input) {
      this.passwordStore = target;
      this.fastDiscordNotifier = input;
   }

   public void onButtonInteraction(@Nonnull ButtonInteractionEvent target) {
      ButtonInteraction input = target.getInteraction();
      if (input.getChannelType() == ChannelType.PRIVATE) {
         String output = input.getButton().getCustomId();
         if (output != null) {
            User context = target.getUser();
            String data = context.getId();
            String value = (String)cache.getIfPresent(data);
            if (value != null) {
               cache.invalidate(data);
               VerifiedServerAdapter result = this.passwordStore.b().resolveVerifiedServerAdapter(value);
               if (result == null) {
                  String content = LocalLocaleFlow.handleMessage(CachedSettingsGateway.computeMessage(LoudProxyState.OUTGOING_LOUDPROXYSTATE));
                  target.reply(content).queue();
               } else {
                  SpawnLookup request = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(result).loadSpawnLookup();
                  String response = StrictMessageKind.STRICT_MESSAGE_KIND.buildMessage(request, null);
                  SecondaryPlatformCatalog source = StrictMessageKind.STRICT_MESSAGE_KIND.createSecondaryPlatformCatalog(request);
                  StrictMessageKind.STRICT_MESSAGE_KIND.updateSpawnLookup(request, null, null);
                  if (source != null) {
                     if (target.getMessage().getId().equals(response)) {
                        switch (output) {
                           case "deny":
                              target.reply("OK").setEphemeral(true).queue();
                              String payload = result.resolveMessage();
                              int element = SpawnState.ACTIVE_SAFE_SPAWNSTATE.r();
                              request.resolveCachedPasswordHashHasher()
                                 .updateMessage("block-" + payload, System.currentTimeMillis() + element * 60000L, element, TimeUnit.MINUTES);
                              result.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_INTERNAL_LOUDPROXYSTATE, element + "m"));
                              PasswordHashContainer.processMessage(
                                 CachedSettingsGateway.loadState()
                                    ? "A conta " + value + " (" + payload + ") foi bloqueada por suspeita de invasão [banimento remoto pelo Discord]"
                                    : "The " + value + "'s account (" + payload + ") has been blocked on suspicion of invasion [remote ban by Discord]"
                              );
                              break;
                           case "allow":
                              LimboCoordinator item = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(result);
                              this.passwordStore
                                 .processLinkedSessionHandler(true)
                                 .buildStrictCommandHandler(
                                    () -> {
                                       switch (source) {
                                          case ACTIVE_SECONDARYPLATFORMCATALOG:
                                             if (StrictMessageKind.STRICT_MESSAGE_KIND.retrieveState()) {
                                                request.performTaskForValue();
                                             }

                                             String requestValue = SecureLoginHandler.loadMessage(SecondaryMessageKind.CURRENT_SECONDARYMESSAGEKIND, 6);
                                             if (!this.passwordStore.findIndirectPasswordResolver().validateState(request, requestValue)) {
                                                CachedSettingsGateway.executeOutgoingSenderAdapter(result, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                                                target.reply(
                                                      LocalLocaleFlow.handleMessage(
                                                         CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE, result)
                                                      )
                                                   )
                                                   .queue();
                                                return;
                                             }

                                             PasswordHashContainer.dispatchMessage(
                                                "The password of " + request.retrieveMessage() + " player was changed via Discord 2FA"
                                             );
                                             item.updateLenientMessageKind(LenientMessageKind.ACTIVE_MAIN_LENIENTMESSAGEKIND, requestValue);
                                             this.passwordStore.findSettingsLinker().saveSpawnLookup(request, result, true, false);
                                             target.reply(
                                                   this.fastDiscordNotifier
                                                      .resolveMessageCreateBuilder(
                                                         CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_TOP_LOUDPROXYSTATE, result, requestValue),
                                                         CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_FAST_LOUDPROXYSTATE, result, requestValue),
                                                         CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_SAFE_LOUDPROXYSTATE, result, requestValue),
                                                         CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_SECURE_LOUDPROXYSTATE, result, requestValue)
                                                      )
                                                      .build()
                                                )
                                                .queue();
                                             break;
                                          case PENDING_SECONDARYPLATFORMCATALOG:
                                             LocalSessionTable valueValue = (LocalSessionTable)item.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
                                             if (valueValue != null) {
                                                LoudPlayerContract resultValue = valueValue.getLoudPlayerContract();
                                                if (resultValue instanceof SharedSessionTable
                                                   && ((SharedSessionTable)resultValue).getStrictMessageKind() == StrictMessageKind.STRICT_MESSAGE_KIND) {
                                                   resultValue.handlePasswordStore(this.passwordStore, result, item);
                                                }
                                             }

                                             target.reply(
                                                   LocalLocaleFlow.handleMessage(
                                                      CachedSettingsGateway.computeMessage(LoudProxyState.LOCAL_LOUDPROXYSTATE, result)
                                                   )
                                                )
                                                .queue();
                                             break;
                                          default:
                                             throw new IllegalArgumentException("Unsupported intent " + source + " for this context!");
                                       }

                                       this.passwordStore
                                          .verifyState(EventEnum.TWO_FACTOR_AUTH, TwoFactorType.DISCORD, result, request.fetchQuickDiscordHandler().getMessage());
                                    }
                                 );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void onSlashCommandInteraction(SlashCommandInteractionEvent target) {
      SlashCommandInteraction input = target.getInteraction();
      if (input.getCommandType() == Type.SLASH) {
         if (input.getFullCommandName().split(" ")[0].equals("link")) {
            User output = target.getUser();
            String context = output.getId();
            OptionMapping data = input.getOption("code");
            String value = data != null ? data.getAsString() : null;
            String result;
            if (value == null || (result = (String)cache.getIfPresent(value)) == null) {
               String setting = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_ACTIVE_LOUDPROXYSTATE);
               String attribute = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_PENDING_LOUDPROXYSTATE);
               String source = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_PRIMARY_LOUDPROXYSTATE);
               String argument = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_MAIN_LOUDPROXYSTATE);
               target.reply(this.fastDiscordNotifier.resolveMessageCreateBuilder(setting, attribute, source, argument).build()).queue();
               return;
            }

            VerifiedServerAdapter request = this.passwordStore.b().resolveVerifiedServerAdapter(result);
            if (request == null) {
               String property = LocalLocaleFlow.handleMessage(CachedSettingsGateway.computeMessage(LoudProxyState.OUTGOING_LOUDPROXYSTATE));
               target.reply(property).queue();
               return;
            }

            long response = StrictPremiumOption.CACHED_STRICTPREMIUMOPTION.r();
            if (output.getTimeCreated().isAfter(OffsetDateTime.now().minusSeconds(response))) {
               String parameter = CachedSettingsGateway.computeMessage(LoudProxyState.PENDING_SECURE_LOUDPROXYSTATE);
               String notice = CachedSettingsGateway.computeMessage(LoudProxyState.PENDING_OPEN_LOUDPROXYSTATE);
               String event = CachedSettingsGateway.computeMessage(LoudProxyState.PENDING_READY_LOUDPROXYSTATE);
               String packet = CachedSettingsGateway.computeMessage(LoudProxyState.PENDING_LIVE_LOUDPROXYSTATE);
               target.reply(this.fastDiscordNotifier.resolveMessageCreateBuilder(parameter, notice, event, packet).build()).queue();
               return;
            }

            int entry = StrictPremiumOption.REMOTE_STRICTPREMIUMOPTION.r();
            if (entry > 0 && StrictMessageKind.STRICT_MESSAGE_KIND.processCount(this.passwordStore, context) >= entry) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(
                  request, LoudProxyState.PENDING_LOCAL_LOUDPROXYSTATE, StrictMessageKind.STRICT_MESSAGE_KIND.findMessage()
               );
               String message = LocalLocaleFlow.handleMessage(
                  CachedSettingsGateway.computeMessage(LoudProxyState.PENDING_LOCAL_LOUDPROXYSTATE, request, StrictMessageKind.STRICT_MESSAGE_KIND.findMessage())
               );
               target.reply(message).queue();
               return;
            }

            cache.invalidate(value);
            LimboCoordinator record = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(request);
            SpawnLookup item = record.loadSpawnLookup();
            QuickDiscordHandler element = item.fetchQuickDiscordHandler();
            element.dispatchMessage(context);
            element.executeState(!item.fetchStateAndState() && !item.getState());
            this.passwordStore
               .findIndirectPasswordResolver()
               .isState(item, OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE);
            this.passwordStore
               .verifyState(EventEnum.TWO_FACTOR_ADD, TwoFactorType.convert(StrictMessageKind.STRICT_MESSAGE_KIND), request, request.getUniqueId(), result, context);
            CachedSettingsGateway.executeOutgoingSenderAdapter(request, LoudProxyState.PENDING_LINKED_LOUDPROXYSTATE, "@" + output.getName());
            String content = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_LOCAL_LOUDPROXYSTATE, request);
            String payload = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_REMOTE_LOUDPROXYSTATE, request);
            String holder = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_CACHED_LOUDPROXYSTATE, request);
            String reference = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_STORED_LOUDPROXYSTATE, request);
            target.reply(this.fastDiscordNotifier.resolveMessageCreateBuilder(content, payload, holder, reference).build()).queue();
            LocalSessionTable subject = (LocalSessionTable)record.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
            if (subject != null) {
               LoudPlayerContract option = subject.getLoudPlayerContract();
               if (option instanceof SessionTable && ((SessionTable)option).getStrictMessageKind() == StrictMessageKind.STRICT_MESSAGE_KIND) {
                  option.handlePasswordStore(this.passwordStore, request, record);
               }
            }
         }
      }
   }

   public void onReady(ReadyEvent target) {
      JDA input = target.getJDA();
      PasswordHashContainer.dispatchMessage("[Discord] Connected guilds: " + input.getSelfUser().getAsTag() + " (" + target.getGuildTotalCount() + ")");
      input.getGuilds()
         .stream()
         .filter(targetValue -> !input.isUnavailable(targetValue.getIdLong()))
         .forEach(instance -> PasswordHashContainer.dispatchMessage(" §7- " + instance.getName() + " §a(" + instance.getId() + ")"));
   }
}
