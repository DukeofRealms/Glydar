package net.cwserver.netty.data;

import io.netty.buffer.ByteBuf;

public interface BaseData {
	public void decode(ByteBuf buf);
}
