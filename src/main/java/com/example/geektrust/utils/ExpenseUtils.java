package com.example.geektrust.utils;

import com.example.geektrust.model.Member;

import java.util.Comparator;
import java.util.Map;

public class ExpenseUtils {
    private ExpenseUtils() {
    }

    public static Comparator<Map.Entry<Member, Integer>> getDuesComparator() {
        return (a, b) -> {
            final int aVal = a.getValue();
            final int bVal = b.getValue();
            if ((aVal == bVal) || (aVal >= 0 && bVal >= 0)) {
                return a.getKey().getName().compareTo(b.getKey().getName());
            } else {
                return Integer.compare(aVal, bVal);
            }
        };
    }


    public static Member getMinMember(Member member) {
        return member.getLedger().keySet().stream().min(Comparator.comparingInt(a -> member.getLedger().get(a))).get();
    }

    public static Member getMaxMember(Member member) {
        return member.getLedger().keySet().stream().max(Comparator.comparingInt(a -> member.getLedger().get(a))).get();
    }



}
