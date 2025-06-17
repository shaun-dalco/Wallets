package de.cheaterpaul.wallets.items;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.network.ModPacketDispatcher;
import de.cheaterpaul.wallets.network.UpdateWalletPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.OptionalInt;

public class WalletItem extends Item {
    public WalletItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            OptionalInt optionalInt = player.openMenu(new SimpleMenuProvider((id, playerInv, ply) -> new WalletContainer(id, playerInv, stack), Component.translatable("container.wallets.wallet")));
            if (optionalInt.isPresent() && serverPlayer.containerMenu instanceof WalletContainer wallet) {
                ModPacketDispatcher.INSTANCE.sendTo(new UpdateWalletPacket(wallet.getWalletAmount(), wallet.getWalletPos()), serverPlayer.connection.connection, net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, List<Component> components, @NotNull TooltipFlag flag) {
        int value = getEmeraldValue(stack);
        components.add(Component.translatable("text.wallets.wallet.stored", value).withStyle(ChatFormatting.DARK_GRAY));
    }

    public static int getEmeraldValue(ItemStack stack) {
        return stack.getOrCreateTag().getInt("emerald_value");
    }

    public static void setEmeraldValue(ItemStack stack, int newValue) {
        if (newValue > 999999999) { // limit wallet amount to ensure right screen rendering
            newValue = 999999999;
        }
        stack.getOrCreateTag().putInt("emerald_value", newValue);
    }
}
