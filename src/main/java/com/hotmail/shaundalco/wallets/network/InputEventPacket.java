package de.cheaterpaul.wallets.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InputEventPacket {
    private final Action action;
    private final String param;

    public InputEventPacket(Action action, String param) {
        this.action = action;
        this.param = param;
    }

    public static void encode(InputEventPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.action);
        buf.writeUtf(msg.param);
    }

    public static InputEventPacket decode(FriendlyByteBuf buf) {
        return new InputEventPacket(buf.readEnum(Action.class), buf.readUtf());
    }

    public static void handle(InputEventPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                de.cheaterpaul.wallets.server.ServerPayloadHandler.handleInputEventPacket(msg, ctx.get().getSender());
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public Action getAction() {
        return action;
    }

    public String getParam() {
        return param;
    }

    public enum Action {
        INSERT_EMERALD,
        TAKE_EMERALD
    }
}
