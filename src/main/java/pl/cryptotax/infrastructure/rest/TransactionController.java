package pl.cryptotax.infrastructure.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.cryptotax.domain.service.TransactionImportService;
import pl.cryptotax.infrastructure.rest.dto.ImportSummaryResponse;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionImportService transactionImportService;

    public TransactionController(TransactionImportService transactionImportService) {
        this.transactionImportService = transactionImportService;
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportSummaryResponse> importTransactions(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file cannot be empty");
        }

        String fileName = file.getOriginalFilename();
        var transactions = transactionImportService.importTransactions(fileName, file.getInputStream());
        var summary = ImportSummaryResponse.from(transactions);

        return ResponseEntity.ok(summary);
    }
}