package de.cheaterpaul.wallets.network;

import de.cheaterpaul.wallets.REFERENCE;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPacketDispatcher {
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static int packetId = 0;

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(REFERENCE.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        INSTANCE.messageBuilder(InputEventPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(InputEventPacket::encode)
                .decoder(InputEventPacket::decode)
                .consumerMainThread(InputEventPacket::handle)
                .add();

        INSTANCE.messageBuilder(UpdateWalletPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(UpdateWalletPacket::encode)
                .decoder(UpdateWalletPacket::decode)
                .consumerMainThread(UpdateWalletPacket::handle)
                .add();
    }
}
