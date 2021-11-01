package io.github.aquerr.regionwars.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.mockito.Mockito;

import java.util.logging.Logger;

import static org.mockito.BDDMockito.given;

public class TestBukkit
{
    private static Server SERVER;

    static {
        SERVER = Mockito.mock(Server.class);
        given(SERVER.getLogger()).willReturn(Logger.getLogger(Server.class.getName()));

        Bukkit.setServer(SERVER);
    }

    public static Server getServer()
    {
        return SERVER;
    }
}
