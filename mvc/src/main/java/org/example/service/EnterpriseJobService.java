package org.example.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EnterpriseJobService {
    private final JobLauncher jobLauncher;
    private final Job enterpriseCsvImportJob;
    private final Job enterpriseJsonImportJob;

    public EnterpriseJobService(JobLauncher jobLauncher, Job enterpriseCsvImportJob, Job enterpriseJsonImportJob) {
        this.jobLauncher = jobLauncher;
        this.enterpriseCsvImportJob = enterpriseCsvImportJob;
        this.enterpriseJsonImportJob = enterpriseJsonImportJob;
    }

    public void importCsv(MultipartFile file, String username) {
        importEnterprises(file, username, "csv", enterpriseCsvImportJob);
    }

    public void importJson(MultipartFile file, String username) {
        importEnterprises(file, username, "json", enterpriseJsonImportJob);
    }

    private void importEnterprises(MultipartFile file, String username, String extension, Job job) {
        Path tempFilePath = copyToTempFile(file, extension);
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", tempFilePath.toAbsolutePath().toString())
                    .addString("username", username)
                    .addLong("runId", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            deleteTempFile(tempFilePath);
        }
    }

    private Path copyToTempFile(MultipartFile file, String extension) {
        try {
            Path tempFilePath = Files.createTempFile("enterprise-import-", "." + extension);
            file.transferTo(tempFilePath);
            return tempFilePath;
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось сохранить временный файл импорта", ex);
        }
    }

    private void deleteTempFile(Path tempFilePath) {
        try {
            Files.deleteIfExists(tempFilePath);
        } catch (IOException ignored) {
        }
    }
}
