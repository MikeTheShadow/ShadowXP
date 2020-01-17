package com.MikeTheShadow.ShadowXP.Util;

import com.MikeTheShadow.ShadowXP.ShadowXP;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHandler
{
    public static void createDatabase()
    {
        File db = new File(ShadowXP.getPlugin(ShadowXP.class).getDataFolder() + "\\" + ShadowXP.DBName);
        if(db.exists())
        {
            Bukkit.getConsoleSender().sendMessage("DB exists ignoring...");
            return;
        }
        String url = "jdbc:sqlite:" + ShadowXP.getPlugin(ShadowXP.class).getDataFolder() + "\\" + ShadowXP.DBName;

        try (Connection conn = DriverManager.getConnection(url))
        {
            if (conn != null)
            {
                DatabaseMetaData meta = conn.getMetaData();
                Bukkit.getConsoleSender().sendMessage("Driver name: " + meta.getDriverName());
                Bukkit.getConsoleSender().sendMessage("Database creation successful!");
            }

        }
        catch (SQLException e)
        {
            Bukkit.getConsoleSender().sendMessage(e.getMessage());
        }
    }
    public static Connection connect()
    {
        Connection conn = null;
        try
        {
            // db parameters
            String url = "jdbc:sqlite:" + ShadowXP.getPlugin(ShadowXP.class).getDataFolder() + "\\" + ShadowXP.DBName;
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            return conn;
        }
        catch (SQLException e)
        {
            Bukkit.getConsoleSender().sendMessage(e.getMessage());
        }
        return null;
    }

    public static void createUserTable()
    {
        // SQLite connection string
        String url = "jdbc:sqlite:" + ShadowXP.getPlugin(ShadowXP.class).getDataFolder() + "\\" + ShadowXP.DBName;

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS Users (\n"
                + "    name text NOT NULL,\n"
                + "    UID text NOT NULL,\n"
                + "    level integer NOT NULL,\n"
                + "    currentXP integer NOT NULL,\n"
                + "    totalXP integer NOT NULL\n"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement())
        {
            // create a new table
            stmt.execute(sql);
        }
        catch (SQLException e)
        {
            Bukkit.getConsoleSender().sendMessage(e.getMessage());
        }
    }
    public static void insertNewUser(String name, String UID, int level, int currentXP, int totalXP)
    {
        String sql = "INSERT INTO Users(name,UID,level,currentXP,totalXP) VALUES(?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, name);
            pstmt.setString(2, UID);
            pstmt.setInt(3, level);
            pstmt.setInt(4, currentXP);
            pstmt.setInt(5, totalXP);
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            Bukkit.getConsoleSender().sendMessage("Error! " + e.getSQLState());
        }
    }

    public static CustomUser getUserByID(String UUID)
    {
        String sql = "SELECT name,UID,level,currentXP,totalXP "
                + "FROM Users WHERE UID = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql))
        {

            // set the value
            pstmt.setString(1,UUID);
            //
            ResultSet rs  = pstmt.executeQuery();
            // loop through the result set
            while (rs.next())
            {
                return new CustomUser(rs.getString("name"),rs.getString("UID"),rs.getInt("level"),rs.getInt("currentXP"),rs.getInt("totalXP"));
            }
        }
        catch (SQLException e)
        {
            Bukkit.getConsoleSender().sendMessage("Error! " + e.getMessage());
        }
        return null;
    }
    public static void updateCustomUser(CustomUser user)
    {
        String sql = "UPDATE Users SET level = ? , "
                + "currentXP = ? ,"
                + "totalXP = ? "
                + "WHERE UID = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            // set the corresponding param
            pstmt.setInt(1, user.level);
            pstmt.setInt(2, user.currentXP);
            pstmt.setInt(3, user.totalXP);
            pstmt.setString(4, user.UID);
            // update
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            Bukkit.getConsoleSender().sendMessage("Error! " + e.getMessage());
        }
    }
    public static List<CustomUser> getAllUsers()
    {
        String sql = "SELECT name,UID,level,currentXP,totalXP "
                + "FROM Users";

        try (Connection conn = connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql))
        {
            ResultSet rs  = pstmt.executeQuery();
            // loop through the result set
            List<CustomUser> returnList = new ArrayList<>();
            while (rs.next())
            {
                 returnList.add(new CustomUser(rs.getString("name"),rs.getString("UID"),rs.getInt("level"),rs.getInt("currentXP"),rs.getInt("totalXP")));
            }
            return returnList;
        }
        catch (SQLException e)
        {
            Bukkit.getConsoleSender().sendMessage("Error! " + e.getMessage());
        }
        return null;
    }
}
