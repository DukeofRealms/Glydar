package org.glydar.glydar.models;

import io.netty.channel.ChannelHandlerContext;
import org.glydar.glydar.netty.data.EntityData;
import org.glydar.glydar.netty.packet.CubeWorldPacket;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Player extends Entity implements BaseTarget {
    private static HashMap<Long, Player> connectedPlayers = new HashMap<Long, Player>();

    public boolean joined = false;
    public EntityData data;
    public byte[] debugCompressedRawData; //TODO: THIS IS A GIANT WORKAROUND. PLEASE PLEASE PLEASE FIND A WAY TO REMOVE ME :C
    private ChannelHandlerContext channelCtx;

	public void setChannelContext(ChannelHandlerContext ctx) {
        this.channelCtx = ctx;
    }

    public ChannelHandlerContext getChannelContext() {
        return this.channelCtx;
    }

	public void sendPacket(CubeWorldPacket packet) {
		packet.sendTo(this);
	}

	@Override
	public Collection<Player> getPlayers() {
		Collection<Player> ret = new HashSet<Player>();
		ret.add(this);
		return ret;
	}

    public static Collection<Player> getConnectedPlayers() {
        return connectedPlayers.values();
    }

	public void playerJoined() {
		if(!connectedPlayers.containsKey(entityID)) {
			connectedPlayers.put(entityID, this);
            this.joined = true;
        }
	}

	public void playerLeft() {
		connectedPlayers.remove(entityID);
	}
}
