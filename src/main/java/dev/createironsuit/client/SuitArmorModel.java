package dev.createironsuit.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

/**
 * Modelo 3D da armadura: o corpo humanoide padrao + placas extras (ombreiras, peitoral, mochila,
 * luvas, joelheiras, botas). Cada grupo de placas so aparece na peca certa.
 *
 * Textura 128x128: os 64x32 do canto superior esquerdo seguem o layout vanilla; as placas extras
 * usam tres areas: PRIMARIA (64,0), ACENTO (64,48) e BRILHO (0,64).
 */
public class SuitArmorModel extends HumanoidModel<LivingEntity> {
    private static final int PU = 64, PV = 0;    // cor primaria
    private static final int AU = 64, AV = 48;   // cor de acento
    private static final int GU = 0, GV = 64;    // brilho (reator, olhos, propulsores)

    private final ModelPart headExtra, chestExtra, beltExtra;
    private final ModelPart rArmExtra, lArmExtra, rLegExtra, lLegExtra, rBootExtra, lBootExtra;

    public SuitArmorModel(ModelPart root) {
        super(root);
        headExtra = head.getChild("head_extra");
        chestExtra = body.getChild("chest_extra");
        beltExtra = body.getChild("belt_extra");
        rArmExtra = rightArm.getChild("arm_extra");
        lArmExtra = leftArm.getChild("arm_extra");
        rLegExtra = rightLeg.getChild("leg_extra");
        lLegExtra = leftLeg.getChild("leg_extra");
        rBootExtra = rightLeg.getChild("boot_extra");
        lBootExtra = leftLeg.getChild("boot_extra");
    }

    /** Mostra so as placas da peca que esta sendo desenhada. */
    public void setSlot(EquipmentSlot slot) {
        headExtra.visible = slot == EquipmentSlot.HEAD;
        chestExtra.visible = slot == EquipmentSlot.CHEST;
        rArmExtra.visible = slot == EquipmentSlot.CHEST;
        lArmExtra.visible = slot == EquipmentSlot.CHEST;
        beltExtra.visible = slot == EquipmentSlot.LEGS;
        rLegExtra.visible = slot == EquipmentSlot.LEGS;
        lLegExtra.visible = slot == EquipmentSlot.LEGS;
        rBootExtra.visible = slot == EquipmentSlot.FEET;
        lBootExtra.visible = slot == EquipmentSlot.FEET;
    }

