package org.glydar.glydar;

import org.glydar.glydar.models.Player;
import org.glydar.glydar.netty.packet.shared.Packet10Chat;
import org.glydar.glydar.util.LogFormatter;

import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import org.glydar.glydar.permissions.Permission;
import org.glydar.glydar.permissions.Permission.PermissionDefault;

public class Server implements Runnable {

	private final Logger LOGGER = Logger.getLogger(Glydar.class.getName());

    private boolean running = true;

    public final boolean DEBUG;

    public Server(boolean debug) {
        this.DEBUG = debug;
	    LOGGER.setUseParentHandlers(false);
	    LogFormatter format = new LogFormatter();
	    ConsoleHandler console = new ConsoleHandler();
	    console.setFormatter(format);
	    LOGGER.addHandler(console);
    }

    public Collection<Player> getConnectedPlayers() {
        return Player.getConnectedPlayers();
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
        for (Player player : this.getConnectedPlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessageToPlayer(message);
            }
        }
    }

}
