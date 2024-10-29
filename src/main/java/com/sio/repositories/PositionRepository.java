package com.sio.repositories;

import com.sio.models.Position;
import com.sio.tools.DataSourceProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PositionRepository {
    private DataSource dataSource;
    private Connection connection;

    public PositionRepository() {
        this.dataSource = DataSourceProvider.getDataSourceInstance();
    }


    public void create(Position p) {
        try {
            this.connection = this.dataSource.getConnection();
            PreparedStatement pSt = this.connection.prepareStatement("INSERT INTO position (targetHash, latitude, longitude, timestamp) VALUES (?, ?, ?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pSt.setString(1, p.getTarget().getHash());
            pSt.setFloat(2, p.getLatitude());
            pSt.setFloat(3, p.getLongitude());
            pSt.setTimestamp(4, p.getTimestamp());
            pSt.executeUpdate();
            ResultSet rs =  pSt.getGeneratedKeys();
            if(rs.next()){
                p.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                if (this.connection != null) {
                    this.connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Find all positions by target hash
     * @param hash String
     * @return ArrayList<Position>
     */
    public ArrayList<Position> findByTargetHash(String hash) {
        ArrayList<Position> positions = new ArrayList<>();
        try {
            this.connection = this.dataSource.getConnection();
            PreparedStatement pSt = this.connection.prepareStatement("SELECT id, latitude, longitude, timestamp FROM position WHERE targetHash = ?");
            pSt.setString(1, hash);
            ResultSet rs = pSt.executeQuery();
            while (rs.next()) {
                positions.add(new Position(
                        rs.getInt("id"),
                        null,
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude"),
                        rs.getTimestamp("timestamp")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                if (this.connection != null) {
                    this.connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        return positions;
    }

    /**
     * Delete all positions by target hash
     * @param hash String
     *
     */
    public void deleteByTargetHash(String hash) {
        try {
            this.connection = this.dataSource.getConnection();
            PreparedStatement pSt = this.connection.prepareStatement("DELETE FROM position WHERE targetHash = ?");
            pSt.setString(1, hash);
            pSt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                if (this.connection != null) {
                    this.connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }


    public int countPositionsByTargetHash(String hash) {
        int count = 0;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pSt = connection.prepareStatement("SELECT COUNT(*) FROM position WHERE targetHash = ?")) {
            pSt.setString(1, hash);
            ResultSet rs = pSt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return count;
    }

}
