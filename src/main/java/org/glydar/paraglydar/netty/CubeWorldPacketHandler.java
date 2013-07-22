package org.glydar.paraglydar.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import org.glydar.paraglydar.models.Player;
import org.glydar.paraglydar.netty.packet.CubeWorldPacket;

public class CubeWorldPacketHandler extends SimpleChannelInboundHandler<CubeWorldPacket> {
	@Override
	protected void messageReceived(ChannelHandlerContext channelHandlerContext, CubeWorldPacket cubeWorldPacket) throws Exception {
		Attribute<Player> playerAttrib = channelHandlerContext.attr(CubeWorldServerInitializer.PLAYER_ATTRIBUTE_KEY);
		Player player = playerAttrib.get();
		if(player == null) {
			player = new Player();
            player.setChannelContext(channelHandlerContext);
			playerAttrib.set(player);
		}
        try {
		    cubeWorldPacket.receivedFrom(player);
        } catch (IllegalAccessError e) {
            //System.out.println("No handler for packet ID "+((CubeWorldPacket.Packet)cubeWorldPacket.getClass().getAnnotation(CubeWorldPacket.Packet.class)).id());
        }
	}
}
