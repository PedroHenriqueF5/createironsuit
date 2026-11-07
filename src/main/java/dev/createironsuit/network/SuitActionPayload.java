package dev.createironsuit.network;

import dev.createironsuit.CreateIronSuit;
import dev.createironsuit.SuitAbilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Pacote enviado pelo cliente quando o jogador aperta uma tecla de poder. */
public record SuitActionPayload(int action) implements CustomPacketPayload {
    public static final Type<SuitActionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateIronSuit.MODID, "suit_action"));

    public static final StreamCodec<ByteBuf, SuitActionPayload> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(SuitActionPayload::new, SuitActionPayload::action);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SuitActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                SuitAbilities.perform(player, payload.action());
            }
        });
    }
}
