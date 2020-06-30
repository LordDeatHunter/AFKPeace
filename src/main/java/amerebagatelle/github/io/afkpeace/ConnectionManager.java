package amerebagatelle.github.io.afkpeace;

import amerebagatelle.github.io.afkpeace.miscellaneous.ReconnectThread;
import amerebagatelle.github.io.afkpeace.settings.SettingsManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ConnectionManager {
    public static ConnectionManager INSTANCE = new ConnectionManager();
    private final MinecraftClient minecraft;
    private ReconnectThread reconnectThread;

    public boolean isDisconnecting = false;
    public boolean isReconnecting = false;

    public ConnectionManager() {
        this.minecraft = MinecraftClient.getInstance();
    }

    // Handling the reconnect feature
    public void startReconnect(ServerInfo target) {
        reconnectThread = new ReconnectThread(target);
        reconnectThread.start();
        this.minecraft.openScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), "AFKPeaceReconnection", new TranslatableText("reconnect.reason")));
    }

    public void finishReconnect() {
        connectToServer(AFKPeace.currentServerEntry);
    }

    // Regular connecting utilities
    public void connectToServer(ServerInfo target) {
        this.minecraft.openScreen(new ConnectScreen(new MultiplayerScreen(new TitleScreen()), this.minecraft, target));
    }

    public void disconnectFromServer(Text reason) {
        isDisconnecting = !Boolean.parseBoolean(SettingsManager.loadSetting("reconnectOnDamageLogout"));
        this.minecraft.getNetworkHandler().getConnection().disconnect(reason);
        this.minecraft.openScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), "AFKPeaceDisconnect", reason));
    }
}
