package com.example.speedometerapp.enums;

import static com.example.speedometerapp.enums.SpeedViolationColumn.TIMESTAMP;

import com.example.speedometerapp.R;

public enum SelectableSearchOption {
    TODAY(R.string.select_today, String.format(" WHERE date(%s) = date('now')", TIMESTAMP.getColName())),
    LAST_WEEK(R.string.select_last_week, String.format(" WHERE date(%s) BETWEEN date('now', '-7 days') AND date('now')", TIMESTAMP.getColName())),
    ALL(R.string.select_all, "");

    private int resourceId;
    private String name, sqlWhere;

    SelectableSearchOption(int resourceId, String sqlWhere) {
        this.resourceId = resourceId;
        this.sqlWhere = sqlWhere;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSqlWhere() {
        return sqlWhere;
    }

    @Override
    public String toString() {
        return name;
    }
}
