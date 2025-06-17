package de.cheaterpaul.wallets.server;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.network.InputEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ServerPayloadHandler {

    public static void handleInputEventPacket(InputEventPacket packet, ServerPlayer player) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu instanceof WalletContainer wallet) {
            switch (packet.getAction()) {
                case INSERT_EMERALD -> wallet.insertEmerald();
                case TAKE_EMERALD -> wallet.takeEmerald(Integer.parseInt(packet.getParam()));
            }
        }
    }
}
