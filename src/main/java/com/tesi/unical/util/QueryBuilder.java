package com.tesi.unical.util;

import com.tesi.unical.entity.dto.MetaDataDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryBuilder {

    public static String selectAll(String schema, String table) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * ");
        sb.append("FROM ");
        sb.append(schema + "." + table);
        return sb.toString();
    }

    public static String countAll(String schema, String table) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) FROM ");
        sb.append(schema + "." + table);
        return sb.toString();
    }

    public static String count(String query) {
        StringBuilder sb = new StringBuilder(query);
        sb.insert(6,"COUNT(");
        sb.insert(15,") ");
        log.info("count: {}",sb);
        return sb.toString();
    }

    //costruisce la join in base alla chiave esterna che ricavo dalle query sulle viste di sistema (ipotesi le chiavi non sono composte)
    public static String join2Tables(String schema, String table, MetaDataDTO dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(schema + "." + table + " t0 ");
        sb.append("JOIN ");
        sb.append(schema + "." + dto.getFkTableName() + " t1 ");
        sb.append("ON ");
        sb.append("t0." + dto.getReferencedColumnName());
        sb.append(" = ");
        sb.append("t1." + dto.getFkColumnName());
        return sb.toString();
    }

    public static String fetchNRowsOnly(String query, Integer nrows) {
        StringBuilder sb = new StringBuilder(query);
        sb.append(" FETCH FIRST " + nrows + " ROWS ONLY");
        return sb.toString();
    }

    public static String limit(String query, String limit, String offset) {
        StringBuilder sb = new StringBuilder(query);
        sb.append(" LIMIT " + limit);
        sb.append(" OFFSET " + offset);
        return sb.toString();
    }
}
