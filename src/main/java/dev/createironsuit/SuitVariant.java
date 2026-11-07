package dev.createironsuit;

import net.minecraft.world.item.ArmorItem;

/** As 5 armaduras do mod. Cada uma tem status, habilidades e modelo proprios. */
public enum SuitVariant {
    //          id             dur  botas leg  pei  cap  tough knock heavy fly    resist str speed jump
    CLASSIC("iron_suit",       37,  3,    6,   8,   3,   3.0F, 0.10F, false, 0.10F, 1,    1,  0,    1),
    STEALTH("stealth_suit",    33,  3,    6,   7,   3,   2.0F, 0.00F, false, 0.12F, 0,    0,  1,    2),
    COBALT("cobalt_suit",      35,  3,    6,   8,   3,   3.0F, 0.05F, false, 0.14F, 1,    0,  1,    1),
    SILVER("silver_suit",      40,  4,    7,   9,   4,   3.5F, 0.10F, false, 0.10F, 1,    1,  0,    1),
    TITAN("titan_suit",        50,  5,    8,   12,  5,   5.0F, 0.40F, true,  0.06F, 2,    2,  0,    0);

    public final String id;
    public final int durabilityFactor;
    public final int bootsDefense, leggingsDefense, chestDefense, helmetDefense;
    public final float toughness, knockbackResistance;
    /** Armadura pesada (modelo grande, tipo "buster"). */
    public final boolean heavy;
    public final float flySpeed;
    /** Amplificador dos efeitos passivos (0 = nivel I, 1 = nivel II...). */
    public final int resistance, strength, speed, jump;

    SuitVariant(String id, int durabilityFactor, int boots, int leggings, int chest, int helmet,
                float toughness, float knockbackResistance, boolean heavy, float flySpeed,
                int resistance, int strength, int speed, int jump) {
        this.id = id;
        this.durabilityFactor = durabilityFactor;
        this.bootsDefense = boots;
        this.leggingsDefense = leggings;
        this.chestDefense = chest;
        this.helmetDefense = helmet;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.heavy = heavy;
        this.flySpeed = flySpeed;
        this.resistance = resistance;
        this.strength = strength;
        this.speed = speed;
        this.jump = jump;
    }

    public int defense(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> bootsDefense;
            case LEGGINGS -> leggingsDefense;
            case CHESTPLATE -> chestDefense;
            case HELMET -> helmetDefense;
            default -> chestDefense;
        };
    }
}
