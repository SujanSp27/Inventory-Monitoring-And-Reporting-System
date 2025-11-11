package com.inventory.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ProductDAOHelper {
    /**
     * Safely gets threshold value from ResultSet, returns default if column doesn't exist
     */
    public static int getThreshold(ResultSet rs, int defaultThreshold) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Check if threshold column exists
            boolean hasThreshold = false;
            for (int i = 1; i <= columnCount; i++) {
                if ("threshold".equalsIgnoreCase(metaData.getColumnName(i))) {
                    hasThreshold = true;
                    break;
                }
            }
            
            if (hasThreshold) {
                int threshold = rs.getInt("threshold");
                return rs.wasNull() ? defaultThreshold : threshold;
            }
        } catch (SQLException e) {
            // Column doesn't exist or other error, return default
        }
        return defaultThreshold;
    }
}

