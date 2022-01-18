package io.github.aquerr.regionwars.command.team;

import io.github.aquerr.regionwars.RegionWarsPlugin;
import io.github.aquerr.regionwars.command.CommandException;
import io.github.aquerr.regionwars.model.Team;
import io.github.aquerr.regionwars.service.TeamService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class DeleteTeamCommandTest
{
    private static final String YOU_NEED_TO_PROVIDE_EXACTLY_ONE_TEAM_NAME = "You need to provide exactly one team name!";
    private static final String TEAM_WITH_SUCH_NAME_DOES_NOT_EXIST = "Team with such name does not exist!";

    private static final String TEAM_NAME = "teamname";
    private static final String TEAM_NAME_2 = "custom2";

    private static final ChatColor GREEN_TEAM_COLOR = ChatColor.GREEN;

    @Mock
    private TeamService teamService;

    @Mock
    private CommandSender commandSender;

    @InjectMocks
    private DeleteTeamCommand deleteTeamCommand;

    @BeforeEach
    public void setUp()
    {
        openMocks(this);
    }

    @Test
    void tabCompleteReturnTeamWhenArgumentIsTeamNamePrefix()
    {
        // given
        given(teamService.getTeams()).willReturn(List.of(prepareTeam()));

        // when
        final List<String> tabCompletions = deleteTeamCommand.tabComplete(commandSender, new String[]{"delete_team","te"});

        // then
        assertThat(tabCompletions).contains(TEAM_NAME);
    }

    @Test
    void tabCompleteDoesNotReturnAnyTeamIfPrefixDoesNotMatchAnyTeam()
    {
        // given
        given(teamService.getTeams()).willReturn(List.of(new Team(TEAM_NAME, GREEN_TEAM_COLOR), new Team(TEAM_NAME_2, GREEN_TEAM_COLOR)));

        // when
        final List<String> tabCompletions = deleteTeamCommand.tabComplete(commandSender, new String[]{"delete_team", "yolo"});

        // then
        assertThat(tabCompletions).isEmpty();
    }

    @Test
    void executeThrowsCommandExceptionWhenTooManyArguments()
    {
        // given
        // when
        Throwable throwable = catchThrowable(() -> deleteTeamCommand.execute(commandSender, new String[] {"delete_team", TEAM_NAME, TEAM_NAME_2}));

        // then
        assertThat(throwable).isInstanceOf(CommandException.class);
        assertThat(throwable.getMessage()).isEqualTo(YOU_NEED_TO_PROVIDE_EXACTLY_ONE_TEAM_NAME);
    }

    @Test
    void executeThrowsCommandExceptionWhenTeamWithGivenNameDoesNotExist()
    {
        // given
        // when
        Throwable throwable = catchThrowable(() -> deleteTeamCommand.execute(commandSender, new String[] {"delete_team", TEAM_NAME}));

        // then
        assertThat(throwable).isInstanceOf(CommandException.class);
        assertThat(throwable.getMessage()).isEqualTo(TEAM_WITH_SUCH_NAME_DOES_NOT_EXIST);
    }

    @Test
    void executeDeletesTeamAndNotifiesCommandSource() throws CommandException
    {
        // given
        CommandSender.Spigot commandSenderSpigot = Mockito.mock(CommandSender.Spigot.class);
        given(commandSender.spigot()).willReturn(commandSenderSpigot);
        given(teamService.getTeam(TEAM_NAME)).willReturn(Optional.of(prepareTeam()));

        // when
        deleteTeamCommand.execute(commandSender, new String[] {"delete_team", TEAM_NAME});

        // then
        verify(teamService).deleteTeam(TEAM_NAME);
        verify(commandSender).spigot();
        verify(commandSenderSpigot).sendMessage(prepareDeleteMessage());
    }

    private Team prepareTeam()
    {
        return new Team(TEAM_NAME, GREEN_TEAM_COLOR);
    }

    private BaseComponent[] prepareDeleteMessage()
    {
        return new ComponentBuilder()
                .append(RegionWarsPlugin.PLUGIN_PREFIX)
                .append("Team " + TEAM_NAME + " has been deleted!")
                .color(ChatColor.GREEN)
                .create();
    }
}