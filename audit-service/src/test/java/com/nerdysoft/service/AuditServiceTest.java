package com.nerdysoft.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nerdysoft.dto.response.LogResponseDto;
import com.nerdysoft.entity.activity.UserActivityEvent;
import com.nerdysoft.entity.activity.enums.ActionType;
import com.nerdysoft.entity.activity.enums.EntityType;
import com.nerdysoft.entity.activity.enums.Status;
import com.nerdysoft.entity.transaction.TransactionEvent;
import com.nerdysoft.entity.transaction.enums.TransactionStatus;
import com.nerdysoft.entity.transaction.enums.TransactionType;
import com.nerdysoft.repo.TransactionEventRepository;
import com.nerdysoft.repo.UserActivityEventRepository;
import com.nerdysoft.service.impl.AuditServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class AuditServiceTest {
    @Mock
    private TransactionEventRepository transactionEventRepository;

    @Mock
    private UserActivityEventRepository activityEventRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    private static TransactionEvent te;
    private static UserActivityEvent ae;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        te = new TransactionEvent();
        te.setTransactionId(UUID.randomUUID());
        te.setFromWalletId(UUID.randomUUID());
        te.setToWalletId(UUID.randomUUID());
        te.setAmount(BigDecimal.valueOf(333));
        te.setCurrency("USD");
        te.setTimestamp(LocalDateTime.now());
        te.setStatus(TransactionStatus.SUCCESS);
        te.setTransactionType(TransactionType.ACCOUNT_TO_ACCOUNT);

        ae = new UserActivityEvent();
        ae.setUserId(UUID.randomUUID());
        ae.setActionType(ActionType.CREATE);
        ae.setEntityType(EntityType.ACCOUNT);
        ae.setTimestamp(LocalDateTime.now());
        ae.setOldData(null);
        ae.setNewData("...");
        ae.setStatus(Status.SUCCESS);
    }

    @Test
    void getAllTransactionLogs_AllOk_ShouldReturnListOfTransactionEvents() {
        //Given
        List<TransactionEvent> expected = List.of(te);

        when(transactionEventRepository.findAll()).thenReturn(expected);

        //When
        List<TransactionEvent> actual = auditService.getAllTransactionLogs();

        //Then
        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected.get(0), actual.get(0));

        verify(transactionEventRepository, times(1)).findAll();
    }

    @Test
    void getByTransactionId_AllOk_ShouldReturnTransactionEvent() {
        //Given
        UUID transactionId = te.getTransactionId();

        when(transactionEventRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(te));

        //When
        TransactionEvent actual = auditService.getByTransactionId(transactionId);

        //Then
        assertEquals(te, actual);

        verify(transactionEventRepository, times(1)).findByTransactionId(transactionId);
    }

    @Test
    void saveTransactionEvent_AllOk_ShouldInvokeSaveMethod() {
        when(transactionEventRepository.save(te)).thenReturn(te);

        auditService.saveTransactionEvent(te);

        verify(transactionEventRepository, times(1)).save(te);
    }

    @Test
    void saveUserActivityEvent_AllOk_ShouldInvokeSaveMethod() {
        when(activityEventRepository.save(ae)).thenReturn(ae);

        auditService.saveUserActivityEvent(ae);

        verify(activityEventRepository, times(1)).save(ae);
    }

    @Test
    void logUserActivity_AllOk_ShouldReturnCorrectLogResponseDto() {
        LogResponseDto expected = new LogResponseDto("User action logged successfully", Status.SUCCESS);
        when(activityEventRepository.save(ae)).thenReturn(ae);

        LogResponseDto actual = auditService.logUserActivity(ae);

        assertEquals(expected, actual);

        verify(activityEventRepository, times(1)).save(ae);
    }

    @Test
    void getAllUserActivityLogs_AllOk_ShouldReturnListOfAllLogs() {
        List<UserActivityEvent> expected = List.of(ae);

        when(activityEventRepository.findAll()).thenReturn(List.of(ae));

        List<UserActivityEvent> actual = auditService.getAllUserActivityLogs();

        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());

        verify(activityEventRepository, times(1)).findAll();
    }

    @Test
    void getUserActivityLogsByUserId_AllOk_ShouldReturnListOfCertainUserLogs() {
        UUID userId = ae.getUserId();
        List<UserActivityEvent> expected = List.of(ae);

        when(activityEventRepository.findAllByUserId(userId)).thenReturn(List.of(ae));

        List<UserActivityEvent> actual = auditService.getUserActivityLogsByUserId(userId);

        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());

        verify(activityEventRepository, times(1)).findAllByUserId(userId);
    }
}
