package de.cheaterpaul.wallets.inventory;

import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.items.WalletItem;
import de.cheaterpaul.wallets.network.ModPacketDispatcher;
import de.cheaterpaul.wallets.network.UpdateWalletPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;

public class WalletContainer extends AbstractContainerMenu {

    protected final SimpleContainer inventory;
    private final ItemStack walletStack;
    private int walletAmount;
    private int walletPos;
    private final Player player;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public WalletContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, ItemStack.EMPTY);
    }

    public WalletContainer(int id, Inventory playerInventory, ItemStack stack) {
        super(WalletsMod.WALLET_CONTAINER.get(), id);
        this.player = playerInventory.player;
        this.walletStack = stack;
        this.inventory = new SimpleContainer(1);
        this.addSlots(inventory);
        this.addPlayerSlots(playerInventory);
        this.walletAmount = WalletItem.getEmeraldValue(stack);
        if (!player.level().isClientSide) {
            this.walletPos = playerInventory.selected;
        }
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        updateClient();
    }

    public int getWalletAmount() {
        return this.walletAmount;
    }

    protected void addSlots(Container inventory) {
        // Only add the emerald slot
        this.addSlot(new Slot(inventory, 0, 118, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.EMERALD;
            }
        });
    }

    protected void addPlayerSlots(Inventory playerInventory) {
        // Main inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Hotbar slots
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new WalletSafeSlot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(slotStack, 1, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (slotStack.getItem() == Items.EMERALD) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    if (!this.moveItemStackTo(slotStack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37) {
                    if (!this.moveItemStackTo(slotStack, 1, 28, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, slotStack);
        }

        return result;
    }

    public void insertEmerald() {
        if (player.level().isClientSide) return;
        ItemStack stack = this.inventory.getItem(0);
        if (!stack.isEmpty() && stack.getItem() == Items.EMERALD) {
            int emeraldCount = stack.getCount();
            if (getWalletAmount() + emeraldCount > 999999999) return; // limit wallet amount
            this.inventory.setItem(0, ItemStack.EMPTY);
            this.addWalletEmeralds(emeraldCount);
            this.broadcastChanges(); // Ensure client is updated
        }
    }

    public void updateClient() {
        if (!(this.player instanceof ServerPlayer serverPlayer)) return;
        ModPacketDispatcher.INSTANCE.sendTo(new UpdateWalletPacket(this.walletAmount, this.walletPos), serverPlayer.connection.connection, net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
    }

    public void takeEmerald(int amount) {
        if (amount <= 0 || amount > this.walletAmount) return;
        ItemStack emeraldStack = new ItemStack(Items.EMERALD, amount);
        if (player.getInventory().add(emeraldStack)) {
            addWalletEmeralds(-amount);
            this.broadcastChanges(); // Ensure client is updated
        }
    }

    private void addWalletEmeralds(int amount) {
        this.walletAmount += amount;
        WalletItem.setEmeraldValue(this.walletStack, this.walletAmount);
        this.updateClient();
    }

    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
        this.clearContainer(player, this.inventory);
    }

    @Override
    public boolean stillValid(@Nonnull Player p_75145_1_) {
        return true;
    }

    public void update(UpdateWalletPacket msg) {
        this.walletAmount = msg.getAmount();
        this.walletPos = msg.getPos();
    }

    public int getWalletPos() {
        return this.walletPos;
    }

    public class WalletSafeSlot extends Slot {
        public WalletSafeSlot(Container inventory, int slot, int xPos, int yPos) {
            super(inventory, slot, xPos, yPos);
        }

        @Override
        public boolean mayPickup(@Nonnull Player player) {
            return this.getItem().getItem() != WalletsMod.WALLET.get();
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (this.getItem().getItem() == WalletsMod.WALLET.get()) {
                WalletContainer.this.updateClient();
            }
        }
    }
}
