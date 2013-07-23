package org.glydar.glydar.netty.packet.shared;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.glydar.glydar.models.Player;
import org.glydar.glydar.netty.data.EntityData;
import org.glydar.glydar.netty.packet.CubeWorldPacket;
import org.glydar.glydar.util.ZLibOperations;

import java.nio.ByteOrder;

@CubeWorldPacket.Packet(id = 0, variableLength = true)
public class Packet0EntityUpdate extends CubeWorldPacket {
	 byte[] rawData;
     EntityData ed;

    public Packet0EntityUpdate() {
        ed = new EntityData();
    }

	@Override
	protected boolean doCacheIncoming() {
		return true;
	}

	@Override
	protected void internalDecode(ByteBuf buffer) {
		int zlibLength = buffer.readInt();
		rawData = new byte[zlibLength];
		buffer.readBytes(rawData);
        try {
            ByteBuf dataBuf = Unpooled.copiedBuffer(this.rawData);
            dataBuf.order(ByteOrder.LITTLE_ENDIAN);
            ed.decode(dataBuf);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void receivedFrom(Player ply) {
        if(!ply.joined) {
           ply.data = this.ed;
        }
        ply.playerJoined();
		this.sendToAll();
    }

    @Override
    protected void internalEncode(ByteBuf buf) {
        buf.writeInt(rawData.length);
        buf.writeBytes(rawData);
    }
}
