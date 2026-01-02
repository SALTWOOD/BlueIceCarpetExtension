package top.saltwood.brine_carpet_addition.network.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.saltwood.brine_carpet_addition.Main;

public record BcaProtocolEnablePayload() implements CustomPayload {
    public static final Identifier PACKET_ID = Main.id("protocol_enable");
    public static final Id<BcaProtocolEnablePayload> ID = new Id<>(PACKET_ID);
    public static final PacketCodec<PacketByteBuf, BcaProtocolEnablePayload> CODEC =
            PacketCodec.unit(new BcaProtocolEnablePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}