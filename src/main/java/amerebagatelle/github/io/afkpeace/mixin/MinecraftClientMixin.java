package amerebagatelle.github.io.afkpeace.mixin;

import java.util.Arrays;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import amerebagatelle.github.io.afkpeace.miscellaneous.PlayerShadowPauseMenuScreen;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method="openPauseMenu", at=@At("HEAD"), cancellable=true)
    public void addPlayerShadowButton(boolean bl, CallbackInfo cbi) {
        // TODO: Make this a mixin into the pause menu instead
    }
}