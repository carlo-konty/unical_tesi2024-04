package com.tesi.unical.util.file;

import java.io.File;
import java.sql.ResultSet;
import java.util.List;

public class ParallelJsonBuilder implements Runnable {

    private ResultSet resultSet;

    public ParallelJsonBuilder(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public void run() {
        try {
            List<Object> objectList = JsonUtils.objectList(resultSet);

        } catch (Exception e) {

        }
    }
}
