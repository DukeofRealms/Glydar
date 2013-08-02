package org.glydar.glydar.netty.packet.shared;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.glydar.glydar.Glydar;
import org.glydar.glydar.models.Player;
import org.glydar.glydar.netty.data.EntityData;
import org.glydar.glydar.netty.packet.CubeWorldPacket;
import org.glydar.glydar.util.ZLibOperations;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteOrder;

@CubeWorldPacket.Packet(id = 0, variableLength = true)
public class Packet0EntityUpdate extends CubeWorldPacket {
	 byte[] rawData;
     EntityData ed;
     boolean sendEntityData = false;


    public Packet0EntityUpdate() {
        ed = new EntityData();
    }

    public Packet0EntityUpdate(byte[] rawData) {
        this.rawData = rawData;
    }

    public Packet0EntityUpdate(EntityData e) {
        this.ed = e;
        sendEntityData = true;
    }

	@Override
	protected boolean doCacheIncoming() {
		return false;
	}

	@Override
	protected void internalDecode(ByteBuf buffer) {
		int zlibLength = buffer.readInt();
		rawData = new byte[zlibLength];
		buffer.readBytes(rawData);
        try {
            ByteBuf dataBuf = Unpooled.copiedBuffer(ZLibOperations.decompress(this.rawData));
            dataBuf = dataBuf.order(ByteOrder.LITTLE_ENDIAN);
            ed.decode(dataBuf);
        } catch (Exception e) {
        }
    }

    @Override
    public void receivedFrom(Player ply) {
        if(!ply.joined) {
            ply.data = this.ed;
            Glydar.getServer().getLogger().info("Player " + ply.data.name + " joined with entity ID " + ply.data.id + "! (Internal ID " + ply.entityID + ")");
            //TODO Send all current entity data and NOT just existing players
            for (Player p : Player.getConnectedPlayers()) {
                if(p.entityID == ply.entityID) {
                    Glydar.getServer().getLogger().warning("I found myself! o.o");
                    continue;
                }
                ply.sendPacket(new Packet0EntityUpdate(p.data));
            }
        }
        ply.playerJoined();
		this.sendToAll();
    }

    @Override
    protected void internalEncode(ByteBuf buf) {
        if(!sendEntityData) {
            buf.writeInt(rawData.length);
            buf.writeBytes(rawData);
        } else {
            ByteBuf buf2 = Unpooled.buffer();
            buf2 = buf2.order(ByteOrder.LITTLE_ENDIAN);
            ed.encode(buf2);
            byte[] compressedData = null;
            try {
                compressedData = ZLibOperations.compress(buf2.array());
            } catch (Exception e) {}
            if(compressedData != null) {
                buf.writeInt(compressedData.length);
                buf.writeBytes(compressedData);
            } else {
                Glydar.getServer().getLogger().warning("Compressed data is null, I'm just writing the raw data back!");
                buf.writeInt(rawData.length);
                buf.writeBytes(rawData);
           }
        }
    }
}
