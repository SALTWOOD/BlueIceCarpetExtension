package top.saltwood.brine_carpet_addition.network.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.saltwood.brine_carpet_addition.network.PcaProtocol;

public record PcaProtocolDisablePayload() implements CustomPayload {
    public static final Identifier PACKET_ID = PcaProtocol.id("disable_pca_sync_protocol");
    public static final Id<PcaProtocolDisablePayload> ID = new Id<>(PACKET_ID);
    public static final PacketCodec<PacketByteBuf, PcaProtocolDisablePayload> CODEC =
            PacketCodec.unit(new PcaProtocolDisablePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}