package com.mgs;

import com.mgs.registries.MgsBlockEntities;
import com.mgs.registries.MgsBlocks;
import com.mgs.registries.MgsDataComponents;
import com.mgs.registries.MgsItems;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Mgs.MODID)
public class Mgs {
    public static final String MODID = "mgs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Mgs(IEventBus eventBus) {
        // ★ MODアイテムの登録
        MgsItems.ITEMS.register(eventBus);
        // ★ MODデータコンポーネントの登録
        MgsDataComponents.DC.register(eventBus);
        // ★ MODブロック・ブロックエンティティの登録
        MgsBlocks.BLOCKS.register(eventBus);
        MgsBlockEntities.BLOCK_ENTITIES.register(eventBus);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}