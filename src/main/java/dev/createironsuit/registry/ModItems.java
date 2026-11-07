package dev.createironsuit.registry;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import dev.createironsuit.CreateIronSuit;
import dev.createironsuit.item.IronSuitArmorItem;
import dev.createironsuit.item.RepulsorGloveItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateIronSuit.MODID);

    private static Item.Properties armorProps(ArmorItem.Type type, int factor) {
        return new Item.Properties().durability(type.getDurability(factor)).rarity(Rarity.EPIC).fireResistant();
    }

    public static final DeferredItem<IronSuitArmorItem> HELMET = ITEMS.register("iron_suit_helmet",
            () -> new IronSuitArmorItem(ModArmorMaterials.IRON_SUIT, ArmorItem.Type.HELMET, armorProps(ArmorItem.Type.HELMET, 37)));
    public static final DeferredItem<IronSuitArmorItem> CHESTPLATE = ITEMS.register("iron_suit_chestplate",
            () -> new IronSuitArmorItem(ModArmorMaterials.IRON_SUIT, ArmorItem.Type.CHESTPLATE, armorProps(ArmorItem.Type.CHESTPLATE, 37)));
    public static final DeferredItem<IronSuitArmorItem> LEGGINGS = ITEMS.register("iron_suit_leggings",
            () -> new IronSuitArmorItem(ModArmorMaterials.IRON_SUIT, ArmorItem.Type.LEGGINGS, armorProps(ArmorItem.Type.LEGGINGS, 37)));
    public static final DeferredItem<IronSuitArmorItem> BOOTS = ITEMS.register("iron_suit_boots",
            () -> new IronSuitArmorItem(ModArmorMaterials.IRON_SUIT, ArmorItem.Type.BOOTS, armorProps(ArmorItem.Type.BOOTS, 37)));

    public static final DeferredItem<RepulsorGloveItem> REPULSOR_GLOVE = ITEMS.register("repulsor_glove",
            () -> new RepulsorGloveItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    /** Item de transicao usado pelas receitas de Sequenced Assembly do Create. */
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_SUIT_PART = ITEMS.register("incomplete_suit_part",
            () -> new SequencedAssemblyItem(new Item.Properties()));

    public static final DeferredItem<BlockItem> CHARGER = ITEMS.register("arc_charger",
            () -> new BlockItem(ModBlocks.CHARGER.get(), new Item.Properties()));
}
