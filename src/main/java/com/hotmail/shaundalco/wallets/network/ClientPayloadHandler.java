package de.cheaterpaul.wallets.network;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleUpdateWallet(UpdateWalletPacket msg) {
        if (Minecraft.getInstance().player != null) {
            AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
            if (container instanceof WalletContainer wallet) {
                wallet.update(msg);
            }
        }
    }
} 