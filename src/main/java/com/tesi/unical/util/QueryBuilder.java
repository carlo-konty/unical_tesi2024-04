package com.tesi.unical.util;

import com.tesi.unical.entity.dto.ColumnMetaData;
import com.tesi.unical.entity.dto.MetaDataDTO;

import java.util.List;

public class QueryBuilder {

    public static String ciao() {
        return "ciao";
    }

    public static String selectAll(String schema, String table, List<ColumnMetaData> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        for(int i=0; i<columns.size(); i++) {
            sb.append(columns.get(i).getColumnName());
            if(i< columns.size()-1)
                sb.append(",\n");
            else
                sb.append("\n");
        }
        sb.append("FROM ");
        sb.append(schema + "." + table + ";");
        return sb.toString();
    }

    //costruisce la join in base alla chiave esterna che ricavo dalle query sulle viste di sistema (ipotesi le chiavi non sono composte)
    public static String join(String schema, String table1, String table2, MetaDataDTO dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(schema + "." + table1 + " t0 ");
        sb.append("JOIN ");
        sb.append(schema + "." + table2 + " t1 ");
        sb.append("ON ");
        sb.append("t0." + dto.getReferencedColumnName());
        sb.append(" = ");
        sb.append("t1." + dto.getFkColumnName() + ";");
        return sb.toString();
    }
}
