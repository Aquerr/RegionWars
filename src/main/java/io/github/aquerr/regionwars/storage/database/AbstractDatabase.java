package io.github.aquerr.regionwars.storage.database;

import io.github.aquerr.regionwars.RegionWarsPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractDatabase implements Database
{
    protected final RegionWarsPlugin plugin;

    protected AbstractDatabase(RegionWarsPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void init()
    {
        try
        {
            final int databaseVersionNumber = getDatabaseVersion();

            //Get all .sql files
            final List<Path> filePaths = getSqlFilesPaths();

            for(final Path resourceFilePath : filePaths)
            {
                final int scriptNumber = Integer.parseInt(resourceFilePath.getFileName().toString().substring(0, 3));
                if(scriptNumber <= databaseVersionNumber)
                    continue;

                readScriptFileAndExecute(resourceFilePath);
            }
        }
        catch (Exception exception)
        {
            throw new IllegalStateException(exception);
        }
    }

    private void readScriptFileAndExecute(final Path resourceFilePath)
    {
        try(final InputStream inputStream = Files.newInputStream(resourceFilePath, StandardOpenOption.READ);
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final Connection connection = getConnection();
            final Statement statement = connection.createStatement())
        {
            final StringBuilder stringBuilder = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null)
            {
                if(line.startsWith("--"))
                    continue;

                stringBuilder.append(line);

                if(line.endsWith(";"))
                {
                    statement.addBatch(stringBuilder.toString().trim());
                    stringBuilder.setLength(0);
                }
            }
            statement.executeBatch();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private List<Path> getSqlFilesPaths() throws URISyntaxException, IOException
    {
        final List<Path> filePaths = new ArrayList<>();
        final URL url = this.plugin.getClass().getResource("/assets/RegionWars/db/" + getStorageType().getName());
        if (url != null)
        {
            final URI uri = url.toURI();
            Path myPath;
            if (uri.getScheme().equals("jar"))
            {
                final FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                myPath = fileSystem.getPath("/assets/RegionWars/db/" + getStorageType().getName());
            }
            else
            {
                myPath = Paths.get(uri);
            }

            final Stream<Path> walk = Files.walk(myPath, 1);
            boolean skipFirst = true;
            for (final Iterator<Path> it = walk.iterator(); it.hasNext();) {
                if (skipFirst)
                {
                    it.next();
                    skipFirst = false;
                }

                final Path zipPath = it.next();
                filePaths.add(zipPath);
            }
        }

        //Sort .sql files
        filePaths.sort(Comparator.comparing(x -> x.getFileName().toString()));
        return filePaths;
    }

    protected abstract int getDatabaseVersion() throws SQLException;
}
