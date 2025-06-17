package de.cheaterpaul.wallets.network;

import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.inventory.WalletContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class UpdateWalletPacket {
    private final int amount;
    private final int pos;

    public UpdateWalletPacket(int amount, int pos) {
        this.amount = amount;
        this.pos = pos;
    }

    public static void encode(UpdateWalletPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.amount);
        buf.writeInt(msg.pos);
    }

    public static UpdateWalletPacket decode(FriendlyByteBuf buf) {
        return new UpdateWalletPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(UpdateWalletPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
                if (container instanceof WalletContainer wallet) {
                    wallet.update(msg);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public int getAmount() {
        return amount;
    }

    public int getPos() {
        return pos;
    }
}
