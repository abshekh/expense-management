package com.example.geektrust.service;

import com.example.geektrust.model.Member;
import com.example.geektrust.utils.ExpenseUtils;

import java.util.HashMap;
import java.util.Map;

import static com.example.geektrust.constants.Constants.*;

public class MemberService {
    private final Map<String, Member> members;
    private final int maxSize;

    public MemberService(int maxSize) {
        this.members = new HashMap<>();
        this.maxSize = maxSize;
    }

    public void processCommands(String command) {
        String[] args = command.split(" ");

        switch (args[0]) {
            case MOVE_IN:
                moveIn(args[1]);
                break;
            case SPEND:
                addExpenses(args);
                break;
            case DUES:
                printDues(args[1]);
                break;
            case CLEAR_DUE:
                clearDues(args[1], args[2], args[3]);
                break;
            case MOVE_OUT:
                moveOut(args[1]);
                break;
            default:
                break;
        }
    }

    private void moveIn(String memberName) {
        if (members.size() == this.maxSize) {
            System.out.println(HOUSEFUL);
        } else if (members.containsKey(memberName)) {
            System.out.println(FAILURE);
        } else {
            Member newMember = new Member(memberName);
            members.forEach((key, value) -> {
                value.getLedger().put(newMember, 0);
                newMember.getLedger().put(value, 0);
            });
            members.put(memberName, newMember);
            System.out.println(SUCCESS);
        }
    }

    private void addExpenses(String[] args) {
        if (memberNotFound(args[2])) return;

        if (args.length == 3) {
            System.out.println(FAILURE);
            return;
        }

        int amount = Integer.parseInt(args[1]);
        int amountDistributed = amount / (args.length - 2);

        for (int i = 3; i < args.length; i++) {
            if (memberNotFound(args[i])) return;
        }

        Member memberSpending = members.get(args[2]);

        for (int i = 3; i < args.length; i++) {
            Member member = members.get(args[i]);
            memberSpending.getLedger().compute(member, (k, v) -> v + amountDistributed);
            member.getLedger().compute(memberSpending, (k, v) -> v - amountDistributed);
        }

        members.values().forEach(this::adjustLedger);

        System.out.println(SUCCESS);
    }

    private void adjustLedger(Member memberSpending) {
        Map<Member, Integer> ledger = memberSpending.getLedger();
        Member minMember = ExpenseUtils.getMinMember(memberSpending);
        Member maxMember = ExpenseUtils.getMaxMember(memberSpending);
        final int minVal = ledger.get(minMember);
        final int maxVal = ledger.get(maxMember);

        if (minVal >= 0 || maxVal <= 0) {
            return;
        }

        if (-1 * minVal <= maxVal) {
            ledger.put(minMember, 0);
            minMember.getLedger().put(memberSpending, 0);

            ledger.put(maxMember, minVal + maxVal);
            maxMember.getLedger().put(memberSpending, -1 * ledger.get(maxMember));

            minMember.getLedger().compute(maxMember, (k, v) -> v - minVal);
            maxMember.getLedger().put(minMember, -1 * minMember.getLedger().get(maxMember));
        } else {
            ledger.put(minMember, minVal + maxVal);
            minMember.getLedger().put(memberSpending, -1 * ledger.get(minMember));

            ledger.put(maxMember, 0);
            maxMember.getLedger().put(memberSpending, 0);

            minMember.getLedger().compute(maxMember, (k, v) -> v + maxVal);
            maxMember.getLedger().put(minMember, -1 * minMember.getLedger().get(maxMember));
        }

        adjustLedger(memberSpending);
    }

    private void printDues(String memberName) {
        if (memberNotFound(memberName)) return;

        members.get(memberName)
                .getLedger().entrySet().stream()
                .sorted(ExpenseUtils.getDuesComparator())
                .forEach(member -> System.out.printf("%s %s%n", member.getKey().getName(),
                        (member.getValue() < 0 ? member.getValue() * -1 : 0)));
    }

    private void clearDues(String memberWhoOwes, String memberWhoLent, String amountString) {
        if (memberNotFound(memberWhoOwes) || memberNotFound(memberWhoLent)) return;

        int amount = Integer.parseInt(amountString);
        Member owedMember = members.get(memberWhoOwes);
        Member lentMember = members.get(memberWhoLent);


        if (amount <= lentMember.getLedger().get(owedMember)) {
            int remainingAmount = lentMember.getLedger().get(owedMember) - amount;
            lentMember.getLedger().put(owedMember, remainingAmount);
            owedMember.getLedger().put(lentMember, -1 * remainingAmount);
            System.out.println(remainingAmount);
        } else {
            System.out.println(INCORRECT_PAYMENT);
        }
    }

    private void moveOut(String memberName) {
        if (memberNotFound(memberName)) return;

        Member member = members.get(memberName);
        if (member.hasNoTransactions()) {
            members.remove(memberName);
            members.forEach((k, v) -> v.getLedger().remove(member));
            System.out.println(SUCCESS);
        } else {
            System.out.println(FAILURE);
        }
    }

    private boolean memberNotFound(String memberName) {
        if (!members.containsKey(memberName)) {
            System.out.println(MEMBER_NOT_FOUND);
            return true;
        }
        return false;
    }
}
