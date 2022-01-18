package io.github.aquerr.regionwars.command.team;

import io.github.aquerr.regionwars.RegionWarsPlugin;
import io.github.aquerr.regionwars.command.CommandException;
import io.github.aquerr.regionwars.model.Team;
import io.github.aquerr.regionwars.service.TeamService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class AddTeamMemberCommandTest
{
    private static final String TEAM_NAME = "TeamName";
    private static final String TEAM_NAME_2 = "TeamName2";
    private static final String PLAYER_NAME = "PlayerName";
    private static final String PLAYER_NAME_2 = "NextName";

    private static final ChatColor GREEN_TEAM_COLOR = ChatColor.GREEN;

    @Mock
    private Player player;

    @Mock
    private TeamService teamService;

    @Mock
    private CommandSender commandSender;

    @InjectMocks
    private AddTeamMemberCommand addTeamMemberCommand;

    @BeforeAll
    public static void setupBukkit()
    {
        Server server = Mockito.mock(Server.class);
        given(server.getLogger()).willReturn(Logger.getLogger(Server.class.getName()));

        Bukkit.setServer(server);
    }

    @BeforeEach
    public void setUp()
    {
        openMocks(this);
    }

    @Nested
    class ExecuteTests
    {
        @Test
        void executeThrowsCommandExceptionWhenArgumentCountIsNotEqualTo4()
        {
            Throwable throwable = catchThrowable(() -> addTeamMemberCommand.execute(commandSender, new String[]{}));
            assertThat(throwable).isInstanceOf(CommandException.class);
        }

        @Test
        void executeThrowsCommandExceptionWhenGivenPlayerIsNotFound()
        {
            given(Bukkit.getServer().getPlayer(PLAYER_NAME)).willReturn(null);

            Throwable throwable = catchThrowable(() -> addTeamMemberCommand.execute(commandSender, new String[] {"team", TEAM_NAME, "add_member", PLAYER_NAME}));
            assertThat(throwable).isInstanceOf(CommandException.class);
        }

        @Test
        void executeThrowsCommandExceptionWhenGivenTeamIsNotFound()
        {
            given(Bukkit.getServer().getPlayer(PLAYER_NAME)).willReturn(player);
            given(teamService.getTeam(TEAM_NAME)).willReturn(Optional.empty());

            Throwable throwable = catchThrowable(() -> addTeamMemberCommand.execute(commandSender, new String[] {"team", TEAM_NAME, "add_member", PLAYER_NAME}));
            assertThat(throwable).isInstanceOf(CommandException.class);
        }

        @Test
        void executeThrowsCommandExceptionWhenGivenPlayerIsAlreadyInATeam()
        {
            given(Bukkit.getServer().getPlayer(PLAYER_NAME)).willReturn(player);
            given(teamService.getTeam(TEAM_NAME)).willReturn(Optional.of(new Team(TEAM_NAME, GREEN_TEAM_COLOR)));
            given(teamService.getTeamForPlayer(player)).willReturn(Optional.of(new Team(TEAM_NAME_2, GREEN_TEAM_COLOR)));

            Throwable throwable = catchThrowable(() -> addTeamMemberCommand.execute(commandSender, new String[] {"team", TEAM_NAME, "add_member", PLAYER_NAME}));
            assertThat(throwable).isInstanceOf(CommandException.class);
        }

        @Test
        void executeAddsPlayerToTeamAndSavesTheTeam() throws CommandException
        {
            CommandSender.Spigot commandSenderSpigot = Mockito.mock(CommandSender.Spigot.class);

            String playerName = "Player";
            UUID playerUUID = UUID.randomUUID();
            Team team = new Team(TEAM_NAME, GREEN_TEAM_COLOR);
            given(player.getName()).willReturn(playerName);
            given(player.getUniqueId()).willReturn(playerUUID);
            given(commandSender.spigot()).willReturn(commandSenderSpigot);
            given(Bukkit.getServer().getPlayer(PLAYER_NAME)).willReturn(player);
            given(teamService.getTeam(TEAM_NAME)).willReturn(Optional.of(team));
            given(teamService.getTeamForPlayer(player)).willReturn(Optional.empty());

            addTeamMemberCommand.execute(commandSender, new String[] {"team", TEAM_NAME, "add_member", PLAYER_NAME});

            assertThat(team.getMembers()).contains(playerUUID);
            verify(teamService).saveTeam(team);
            verify(commandSender).spigot();
            verify(commandSenderSpigot).sendMessage(prepareMemberAddedMessage(player.getName(), team.getName()));
        }
    }

    @Nested
    class TabCompleteTests
    {
        @Test
        void tabCompleteReturnsOnlinePlayersThatNameStartWithGivenArgumentWhenArgumentCountIs4()
        {
            Collection<? extends Player> players = List.of(prepareMockPlayer(PLAYER_NAME), prepareMockPlayer(PLAYER_NAME_2));
            given(Bukkit.getServer().getOnlinePlayers()).willReturn((Collection)players);

            List<String> playerNames = addTeamMemberCommand.tabComplete(commandSender, new String[] {"team", TEAM_NAME, "add_member", "player"});

            assertThat(playerNames).hasSize(1);
            assertThat(playerNames.get(0)).isEqualTo(PLAYER_NAME);
        }

        @Test
        void tabCompleteReturnsOnlinePlayerListWhenArgumentsCountIsNot4()
        {
            Collection<? extends Player> players = List.of(prepareMockPlayer(PLAYER_NAME), prepareMockPlayer(PLAYER_NAME_2));
            given(Bukkit.getServer().getOnlinePlayers()).willReturn((Collection)players);

            List<String> playerNames = addTeamMemberCommand.tabComplete(commandSender, new String[] {"team", TEAM_NAME, "add_member"});

            assertThat(playerNames).hasSize(2);
            assertThat(playerNames.get(0)).isEqualTo(PLAYER_NAME);
            assertThat(playerNames.get(1)).isEqualTo(PLAYER_NAME_2);
        }
    }

    private BaseComponent[] prepareMemberAddedMessage(String playerName, String teamName)
    {
        return new ComponentBuilder()
                .append(RegionWarsPlugin.PLUGIN_PREFIX)
                .append("Player " + playerName + " has been added to the team " + teamName + "!")
                .color(ChatColor.GREEN)
                .create();
    }

    private Player prepareMockPlayer(String playerName)
    {
        Player player = Mockito.mock(Player.class);
        given(player.getName()).willReturn(playerName);
        return player;
    }
}