package com.financeos.service;

import com.financeos.dto.DashboardDTOs.*;
import com.financeos.entity.Transaction.TransactionType;
import com.financeos.entity.User;
import com.financeos.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public DashboardSummary getSummary(User user, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().withDayOfMonth(1);
        if (endDate == null)   endDate   = LocalDate.now();

        var totalIncome   = transactionRepository.sumByUserAndTypeAndDateBetween(user.getId(), TransactionType.INCOME,  startDate, endDate);
        var totalExpenses = transactionRepository.sumByUserAndTypeAndDateBetween(user.getId(), TransactionType.EXPENSE, startDate, endDate);
        var balance       = totalIncome.subtract(totalExpenses);

        BigDecimal savingsRate = BigDecimal.ZERO;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = balance.divide(totalIncome, 4, RoundingMode.HALF_UP)
                               .multiply(BigDecimal.valueOf(100))
                               .setScale(2, RoundingMode.HALF_UP);
        }

        return DashboardSummary.builder()
            .totalIncome(totalIncome)
            .totalExpenses(totalExpenses)
            .balance(balance)
            .savingsRate(savingsRate)
            .monthlyData(buildMonthlyData(user))
            .expensesByCategory(buildCategoryBreakdown(user, TransactionType.EXPENSE, startDate, endDate))
            .incomeByCategory(buildCategoryBreakdown(user, TransactionType.INCOME, startDate, endDate))
            .recentTransactions(transactionService.getRecentTransactions(user))
            .build();
    }

    private List<MonthlyData> buildMonthlyData(User user) {
        List<MonthlyData> result = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            var yearMonth = YearMonth.now().minusMonths(i);
            var start = yearMonth.atDay(1);
            var end   = yearMonth.atEndOfMonth();

            var income   = transactionRepository.sumByUserAndTypeAndDateBetween(user.getId(), TransactionType.INCOME,  start, end);
            var expenses = transactionRepository.sumByUserAndTypeAndDateBetween(user.getId(), TransactionType.EXPENSE, start, end);

            result.add(MonthlyData.builder()
                .month(yearMonth.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR")))
                .income(income)
                .expenses(expenses)
                .balance(income.subtract(expenses))
                .build());
        }
        return result;
    }

    private List<CategoryBreakdown> buildCategoryBreakdown(User user, TransactionType type, LocalDate start, LocalDate end) {
        var transactions = transactionRepository.findByUserAndDateRange(user.getId(), start, end)
            .stream().filter(t -> t.getType() == type).toList();

        var total = transactions.stream()
            .map(t -> t.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) == 0) return List.of();

        return transactions.stream()
            .filter(t -> t.getCategory() != null)
            .collect(Collectors.groupingBy(
                t -> t.getCategory().getId(),
                Collectors.reducing(BigDecimal.ZERO, t -> t.getAmount(), BigDecimal::add)
            ))
            .entrySet().stream()
            .map(e -> {
                var tx = transactions.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(e.getKey()))
                    .findFirst().get();
                var cat = tx.getCategory();
                double pct = e.getValue().divide(total, 4, RoundingMode.HALF_UP)
                              .multiply(BigDecimal.valueOf(100))
                              .doubleValue();
                return CategoryBreakdown.builder()
                    .categoryId(cat.getId())
                    .categoryName(cat.getName())
                    .color(cat.getColor())
                    .icon(cat.getIcon())
                    .total(e.getValue())
                    .percentage(pct)
                    .build();
            })
            .sorted(Comparator.comparing(CategoryBreakdown::getTotal).reversed())
            .toList();
    }
}
