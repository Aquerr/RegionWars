package io.github.aquerr.regionwars.command.team;

import io.github.aquerr.regionwars.RegionWarsPlugin;
import io.github.aquerr.regionwars.bukkit.TestBukkit;
import io.github.aquerr.regionwars.command.CommandException;
import io.github.aquerr.regionwars.model.Team;
import io.github.aquerr.regionwars.service.TeamService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class RemoveTeamMemberCommandTest
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
    private RemoveTeamMemberCommand removeTeamMemberCommand;

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
            Throwable throwable = catchThrowable(() -> removeTeamMemberCommand.execute(commandSender, new String[]{}));
            assertThat(throwable).isInstanceOf(CommandException.class);
        }

        @Test
        void executeThrowsCommandExceptionWhenGivenPlayerIsNotFound()
        {
            given(TestBukkit.getServer().getPlayer(PLAYER_NAME)).willReturn(null);

            Throwable throwable = catchThrowable(() -> removeTeamMemberCommand.execute(commandSender, new String[] {"team", TEAM_NAME, "add_member", PLAYER_NAME}));
            assertThat(throwable).isInstanceOf(CommandException.class);
        }

        @Test
        void executeThrowsCommandExceptionWhenGivenTeamIsNotFound()
        {
            given(TestBukkit.getServer().getPlayer(PLAYER_NAME)).willReturn(player);
            given(teamService.getTeam(TEAM_NAME)).willReturn(Optional.empty());

            Throwable throwable = catchThrowable(() -> removeTeamMemberCommand.execute(commandSender, new String[] {"team", TEAM_NAME, "add_member", PLAYER_NAME}));
            assertThat(throwable).isInstanceOf(CommandException.class);
        }

        @Test
        void executeThrowsCommandExceptionWhenGivenPlayerIsNotInATeam()
        {
            given(TestBukkit.getServer().getPlayer(PLAYER_NAME)).willReturn(player);
            given(teamService.getTeam(TEAM_NAME)).willReturn(Optional.of(new Team(TEAM_NAME, GREEN_TEAM_COLOR)));
            given(teamService.getTeamForPlayer(player)).willReturn(Optional.empty());

            Throwable throwable = catchThrowable(() -> removeTeamMemberCommand.execute(commandSender, new String[] {"team", TEAM_NAME, "add_member", PLAYER_NAME}));
            assertThat(throwable).isInstanceOf(CommandException.class);
        }

        @Test
        void executeRemovesPlayerFromTeamAndSavesTheTeam() throws CommandException
        {
            CommandSender.Spigot commandSenderSpigot = Mockito.mock(CommandSender.Spigot.class);

            String playerName = "Player";
            UUID playerUUID = UUID.randomUUID();
            Team team = new Team(TEAM_NAME, GREEN_TEAM_COLOR);
            given(player.getName()).willReturn(playerName);
            given(player.getUniqueId()).willReturn(playerUUID);
            given(commandSender.spigot()).willReturn(commandSenderSpigot);
            given(TestBukkit.getServer().getPlayer(PLAYER_NAME)).willReturn(player);
            given(teamService.getTeam(TEAM_NAME)).willReturn(Optional.of(team));
            given(teamService.getTeamForPlayer(player)).willReturn(Optional.of(team));

            removeTeamMemberCommand.execute(commandSender, new String[] {"team", TEAM_NAME, "remove_member", PLAYER_NAME});

            assertThat(team.getMembers()).doesNotContain(playerUUID);
            verify(teamService).saveTeam(team);
            verify(commandSender).spigot();
            verify(commandSenderSpigot).sendMessage(prepareMemberRemovedMessage(player.getName(), team.getName()));
        }
    }

    @Nested
    class TabCompleteTests
    {
        @Test
        void tabCompleteReturnsPlayersFromTeamThatNameStartWithGivenArgumentWhenArgumentCountIs4()
        {
            Team team = new Team(TEAM_NAME, GREEN_TEAM_COLOR);
            UUID playerUUID = UUID.randomUUID();
            team.addMember(playerUUID);
            given(teamService.getTeam(TEAM_NAME)).willReturn(Optional.of(team));
            OfflinePlayer teamPlayer = prepareMockOfflinePlayer(PLAYER_NAME);
            given(TestBukkit.getServer().getOfflinePlayer(playerUUID)).willReturn(teamPlayer);

            List<String> playerNames = removeTeamMemberCommand.tabComplete(commandSender, new String[] {"team", TEAM_NAME, "remove_member", "player"});

            assertThat(playerNames).hasSize(1);
            assertThat(playerNames.get(0)).isEqualTo(PLAYER_NAME);
        }

        @Test
        void tabCompleteReturnsOnlinePlayerListWhenArgumentsCountIsNot4()
        {
            Collection<? extends Player> players = List.of(prepareMockPlayer(PLAYER_NAME), prepareMockPlayer(PLAYER_NAME_2));
            given(TestBukkit.getServer().getOnlinePlayers()).willReturn((Collection)players);

            List<String> playerNames = removeTeamMemberCommand.tabComplete(commandSender, new String[] {"team", TEAM_NAME, "add_member"});

            assertThat(playerNames).hasSize(2);
            assertThat(playerNames.get(0)).isEqualTo(PLAYER_NAME);
            assertThat(playerNames.get(1)).isEqualTo(PLAYER_NAME_2);
        }
    }

    private BaseComponent[] prepareMemberRemovedMessage(String playerName, String teamName)
    {
        return new ComponentBuilder()
                .append(RegionWarsPlugin.PLUGIN_PREFIX)
                .append("Player " + playerName + " has been removed from the team " + teamName + "!")
                .color(ChatColor.GREEN)
                .create();
    }

    private OfflinePlayer prepareMockOfflinePlayer(String playerName)
    {
        OfflinePlayer offlinePlayer = Mockito.mock(OfflinePlayer.class);
        given(offlinePlayer.getName()).willReturn(playerName);
        return offlinePlayer;
    }

    private Player prepareMockPlayer(String playerName)
    {
        Player player = Mockito.mock(Player.class);
        given(player.getName()).willReturn(playerName);
        return player;
    }
}