package com.example.speedometerapp.enums;

public enum SpeedViolationColumn {
    ID("id"),
    LONGITUDE("longitude"),
    LATITUDE("latitude"),
    SPEED("speed"),
    SPEED_LIMIT("speed_limit"),
    TIMESTAMP("timestamp");

    private String colName;
    private SpeedViolationColumn(String colName) {
        this.colName = colName;
    }

    public String getColName() {
        return colName;
    }

    public int getColIndex() {
        return this.ordinal();
    }
}
