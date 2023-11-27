package com.tesi.unical.util.file;

import com.tesi.unical.entity.dto.ColumnMetaData;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ParallelJsonBuilder implements Runnable {

    private ResultSet resultSet;
    private ConcurrentHashMap<Long,List<JSONObject>> map;
    private Long id;

    private List<ColumnMetaData> columnMetaDataList;

    public ParallelJsonBuilder(Long id, ResultSet resultSet, ConcurrentHashMap<Long,List<JSONObject>> map, List<ColumnMetaData> columnMetaDataList) {
        this.id = id;
        this.resultSet = resultSet;
        this.map = map;
        this.columnMetaDataList = columnMetaDataList;
    }

    @Override
    public void run() {
        try {
            //JsonUtils jsonUtils = new JsonUtils(resultSet,columnMetaDataList);
            //List<JSONObject> jsonUtilsList = jsonUtils.fillJsonListByColumnName(resultSet,columnMetaDataList);
            //map.put(id,jsonUtilsList);
        } catch (Exception e) {
            return;
        }
    }
}
