package de.cheaterpaul.wallets.data;

import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider implements IConditionBuilder {
    public RecipeGenerator(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WalletsMod.WALLET.get())
                .pattern("LLL")
                .pattern("LSL")
                .pattern("LLL")
                .define('L', Items.LEATHER)
                .define('S', Items.STRING)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .unlockedBy("has_string", has(Items.STRING))
                .save(consumer, new ResourceLocation(REFERENCE.MOD_ID, "wallet"));
    }
}
