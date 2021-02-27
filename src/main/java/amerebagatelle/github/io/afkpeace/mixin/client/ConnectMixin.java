package amerebagatelle.github.io.afkpeace.mixin.client;

import amerebagatelle.github.io.afkpeace.client.AFKPeaceClient;
import amerebagatelle.github.io.afkpeace.client.ConnectionManager;
import amerebagatelle.github.io.afkpeace.common.SettingsManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ConnectMixin {
    @Shadow
    private MinecraftClient client;
    private float lastHealth;

    /**
     * Gathers server data so that we know what to reconnect to.
     */
    @Environment(EnvType.CLIENT)
    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void onConnectedToServerEvent(GameJoinS2CPacket packet, CallbackInfo cbi) {
        AFKPeaceClient.currentServerEntry = client.getCurrentServerEntry();
    }

    /**
     * Checks if we should try to automatically reconnect, and if not opens a custom screen with a reconnect button
     */
    @Environment(EnvType.CLIENT)
    @Inject(method = "onDisconnected", at = @At("HEAD"), cancellable = true)
    public void tryReconnect(Text reason, CallbackInfo cbi) {
        ConnectionManager connectionManager = ConnectionManager.INSTANCE;
        ServerInfo target = AFKPeaceClient.currentServerEntry;
        String reasonString = reason.toString();
        if (SettingsManager.applyOverride(SettingsManager.settings.reconnectEnabled, SettingsManager.settingsOverride.reconnectEnabled)) {
            if (!reasonString.contains("multiplayer.disconnect.kicked")) {
                if (!connectionManager.isDisconnecting) {
                    if (target != null) {
                        connectionManager.startReconnect(target);
                        cbi.cancel();
                    }
                } else {
                    connectionManager.isDisconnecting = false;
                }
            }
        }
    }

    /**
     * Gets when the player's health changes, and logs the player out if it has taken damage
     */
    @Environment(EnvType.CLIENT)
    @Inject(method = "onHealthUpdate", at = @At("TAIL"))
    public void onPlayerHealthUpdate(HealthUpdateS2CPacket packet, CallbackInfo cbi) {
        if (SettingsManager.applyOverride(SettingsManager.settings.damageLogoutEnabled, SettingsManager.settingsOverride.damageLogoutEnabled)) {
            try {
                if (packet.getHealth() < lastHealth && packet.getHealth() < SettingsManager.settings.damageLogoutTolerance) {
                    ConnectionManager.INSTANCE.disconnectFromServer(new TranslatableText("afkpeace.reason.damagelogout"));
                }
            } catch (NullPointerException ignored) {
            }
        }
        lastHealth = packet.getHealth();
    }
}