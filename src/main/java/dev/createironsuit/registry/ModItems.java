package dev.createironsuit.registry;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import dev.createironsuit.CreateIronSuit;
import dev.createironsuit.SuitVariant;
import dev.createironsuit.item.IronSuitArmorItem;
import dev.createironsuit.item.RepulsorGloveItem;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateIronSuit.MODID);

    private static final ArmorItem.Type[] PIECES = {
            ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE, ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS};

    /** Todas as pecas de armadura, por variante e por tipo. */
    public static final Map<SuitVariant, Map<ArmorItem.Type, DeferredItem<IronSuitArmorItem>>> ARMOR =
            new EnumMap<>(SuitVariant.class);

    static {
        for (SuitVariant variant : SuitVariant.values()) {
            Map<ArmorItem.Type, DeferredItem<IronSuitArmorItem>> pieces = new EnumMap<>(ArmorItem.Type.class);
            for (ArmorItem.Type type : PIECES) {
                pieces.put(type, ITEMS.register(variant.id + "_" + suffix(type),
                        () -> new IronSuitArmorItem(ModArmorMaterials.BY_VARIANT.get(variant), type, variant,
                                new Item.Properties()
                                        .durability(type.getDurability(variant.durabilityFactor))
                                        .rarity(variant.heavy ? Rarity.EPIC : Rarity.RARE)
                                        .fireResistant())));
            }
            ARMOR.put(variant, pieces);
        }
    }

    private static String suffix(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> "helmet";
            case CHESTPLATE -> "chestplate";
            case LEGGINGS -> "leggings";
            default -> "boots";
        };
    }

    public static DeferredItem<IronSuitArmorItem> piece(SuitVariant variant, ArmorItem.Type type) {
        return ARMOR.get(variant).get(type);
    }

    public static List<Item> allChestplates() {
        List<Item> list = new ArrayList<>();
        for (SuitVariant v : SuitVariant.values()) list.add(piece(v, ArmorItem.Type.CHESTPLATE).get());
        return list;
    }

    public static final DeferredItem<RepulsorGloveItem> REPULSOR_GLOVE = ITEMS.register("repulsor_glove",
            () -> new RepulsorGloveItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    /** Item de transicao usado pelas receitas de Sequenced Assembly do Create. */
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_SUIT_PART = ITEMS.register("incomplete_suit_part",
            () -> new SequencedAssemblyItem(new Item.Properties()));

    public static final DeferredItem<BlockItem> CHARGER = ITEMS.register("arc_charger",
            () -> new BlockItem(ModBlocks.CHARGER.get(), new Item.Properties()));
}