    private static PartDefinition group(PartDefinition parent, String name) {
        return parent.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.ZERO);
    }

    private static void box(PartDefinition g, String name, int u, int v,
                            float x, float y, float z, float sx, float sy, float sz, CubeDeformation d) {
        g.addOrReplaceChild(name, CubeListBuilder.create().texOffs(u, v).addBox(x, y, z, sx, sy, sz, d), PartPose.ZERO);
    }

    /**
     * @param baseDef inflacao do corpo base (1.0 = armadura externa, 0.5 = calcas)
     * @param extra   inflacao das placas extras (0 = normal, ~0.6 = versao pesada)
     */
    public static LayerDefinition createLayer(float baseDef, float extra) {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(baseDef), 0.0F);
        PartDefinition root = mesh.getRoot();
        CubeDeformation d = new CubeDeformation(extra);
        CubeDeformation none = CubeDeformation.NONE;

        // ---------------- CAPACETE ----------------
        PartDefinition head = group(root.getChild("head"), "head_extra");
        float front = -4.0F - baseDef;
        box(head, "cheek_r", PU, PV, -5.3F, -6.2F, -3.0F, 1.0F, 4.2F, 6.0F, d);
        box(head, "cheek_l", PU, PV, 4.3F, -6.2F, -3.0F, 1.0F, 4.2F, 6.0F, d);
        box(head, "jaw", AU, AV, -3.3F, -1.6F, front - 0.4F, 6.6F, 2.0F, 1.4F, d);
        box(head, "brow", AU, AV, -3.6F, -9.4F, front - 0.4F, 7.2F, 1.0F, 1.2F, d);
        box(head, "crest", PU, PV, -0.8F, -10.2F, -4.5F, 1.6F, 1.4F, 9.0F, d);
        box(head, "eye_r", GU, GV, -3.2F, -5.2F, front - 0.35F, 2.4F, 0.9F, 0.4F, none);
        box(head, "eye_l", GU, GV, 0.8F, -5.2F, front - 0.35F, 2.4F, 0.9F, 0.4F, none);

        // ---------------- PEITORAL (corpo, ombreiras, luvas) ----------------
        PartDefinition chest = group(root.getChild("body"), "chest_extra");
        box(chest, "pec", PU, PV, -4.6F, 0.4F, -3.3F, 9.2F, 5.2F, 1.4F, d);
        box(chest, "abs", AU, AV, -3.4F, 5.6F, -3.1F, 6.8F, 4.4F, 1.0F, d);
        box(chest, "reactor", GU, GV, -1.5F, 1.8F, -3.8F, 3.0F, 3.0F, 0.7F, none);
        box(chest, "backpack", PU, PV, -4.0F, 0.8F, 3.0F, 8.0F, 8.4F, 2.0F, d);
        box(chest, "thruster_r", GU, GV, -3.0F, 8.6F, 4.4F, 2.0F, 2.0F, 1.2F, none);
        box(chest, "thruster_l", GU, GV, 1.0F, 8.6F, 4.4F, 2.0F, 2.0F, 1.2F, none);

        PartDefinition rArm = group(root.getChild("right_arm"), "arm_extra");
        box(rArm, "pauldron", PU, PV, -4.6F, -3.4F, -3.2F, 5.2F, 3.0F, 6.4F, d);
        box(rArm, "pauldron_trim", AU, AV, -4.8F, -1.0F, -3.0F, 5.6F, 0.8F, 6.0F, d);
        box(rArm, "gauntlet", AU, AV, -3.5F, 5.5F, -2.5F, 5.0F, 5.0F, 5.0F, d);
        box(rArm, "palm", GU, GV, -2.0F, 10.5F, -1.0F, 2.0F, 0.6F, 2.0F, none);

        PartDefinition lArm = group(root.getChild("left_arm"), "arm_extra");
        box(lArm, "pauldron", PU, PV, -0.6F, -3.4F, -3.2F, 5.2F, 3.0F, 6.4F, d);
        box(lArm, "pauldron_trim", AU, AV, -0.8F, -1.0F, -3.0F, 5.6F, 0.8F, 6.0F, d);
        box(lArm, "gauntlet", AU, AV, -1.5F, 5.5F, -2.5F, 5.0F, 5.0F, 5.0F, d);
        box(lArm, "palm", GU, GV, 0.0F, 10.5F, -1.0F, 2.0F, 0.6F, 2.0F, none);

        // ---------------- CALCAS (cinto, coxas, joelheiras) ----------------
        PartDefinition belt = group(root.getChild("body"), "belt_extra");
        box(belt, "belt", AU, AV, -4.5F, 9.4F, -2.6F, 9.0F, 2.0F, 5.2F, d);
        box(belt, "buckle", GU, GV, -1.0F, 9.6F, -3.1F, 2.0F, 1.6F, 0.5F, none);

        for (String side : new String[]{"right_leg", "left_leg"}) {
            PartDefinition legs = group(root.getChild(side), "leg_extra");
            box(legs, "thigh", PU, PV, -2.6F, 0.2F, -2.6F, 5.2F, 5.0F, 5.2F, d);
            box(legs, "knee", AU, AV, -2.3F, 5.4F, -3.1F, 4.6F, 2.2F, 1.0F, d);

            // ---------------- BOTAS ----------------
            PartDefinition boots = group(root.getChild(side), "boot_extra");
            box(boots, "boot", PU, PV, -2.6F, 8.0F, -2.6F, 5.2F, 4.0F, 5.2F, d);
            box(boots, "toe", AU, AV, -2.4F, 10.2F, -4.4F, 4.8F, 1.8F, 2.0F, d);
            box(boots, "boot_thruster", GU, GV, -1.5F, 9.0F, 2.4F, 3.0F, 2.0F, 1.0F, none);
        }

        return LayerDefinition.create(mesh, 128, 128);
    }
}
