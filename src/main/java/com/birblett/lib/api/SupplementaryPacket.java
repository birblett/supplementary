package com.birblett.lib.api;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public abstract class SupplementaryPacket<T> {

    public final Identifier id;
    public final PacketByteBuf buf;
    protected int length = 0;
    public final int max;

    public SupplementaryPacket(Identifier id, int length) {
        this.id = id;
        this.buf = PacketByteBufs.create();
        buf.writeShort(length);
        this.max = length;
    }

    public void send(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, this.id, this.buf);
    }

    public void sendC2S() {
        ClientPlayNetworking.send(this.id, this.buf);
    }

    public abstract void write(T data);
}
