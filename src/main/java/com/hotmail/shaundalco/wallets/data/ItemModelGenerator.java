package de.cheaterpaul.wallets.data;

import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, REFERENCE.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(WalletsMod.WALLET.getId().toString(), mcLoc("item/generated")).texture("layer0", REFERENCE.MOD_ID + ":item/" + WalletsMod.WALLET.getId().getPath());
    }
}
