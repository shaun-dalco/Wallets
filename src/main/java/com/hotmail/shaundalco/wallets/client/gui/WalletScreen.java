package de.cheaterpaul.wallets.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.network.InputEventPacket;
import de.cheaterpaul.wallets.network.ModPacketDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WalletScreen extends AbstractContainerScreen<WalletContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MOD_ID, "textures/gui/wallet.png");

    private EditBox sumField;
    private EditBox walletSum;

    public WalletScreen(WalletContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.imageHeight = 166;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX = 8;
    }

    @Override
    protected void init() {
        super.init();

        var depositEmerald = this.addRenderableWidget(new Button.Builder(Component.literal("+"), this::depositEmerald)
                .pos(this.getGuiLeft() + 117, this.getGuiTop() + 52)
                .size(14, 14)
                .build());
        depositEmerald.setTooltip(Tooltip.create(Component.translatable("text.wallets.deposit")));

        var withdrawEmerald = this.addRenderableWidget(new Button.Builder(Component.literal("-"), this::withdrawEmerald)
                .pos(this.getGuiLeft() + 135, this.getGuiTop() + 52)
                .size(14, 14)
                .build());
        withdrawEmerald.setTooltip(Tooltip.create(Component.translatable("text.wallets.withdraw")));

        this.sumField = new EditBox(this.font, this.getGuiLeft() + 41, this.getGuiTop() + 53, 57, 12, Component.empty());
        this.sumField.setValue("1");
        this.sumField.setMaxLength(5);
        this.sumField.setFilter(s -> s.matches("\\d*"));
        this.addRenderableWidget(this.sumField);
        this.setInitialFocus(this.sumField);
        this.titleLabelX = 15;
        this.titleLabelY = 5;
        this.inventoryLabelX = 15;
        this.inventoryLabelY = 72;

        this.walletSum = new NoEditTextFieldWidget(this.font, this.leftPos + 43, this.topPos + 24, 55, 10, Component.translatable("text.wallets.wallet_sum"));
        this.walletSum.setMaxLength(10);
        this.walletSum.setBordered(false);
        this.walletSum.setEditable(false);
        this.walletSum.setTextColorUneditable(14737632);
        this.walletSum.setValue(String.valueOf(this.menu.getWalletAmount()));
        this.addWidget(this.walletSum);
    }

    @Override
    public void resize(@Nonnull Minecraft minecraft, int xSize, int ySize) {
        String walletAmount = this.walletSum.getValue();
        String sumAmount = this.sumField.getValue();
        super.resize(minecraft, xSize, ySize);
        this.walletSum.setValue(walletAmount);
        this.sumField.setValue(sumAmount);
    }

    @Override
    protected void containerTick() {
        this.sumField.tick();
    }

    private void depositEmerald(Button b) {
        Slot emeraldSlot = this.menu.getSlot(0);
        if (emeraldSlot.hasItem() && emeraldSlot.getItem().getItem() == Items.EMERALD) {
            ModPacketDispatcher.INSTANCE.sendToServer(new InputEventPacket(InputEventPacket.Action.INSERT_EMERALD, ""));
            this.walletSum.setValue(String.valueOf(this.menu.getWalletAmount()));
        }
    }

    private void withdrawEmerald(Button b) {
        try {
            int amount = Integer.parseInt(this.sumField.getValue());
            if (amount > 0 && amount <= this.menu.getWalletAmount()) {
                ModPacketDispatcher.INSTANCE.sendToServer(new InputEventPacket(InputEventPacket.Action.TAKE_EMERALD, String.valueOf(amount)));
            }
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.walletSum.setValue(String.valueOf(this.menu.getWalletAmount()));
        this.walletSum.render(graphics, mouseX, mouseY, partialTicks);
        this.sumField.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
        graphics.drawString(this.font, Component.translatable("text.wallets.balance"), this.getGuiLeft() + 70 - this.font.width(Component.translatable("text.wallets.balance").getVisualOrderText()) / 2, this.getGuiTop() + 13, 0x404040, false);
        graphics.drawString(this.font, Component.translatable("text.wallets.withdraw"), this.getGuiLeft() + 41 + (57 / 2) - this.font.width(Component.translatable("text.wallets.withdraw").getVisualOrderText()) / 2, this.getGuiTop() + 41, 0x404040, false);
    }

    private static class NoEditTextFieldWidget extends EditBox {
        public NoEditTextFieldWidget(Font p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, Component p_i232260_6_) {
            super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
        }

        @Override
        public boolean isFocused() {
            return false;
        }
    }
}
