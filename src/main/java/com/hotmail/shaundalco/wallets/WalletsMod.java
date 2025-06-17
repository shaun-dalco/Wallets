package de.cheaterpaul.wallets;

import de.cheaterpaul.wallets.client.core.ModScreens;
import de.cheaterpaul.wallets.config.Config;
import de.cheaterpaul.wallets.data.ItemModelGenerator;
import de.cheaterpaul.wallets.data.RecipeGenerator;
import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.WalletItem;
import de.cheaterpaul.wallets.network.ModPacketDispatcher;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod(REFERENCE.MOD_ID)
public class WalletsMod
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, REFERENCE.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, REFERENCE.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, REFERENCE.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register(REFERENCE.MOD_ID, WalletsMod::createCreativeTab);
    public static final RegistryObject<WalletItem> WALLET = ITEMS.register("wallet", () -> new WalletItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<MenuType<WalletContainer>> WALLET_CONTAINER = MENUS.register("wallet_container", () -> new MenuType<>(WalletContainer::new, FeatureFlags.DEFAULT_FLAGS));

    public WalletsMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::gatherData);
        modBus.addListener(this::doClientStuff);
        ITEMS.register(modBus);
        MENUS.register(modBus);
        CREATIVE_TABS.register(modBus);
        MinecraftForge.EVENT_BUS.register(this);

        ModPacketDispatcher.register();
        Config.init();
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(event.includeClient(), new ItemModelGenerator(gen.getPackOutput(), event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new RecipeGenerator(gen.getPackOutput()));
    }

    private static CreativeModeTab createCreativeTab() {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(WALLET.get()))
                .title(Component.translatable("itemGroup." + REFERENCE.MOD_ID))
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .displayItems((params, output) -> {
                    output.accept(WALLET.get());
                }).build();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModScreens.registerScreens();
        }
    }
}
