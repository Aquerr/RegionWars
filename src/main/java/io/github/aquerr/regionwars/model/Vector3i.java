package io.github.aquerr.regionwars.model;

import java.util.List;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public final class Vector3i
{
    private static final Pattern VECTOR3I_PATTERN = Pattern.compile("-*\\d*");

    private final int x;
    private final int y;
    private final int z;

    public Vector3i(final int x, final int y, final int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    /**
     * Converts string vector3i to instance of Vector3i.
     *
     * Expected string format is: "(x, y, z)"
     * @param vector3i as string
     * @return instance of Vector3i
     */
    public static Vector3i from(String vector3i)
    {
        List<String> positions = VECTOR3I_PATTERN.matcher(vector3i).results()
                .map(MatchResult::group)
                .toList();
        int x = Integer.parseInt(positions.get(0));
        int y = Integer.parseInt(positions.get(1));
        int z = Integer.parseInt(positions.get(2));
        return new Vector3i(x, y, z);
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3i vector3i = (Vector3i) o;
        return x == vector3i.x && y == vector3i.y && z == vector3i.z;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(x, y, z);
    }
}
