package org.glydar.glydar;

import org.glydar.api.Server;
import org.glydar.api.permissions.Permission;
import org.glydar.api.permissions.Permission.PermissionDefault;
import org.glydar.glydar.models.GPlayer;
import org.glydar.glydar.netty.packet.shared.Packet10Chat;
import org.glydar.glydar.util.LogFormatter;

import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class GServer implements Runnable, Server {

	private final Logger LOGGER = Logger.getLogger(Glydar.class.getName());

    private boolean running = true;

    public final boolean DEBUG;

    public GServer(boolean debug) {
        this.DEBUG = debug;
	    LOGGER.setUseParentHandlers(false);
	    LogFormatter format = new LogFormatter();
	    ConsoleHandler console = new ConsoleHandler();
	    console.setFormatter(format);
	    LOGGER.addHandler(console);
    }

    public Collection<GPlayer> getConnectedPlayers() {
        return GPlayer.getConnectedPlayers();
    }

	public Logger getLogger() {
		return LOGGER;
	}

	public boolean isRunning() {
		return running;
	}

    @Override
    public void run() {
        while (this.isRunning()) {
            try {
            /* TODO Server loop / tick code.
               Eventually; All periodic events will be processed here, such as AI logic, etc for entities.
             */
                Thread.sleep(1); //To check shutdown
            } catch (InterruptedException ex) { break; }
        }
        getLogger().info("Goodbye!");
    }

    public void shutdown() {
        this.running = false;
    }
    
    public void broadcastMessage(String message) {
    	new Packet10Chat(message, 0).sendToAll();
    }
    
    public void broadcast(String message, String permission) {
        broadcast(message, new Permission(permission, PermissionDefault.TRUE));
    }

    public void broadcast(String message, Permission permission) {
        for (GPlayer player : this.getConnectedPlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessageToPlayer(message);
            }
        }
    }

}
