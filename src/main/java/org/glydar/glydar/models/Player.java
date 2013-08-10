package org.glydar.glydar.models;

import io.netty.channel.ChannelHandlerContext;

import org.glydar.glydar.Glydar;
import org.glydar.glydar.netty.data.EntityData;
import org.glydar.glydar.netty.packet.CubeWorldPacket;
import org.glydar.glydar.netty.packet.shared.Packet0EntityUpdate;
import org.glydar.glydar.netty.packet.shared.Packet10Chat;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.glydar.glydar.permissions.Permissible;
import org.glydar.glydar.permissions.Permission;
import org.glydar.glydar.permissions.PermissionAttachment;

public class Player extends Entity implements BaseTarget, Permissible {
    private static HashMap<Long, Player> connectedPlayers = new HashMap<Long, Player>();

    public boolean joined = false;
    private EntityData data;
    private ChannelHandlerContext channelCtx;
    private boolean admin;

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
        forceUpdateData();
    }

    /**
     * Temporary fix to allow plugins to manipulate entityData while we fix other issues.
     * Call this whenever you modify anything in Player.data and wish to update all of the clients.
     */
    public void forceUpdateData() {
        new Packet0EntityUpdate(this.data).sendToAll();
    }
    
    public void forceUpdateData(EntityData ed){
    	this.data = ed;
    	new Packet0EntityUpdate(this.data).sendToAll();
    }

    public static Player getPlayerByEntityID(long id) {
        if(connectedPlayers.containsKey(id))
            return connectedPlayers.get(id);
        else
        {
            Glydar.getServer().getLogger().warning("Unable to find player with entity ID "+id+"! Returning null!");
            return null;
        }
    }
    
    public EntityData getEntityData(){
    	if (data == null){
    		data = new EntityData();
    	}
    	return data;
    }

	public void setEntityData(EntityData ed) {
		this.data = ed;
	}
	
	public String getIp(){
		return ((InetSocketAddress)channelCtx.channel().remoteAddress()).getAddress().getHostAddress();
	}
	
	public void sendMessageToPlayer(String message){
		this.sendPacket(new Packet10Chat(message, 0));
	}
	
	public void kickPlayer(String message){
		sendMessageToPlayer(message);
		channelCtx.disconnect();
	}
	
	public void kickPlayer(){
		sendMessageToPlayer("You have been kicked!");
		channelCtx.disconnect();
	}

    @Override
    public boolean hasPermission(String permission) {
        return hasPermission(new Permission(permission, Permission.PermissionDefault.FALSE));
    }

    @Override
    public boolean hasPermission(Permission permission) {
        if (getAttachments() == null || getAttachments().isEmpty()) {
            switch (permission.getPermissionDefault()) {
                case TRUE: return true;
                case FALSE: return false;
                case ADMIN: return isAdmin();
                case NON_ADMIN: return (!isAdmin());
            }
        }
        for (PermissionAttachment attachment : getAttachments()) {
            if (attachment.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    public List<PermissionAttachment> getAttachments() {
        return PermissionAttachment.getAttachments(this);
    }
    
    public void addAttachment(PermissionAttachment attachment) {
        PermissionAttachment.addAttachment(attachment);
    }

    // TODO
    public boolean isAdmin() {
        return this.admin;
    }
    
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
