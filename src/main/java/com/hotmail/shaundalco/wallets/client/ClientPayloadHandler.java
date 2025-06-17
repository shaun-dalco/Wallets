package de.cheaterpaul.wallets.client;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.network.UpdateWalletPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ClientPayloadHandler {

    public static void handleUpdateWalletPacket(UpdateWalletPacket packet) {
        if (Minecraft.getInstance().player != null) {
            AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
            if (container instanceof WalletContainer wallet) {
                wallet.update(packet);
            }
        }
    }
}
