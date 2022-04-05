package com.example.geektrust.model;

import java.util.HashMap;
import java.util.Map;

public class Member {
    private final String name;
    private final Map<Member, Integer> ledger;

    public Member(String name) {
        this.name = name;
        this.ledger = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<Member, Integer> getLedger() {
        return ledger;
    }

    public boolean hasNoTransactions() {
        for (Map.Entry<Member, Integer> entry : ledger.entrySet()) {
            if (entry.getValue() != 0) {
                return false;
            }
        }
        return true;
    }
}
