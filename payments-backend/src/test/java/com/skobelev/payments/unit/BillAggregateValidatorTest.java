package com.skobelev.payments.unit;

import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.dto.PayloadType;
import com.skobelev.payments.exceptions.NotValidBillAggregateRequestException;
import com.skobelev.payments.service.BillAggregateValidatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BillAggregateValidatorTest {

    private static final BillAggregateValidatorService validatorService =
            new BillAggregateValidatorService();

    @Test
    @DisplayName("Check valid BillAggregateRequest with PayLoadType.URL")
    void validUrl() {
        BillAggregateRequest request =
                buildRequest("Gosha", "http://Gosha.ru", null, PayloadType.URL);
        assertTrue(validatorService.validateBillRequest(request));
    }

    @Test
    @DisplayName("Check not valid BillAggregateRequest with PayLoadType.URL")
    void notValidUrl() {
        BillAggregateRequest request =
                buildRequest("Vim", null, null, PayloadType.URL);
        assertThrows(NotValidBillAggregateRequestException.class, () -> validatorService.validateBillRequest(request));
    }

    @Test
    @DisplayName("Check valid BillAggregateRequest with PayLoadType.TOPIC")
    void validTopic() {
        BillAggregateRequest request =
                buildRequest("Masha", null, "billing", PayloadType.TOPIC);
        assertTrue(validatorService.validateBillRequest(request));
    }

    @Test
    @DisplayName("Check not valid BillAggregateRequest with PayLoadType.TOPIC")
    void notValidTopic() {
        BillAggregateRequest request =
                buildRequest("Evgen", null, null, PayloadType.TOPIC);
        assertThrows(NotValidBillAggregateRequestException.class, () -> validatorService.validateBillRequest(request));
    }

    private BillAggregateRequest buildRequest(String username, String url, String topic, PayloadType type) {
        BillAggregateRequest request = new BillAggregateRequest();
        request.setPayloadType(type);
        request.setUsername(username);
        request.setUrl(url);
        request.setTopic(topic);
        return request;
    }
}
