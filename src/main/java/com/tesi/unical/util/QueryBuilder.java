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
        sb.insert(7,"COUNT(");
        sb.insert(15,")");
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

    public static String limit(String query, int limit, int offset) {
        StringBuilder sb = new StringBuilder(query);
        sb.append(" LIMIT " + limit);
        sb.append(" OFFSET " + offset);
        log.info("limit: {}",sb);
        return sb.toString();
    }

    public static String where(String query) {
        StringBuilder sb = new StringBuilder(query);
        sb.append(" WHERE 1=1").toString();
        log.info(sb.toString());
        return sb.toString();
    }

    public static String and(String query, String column, String value) {
        StringBuilder sb = new StringBuilder(query);
        sb.append(" AND " ).append(column).append(" = ").append('\'' + value + '\'').toString();
        log.info(sb.toString());
        return sb.toString();
    }

    public static String orderByDesc(String query, String column) {
        StringBuilder sb = new StringBuilder(query);
        sb.append(" ORDER BY ").append(column).append(" DESC");
        return sb.toString();
    }

}
