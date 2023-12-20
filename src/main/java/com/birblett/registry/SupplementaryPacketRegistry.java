package com.birblett.registry;

import com.birblett.Supplementary;
import com.birblett.lib.accessor.ServerPlayerInteractionManagerInterface;
import com.birblett.lib.api.SupplementaryPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SupplementaryPacketRegistry {

    public static final Identifier HITSCAN_PARTICLE_S2C_PACKET = new Identifier(Supplementary.MODID, "hitscan");
    public static final Identifier MINING_DIRECTION_C2S_PACKET = new Identifier(Supplementary.MODID, "mining_direction");

    public static class HitscanS2CPacket extends SupplementaryPacket<Vector3f> {

        public HitscanS2CPacket(List<Vector3f> path) {
            super(HITSCAN_PARTICLE_S2C_PACKET, path.size());
            path.forEach(this::write);
        }

        @Override
        public void write(Vector3f data) {
            if (this.length > this.max) {
                Supplementary.LOGGER.info("malformed packet of type {}: exceed max length {}", this.id, this.length);
            }
            else {
                this.buf.writeVector3f(data);
                this.length++;
            }
        }

        static void recv(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            if (client.world != null) {
                short s = buf.readShort();
                List<Vector3f> path = new ArrayList<>();
                for (short i = 0; i < s; i++) {
                    path.add(buf.readVector3f());
                }
                for (int i = 0; i < path.size() - 1; i++) {
                    Vector3f pos = path.get(i);
                    Vector3f other = path.get(i + 1);
                    client.execute(() -> {
                        for (int j = 0; j < 4; j++) {
                            Vector3f temp = new Vector3f();
                            pos.lerp(other, j * 0.25f, temp);
                            client.world.addParticle(ParticleTypes.END_ROD, true, temp.x, temp.y, temp.z, 0, 0, 0);
                        }
                    });
                }
            }
        }
    }

    public static class MiningDirectionC2SPacket extends SupplementaryPacket<Direction> {

        public MiningDirectionC2SPacket(Direction d) {
            super(MINING_DIRECTION_C2S_PACKET, 0);
            this.write(d);
        }

        @Override
        public void write(Direction d) {
            this.buf.writeInt(d.getId());
        }

        static void recv(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            buf.readShort();
            Direction direction = Direction.byId(buf.readInt());
            server.execute(() -> ((ServerPlayerInteractionManagerInterface) handler.getPlayer().interactionManager).setMiningDirection(
                    direction));
        }
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(HITSCAN_PARTICLE_S2C_PACKET, HitscanS2CPacket::recv);
        ServerPlayNetworking.registerGlobalReceiver(MINING_DIRECTION_C2S_PACKET, MiningDirectionC2SPacket::recv);
    }
}
