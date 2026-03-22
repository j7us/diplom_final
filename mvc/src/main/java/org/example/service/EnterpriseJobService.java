package org.example.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.example.dto.enterprise.EnterpriseExport;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EnterpriseJobService {
    private final JobLauncher jobLauncher;
    private final Job enterpriseCsvImportJob;
    private final Job enterpriseJsonImportJob;
    private final Job enterpriseCsvExportJob;
    private final Job enterpriseJsonExportJob;

    public EnterpriseJobService(JobLauncher jobLauncher,
                                @Qualifier("enterpriseCsvImportJob") Job enterpriseCsvImportJob,
                                @Qualifier("enterpriseJsonImportJob") Job enterpriseJsonImportJob,
                                @Qualifier("enterpriseCsvExportJob") Job enterpriseCsvExportJob,
                                @Qualifier("enterpriseJsonExportJob") Job enterpriseJsonExportJob) {
        this.jobLauncher = jobLauncher;
        this.enterpriseCsvImportJob = enterpriseCsvImportJob;
        this.enterpriseJsonImportJob = enterpriseJsonImportJob;
        this.enterpriseCsvExportJob = enterpriseCsvExportJob;
        this.enterpriseJsonExportJob = enterpriseJsonExportJob;
    }

    public void importCsv(MultipartFile file, String username) {
        importEnterprises(file, username, "csv", enterpriseCsvImportJob);
    }

    public void importJson(MultipartFile file, String username) {
        importEnterprises(file, username, "json", enterpriseJsonImportJob);
    }

    public EnterpriseExport exportCsv(String username) {
        return exportEnterprises(username, "csv", "enterprises.csv", "text/csv", enterpriseCsvExportJob);
    }

    public EnterpriseExport exportJson(String username) {
        return exportEnterprises(username, "json", "enterprises.json", "application/json", enterpriseJsonExportJob);
    }

    private void importEnterprises(MultipartFile file, String username, String extension, Job job) {
        Path tempFilePath = copyToTempFile(file, extension);
        try {
            runJob(job, tempFilePath, username);
        } finally {
            deleteTempFile(tempFilePath);
        }
    }

    private EnterpriseExport exportEnterprises(
            String username,
            String extension,
            String fileName,
            String contentType,
            Job job
    ) {
        Path tempFilePath = createTempFile("enterprise-export-", extension);
        try {
            runJob(job, tempFilePath, username);
            byte[] fileContent = readFileBytes(tempFilePath);

            return new EnterpriseExport(fileContent, fileName, contentType);
        } finally {
            deleteTempFile(tempFilePath);
        }
    }

    private Path copyToTempFile(MultipartFile file, String extension) {
        Path tempFilePath = createTempFile("enterprise-import-", extension);

        try {
            file.transferTo(tempFilePath);
            return tempFilePath;
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось сохранить временный файл импорта", ex);
        }
    }

    private Path createTempFile(String prefix, String extension) {
        try {
            return Files.createTempFile(prefix, "." + extension);
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось создать временный файл", ex);
        }
    }

    private void runJob(Job job, Path filePath, String username) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", filePath.toAbsolutePath().toString())
                .addString("username", username)
                .addLong("runId", System.currentTimeMillis())
                .toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            if (!BatchStatus.COMPLETED.equals(jobExecution.getStatus())) {
                throw new RuntimeException("Batch-задача завершилась с ошибкой");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private byte[] readFileBytes(Path tempFilePath) {
        try {
            return Files.readAllBytes(tempFilePath);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void deleteTempFile(Path tempFilePath) {
        try {
            Files.deleteIfExists(tempFilePath);
        } catch (IOException ignored) {
        }
    }
}
