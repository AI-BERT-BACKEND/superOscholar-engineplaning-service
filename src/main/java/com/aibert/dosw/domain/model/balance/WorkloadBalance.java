package com.aibert.dosw.domain.model.balance;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents the full weekly workload balance.
 * Aggregates differential balances for each day of the week.
 */
@Getter
@Builder
public class WorkloadBalance {

    private final String userId;
    private final LocalDate weekStart;
    private final List<DifferentialBalance> dailyBalances;

    /**
     * Returns the overloaded days of the week.
     *
     * @return list of balances with OVERLOADED status
     */
    public List<DifferentialBalance> getOverloadedDays() {
        if (dailyBalances == null)
            return List.of();
        return dailyBalances.stream()
                .filter(DifferentialBalance::isOverloaded)
                .collect(Collectors.toList());
    }

    /**
     * Returns the days with free time.
     *
     * @return list of balances with FREE status
     */
    public List<DifferentialBalance> getFreeDays() {
        if (dailyBalances == null)
            return List.of();
        return dailyBalances.stream()
                .filter(DifferentialBalance::hasFreeTime)
                .collect(Collectors.toList());
    }

    /**
     * Checks whether the week has any overloaded day.
     *
     * @return true if at least one day is OVERLOADED
     */
    public boolean hasOverload() {
        return !getOverloadedDays().isEmpty();
    }

    /**
     * Calculates the total available hours in the week.
     *
     * @return sum of available hours across all days
     */
    public double getTotalAvailableHours() {
        if (dailyBalances == null)
            return 0.0;
        return dailyBalances.stream()
                .mapToDouble(DifferentialBalance::getAvailableHours)
                .sum();
    }

    /**
     * Calculates the total scheduled hours in the week.
     *
     * @return sum of scheduled hours across all days
     */
    public double getTotalScheduledHours() {
        if (dailyBalances == null)
            return 0.0;
        return dailyBalances.stream()
                .mapToDouble(DifferentialBalance::getScheduledHours)
                .sum();
    }

    /**
     * Finds the balance for a specific day.
     *
     * @param date target day
     * @return balance for the day or null if not found
     */
    public DifferentialBalance getBalanceForDay(LocalDate date) {
        if (dailyBalances == null)
            return null;
        return dailyBalances.stream()
                .filter(b -> b.getDate().equals(date))
                .findFirst()
                .orElse(null);
    }
}
