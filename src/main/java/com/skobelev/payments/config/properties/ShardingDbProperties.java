package com.skobelev.payments.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Validated
@ConfigurationProperties("sharding-db")
public class ShardingDbProperties {

    @NotEmpty(message = "Data base connection list cannot be empty")
    private List<DbConnection> connections;

}
