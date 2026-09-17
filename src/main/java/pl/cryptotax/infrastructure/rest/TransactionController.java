package pl.cryptotax.infrastructure.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.cryptotax.domain.port.TransactionRepository;
import pl.cryptotax.domain.service.TransactionImportService;
import pl.cryptotax.infrastructure.rest.dto.CreateTransactionRequestDto;
import pl.cryptotax.infrastructure.rest.dto.ImportSummaryResponse;
import pl.cryptotax.infrastructure.rest.dto.TransactionResponseDto;
import pl.cryptotax.infrastructure.rest.mapper.TransactionRestMapper;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final TransactionImportService transactionImportService;
    private final TransactionRestMapper transactionRestMapper;
    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionImportService transactionImportService,
                                 TransactionRestMapper transactionRestMapper,
                                 TransactionRepository transactionRepository) {
        this.transactionImportService = transactionImportService;
        this.transactionRestMapper = transactionRestMapper;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportSummaryResponse> importTransactions(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file cannot be empty");
        }

        String fileName = file.getOriginalFilename();

        // Dodaliśmy DEFAULT_USER_ID jako 3. parametr:
        var transactions = transactionImportService.importTransactions(fileName, file.getInputStream(), DEFAULT_USER_ID);
        var summary = ImportSummaryResponse.from(transactions);

        return ResponseEntity.ok(summary);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDto createTransaction(@Valid @RequestBody CreateTransactionRequestDto dto) {
        var domain = transactionRestMapper.toDomain(dto, DEFAULT_USER_ID);
        var savedDomain = transactionRepository.save(domain);

        return transactionRestMapper.toDto(savedDomain);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponseDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionRestMapper::toDto)
                .toList();
    }
}