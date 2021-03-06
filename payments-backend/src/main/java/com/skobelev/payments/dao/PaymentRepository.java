package com.skobelev.payments.dao;

import com.skobelev.payments.dto.Payment;
import com.skobelev.payments.exceptions.SummarizeException;
import com.skobelev.payments.exceptions.TransferPaymentsException;
import com.skobelev.payments.model.UserBillAggregate;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
public class PaymentRepository {

    private static final String INSERT_QUERY = "insert into payments(user_from, user_to, money) values (?, ?, ?);";
    private static final String AGGREGATE_QUERY = "select sum(money) from payments where user_from = ?;";
    private final List<DataSource> dataSources;
    private final AtomicInteger counter = new AtomicInteger();
    private final int connectionCount;

    public PaymentRepository(@NotNull final List<DataSource> dataSources) {
        this.dataSources = dataSources;
        this.connectionCount = dataSources.size();
    }

    public void add(@NotNull final List<Payment> payments) {
        Map<Integer, List<Payment>> paymentMap =
                payments.stream().collect(groupingBy(payment -> counter.getAndIncrement() % connectionCount + 1));
        for (Integer partition: paymentMap.keySet()) {
            DataSource dataSource = dataSources.get(partition - 1);
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY);
                paymentMap.get(partition).forEach(payment -> {
                    try {
                        preparedStatement.setString(1, payment.getFrom());
                        preparedStatement.setString(2, payment.getTo());
                        preparedStatement.setBigDecimal(3, payment.getMoney());
                        preparedStatement.addBatch();
                    } catch (SQLException ex) {
                        log.error("Error to prepare statement", ex);
                        throw new TransferPaymentsException("Error to transfer payments");
                    }
                });
                int[] stored = preparedStatement.executeBatch();
                log.info("Stored row {} to {} partition", stored.length, partition);
            } catch (SQLException ex) {
                log.error("Error to execute batch", ex);
                throw new TransferPaymentsException("Error to transfer payments");
            }
        }
    }

    public UserBillAggregate billAggregate(@NotNull final String username) {
        BigDecimal aggregate = BigDecimal.ZERO;
        for (DataSource dataSource: dataSources) {
            try (Connection connection = dataSource.getConnection()){
                PreparedStatement ps = connection.prepareStatement(AGGREGATE_QUERY);
                ps.setString(1, username);
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    BigDecimal sum = resultSet.getBigDecimal("sum");
                    if (sum != null) {
                        aggregate = aggregate.add(sum);
                    }
                }
            } catch (SQLException ex) {
                log.error("Error get aggregate", ex);
                throw new SummarizeException("Error get aggregate");
            }
        }
        return new UserBillAggregate(username, aggregate);
    }
}
