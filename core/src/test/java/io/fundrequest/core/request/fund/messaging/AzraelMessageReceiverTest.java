package io.fundrequest.core.request.fund.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fundrequest.core.request.RequestService;
import io.fundrequest.core.request.fund.FundService;
import io.fundrequest.core.request.fund.command.AddFundsCommand;
import io.fundrequest.core.request.fund.domain.ProcessedBlockchainEvent;
import io.fundrequest.core.request.fund.infrastructure.ProcessedBlockchainEventRepository;
import io.fundrequest.core.request.fund.messaging.dto.FundedEthDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.StringWriter;
import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class AzraelMessageReceiverTest {

    private AzraelMessageReceiver messageReceiver;
    private FundService fundService;
    private ProcessedBlockchainEventRepository blockchainEventRepository;
    private ObjectMapper objectMapper;
    private RequestService requestService;

    @Before
    public void setUp() throws Exception {
        fundService = mock(FundService.class);
        blockchainEventRepository = mock(ProcessedBlockchainEventRepository.class);
        requestService = mock(RequestService.class);
        objectMapper = new ObjectMapper();
        messageReceiver = new AzraelMessageReceiver(requestService, objectMapper, blockchainEventRepository);
    }

    @Test
    public void receiveMessage() throws Exception {
        FundedEthDto dto = createDto();
        StringWriter w = new StringWriter();
        objectMapper.writeValue(w, dto);
        when(blockchainEventRepository.findOne(dto.getTransactionHash())).thenReturn(Optional.empty());

        messageReceiver.receiveMessage(w.toString());

        verifyFundsAdded(dto);
        verify(blockchainEventRepository).save(new ProcessedBlockchainEvent(dto.getTransactionHash()));
    }

    @Test
    public void receiveMessageTransactionAlreadyProcessed() throws Exception {
        FundedEthDto dto = createDto();
        StringWriter w = new StringWriter();
        objectMapper.writeValue(w, dto);
        when(blockchainEventRepository.findOne(dto.getTransactionHash())).thenReturn(Optional.of(new ProcessedBlockchainEvent(dto.getTransactionHash())));

        messageReceiver.receiveMessage(w.toString());

        verifyZeroInteractions(fundService);
    }

    private FundedEthDto createDto() {
        FundedEthDto dto = new FundedEthDto();
        dto.setAmount("5223000000000000000");
        dto.setPlatform("GITHUB");
        dto.setPlatformId("1");
        dto.setFrom("0x");
        dto.setTransactionHash("0xh");
        return dto;
    }

    private void verifyFundsAdded(FundedEthDto dto) {
        ArgumentCaptor<Principal> principalArgumentCaptor = ArgumentCaptor.forClass(Principal.class);
        ArgumentCaptor<AddFundsCommand> addFundsCommandArgumentCaptor = ArgumentCaptor.forClass(AddFundsCommand.class);
        verify(fundService).addFunds(addFundsCommandArgumentCaptor.capture());
        assertThat(addFundsCommandArgumentCaptor.getValue().getAmountInWei()).isEqualTo(dto.getAmount());
    }
}