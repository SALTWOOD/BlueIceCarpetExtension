package top.saltwood.brine_carpet_addition.network.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.saltwood.brine_carpet_addition.Main;
import top.saltwood.brine_carpet_addition.network.PcaProtocol;

public record PcaProtocolEnablePayload() implements CustomPayload {
    public static final Identifier PACKET_ID = PcaProtocol.id("enable_pca_sync_protocol");
    public static final Id<PcaProtocolEnablePayload> ID = new Id<>(PACKET_ID);
    public static final PacketCodec<PacketByteBuf, PcaProtocolEnablePayload> CODEC =
            PacketCodec.unit(new PcaProtocolEnablePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}