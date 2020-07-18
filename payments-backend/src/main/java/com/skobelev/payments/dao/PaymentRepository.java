package com.skobelev.payments.dao;

import com.skobelev.payments.dto.PaymentDto;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;

public class PaymentRepository {

    public static final String INSERT_QUERY = "insert into payments(user_from, user_to, money) values (?, ?, ?);";
    private final List<DataSource> dataSources;
    private final AtomicInteger counter = new AtomicInteger();
    private final int connectionCount;

    public PaymentRepository(@NotNull final List<DataSource> dataSources) {
        this.dataSources = dataSources;
        this.connectionCount = dataSources.size();
    }

    public void add(@NotNull final List<PaymentDto> payments) {
        Map<Integer, List<PaymentDto>> paymentMap =
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
                        ex.printStackTrace();
                    }
                });
                int[] stored = preparedStatement.executeBatch();
                System.out.println(Arrays.toString(stored));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
