package net.cwserver.netty.packet.client;

import io.netty.buffer.ByteBuf;
import net.cwserver.models.Player;
import net.cwserver.netty.data.Item;
import net.cwserver.netty.packet.CubeWorldPacket;

@CubeWorldPacket.Packet(id = 6)
public class Packet6Interaction extends CubeWorldPacket {

    Item item;
    int chunkX, chunkY;
    int itemIndex; //Index of item in ChunkItems
    long something4; //uint
    byte interactType; //TODO ENUM
    byte something6;
    int something7; //ushort

	@Override
	protected void internalDecode(ByteBuf buf) {
		item = new Item();
        item.decode(buf);
        chunkX = buf.readInt();
        chunkY = buf.readInt();
        itemIndex = buf.readInt();
        something4 = buf.readUnsignedInt();
        interactType = buf.readByte();
        something6 = buf.readByte();
        something7 = buf.readUnsignedShort();

	}

    @Override
    protected void internalEncode(ByteBuf buf) {
        item.encode(buf);
        buf.writeInt(chunkX);
        buf.writeInt(chunkY);
        buf.writeInt(itemIndex);
        buf.writeInt((int)something4);
        buf.writeByte(interactType);
        buf.writeByte(something6);
        buf.writeShort(something7);
    }

    @Override
    public void receivedFrom(Player ply) {

    }
}
