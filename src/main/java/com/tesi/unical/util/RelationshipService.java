package com.tesi.unical.util;

import org.springframework.stereotype.Service;
@Service
public class RelationshipService {

    private boolean isOneToOne(int left, int middle, int right) {
        return left == middle && middle == right;
    }

    private boolean isOneToMany(int left, int middle, int right) {
        return left > middle && middle == right;
    }

    private boolean isManyToOne(int left, int middle, int right) {
        return left == middle && middle < right;
    }

    public String getRelationshipType(int left, int middle, int right) {
        if(isOneToOne(left, middle, right))
            return "ONE_TO_ONE";
        else if(isOneToMany(left, middle, right))
            return "ONE_TO_MANY";
        else if(isManyToOne(left, middle, right))
            return "MANY_TO_ONE";
        else
            return "MANY_TO_MANY";
    }
}
