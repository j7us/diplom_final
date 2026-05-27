package org.example.bootstrap.config;

import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import org.example.adapter.batch.mapper.EnterpriseBatchMapper;
import org.example.adapter.batch.dto.EnterpriseImport;
import org.example.adapter.repository.entity.EnterpriseEntity;
import org.example.model.Enterprise;
import org.example.application.service.EnterpriseService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EnterpriseImportBatchConfig {
    @Bean
    @StepScope
    public FlatFileItemReader<EnterpriseImport> enterpriseCsvImportReader(
            @Value("#{jobParameters['filePath']}") String filePath
    ) {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("name", "country", "productionCapacity", "timeZone");

        BeanWrapperFieldSetMapper<EnterpriseImport> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(EnterpriseImport.class);

        DefaultLineMapper<EnterpriseImport> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return new FlatFileItemReaderBuilder<EnterpriseImport>()
                .name("enterpriseCsvImportReader")
                .resource(new FileSystemResource(filePath))
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    @StepScope
    public JsonItemReader<EnterpriseImport> enterpriseJsonImportReader(
            @Value("#{jobParameters['filePath']}") String filePath
    ) {
        return new JsonItemReaderBuilder<EnterpriseImport>()
                .name("enterpriseJsonImportReader")
                .resource(new FileSystemResource(filePath))
                .jsonObjectReader(new JacksonJsonObjectReader<>(EnterpriseImport.class))
                .build();
    }

    @Bean
    public ItemProcessor<EnterpriseImport, Enterprise> enterpriseImportProcessor(
            EnterpriseBatchMapper enterpriseBatchMapper
    ) {
        return enterpriseBatchMapper::toModel;
    }

    @Bean
    @StepScope
    public ItemWriter<Enterprise> enterpriseImportWriter(
            EnterpriseService enterpriseService,
            @Value("#{jobParameters['username']}") String username
    ) {
        return chunk -> chunk.forEach(item -> enterpriseService.create(item, username));
    }

    @Bean
    public Step enterpriseCsvImportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            @Qualifier("enterpriseCsvImportReader") ItemReader<EnterpriseImport> enterpriseCsvImportReader,
            ItemProcessor<EnterpriseImport, Enterprise> enterpriseImportProcessor,
            ItemWriter<Enterprise> enterpriseImportWriter
    ) {
        return new StepBuilder("enterpriseCsvImportStep", jobRepository)
                .<EnterpriseImport, Enterprise>chunk(20, transactionManager)
                .reader(enterpriseCsvImportReader)
                .processor(enterpriseImportProcessor)
                .writer(enterpriseImportWriter)
                .build();
    }

    @Bean
    public Step enterpriseJsonImportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            @Qualifier("enterpriseJsonImportReader") ItemReader<EnterpriseImport> enterpriseJsonImportReader,
            ItemProcessor<EnterpriseImport, Enterprise> enterpriseImportProcessor,
            ItemWriter<Enterprise> enterpriseImportWriter
    ) {
        return new StepBuilder("enterpriseJsonImportStep", jobRepository)
                .<EnterpriseImport, Enterprise>chunk(20, transactionManager)
                .reader(enterpriseJsonImportReader)
                .processor(enterpriseImportProcessor)
                .writer(enterpriseImportWriter)
                .build();
    }

    @Bean
    public Job enterpriseCsvImportJob(JobRepository jobRepository,
                                      Step enterpriseCsvImportStep) {
        return new JobBuilder("enterpriseCsvImportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(enterpriseCsvImportStep)
                .build();
    }

    @Bean
    public Job enterpriseJsonImportJob(JobRepository jobRepository,
                                       Step enterpriseJsonImportStep) {
        return new JobBuilder("enterpriseJsonImportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(enterpriseJsonImportStep)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<EnterpriseEntity> enterpriseExportReader(
            EntityManagerFactory entityManagerFactory,
            @Value("#{jobParameters['username']}") String username
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);

        return new JpaPagingItemReaderBuilder<EnterpriseEntity>()
                .name("enterpriseExportReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select e from EnterpriseEntity e join e.managerEntities m where m.username = :username")
                .parameterValues(params)
                .pageSize(20)
                .build();
    }

    @Bean
    public ItemProcessor<EnterpriseEntity, EnterpriseImport> enterpriseExportProcessor(
            EnterpriseBatchMapper enterpriseBatchMapper
    ) {
        return enterpriseBatchMapper::toImport;
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<EnterpriseImport> enterpriseCsvExportWriter(
            @Value("#{jobParameters['filePath']}") String filePath
    ) {
        FlatFileItemWriter<EnterpriseImport> writer = new FlatFileItemWriter<>();
        writer.setName("enterpriseCsvExportWriter");
        writer.setResource(new FileSystemResource(filePath));
        writer.setHeaderCallback(header -> header.write("name,country,productionCapacity,timeZone"));

        DelimitedLineAggregator<EnterpriseImport> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");

        BeanWrapperFieldExtractor<EnterpriseImport> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"name", "country", "productionCapacity", "timeZone"});
        lineAggregator.setFieldExtractor(fieldExtractor);

        writer.setLineAggregator(lineAggregator);

        return writer;
    }

    @Bean
    @StepScope
    public JsonFileItemWriter<EnterpriseImport> enterpriseJsonExportWriter(
            @Value("#{jobParameters['filePath']}") String filePath
    ) {
        return new JsonFileItemWriterBuilder<EnterpriseImport>()
                .name("enterpriseJsonExportWriter")
                .resource(new FileSystemResource(filePath))
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .build();
    }

    @Bean
    public Step enterpriseCsvExportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JpaPagingItemReader<EnterpriseEntity> enterpriseExportReader,
            ItemProcessor<EnterpriseEntity, EnterpriseImport> enterpriseExportProcessor,
            FlatFileItemWriter<EnterpriseImport> enterpriseCsvExportWriter
    ) {
        return new StepBuilder("enterpriseCsvExportStep", jobRepository)
                .<EnterpriseEntity, EnterpriseImport>chunk(20, transactionManager)
                .reader(enterpriseExportReader)
                .processor(enterpriseExportProcessor)
                .writer(enterpriseCsvExportWriter)
                .build();
    }

    @Bean
    public Step enterpriseJsonExportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JpaPagingItemReader<EnterpriseEntity> enterpriseExportReader,
            ItemProcessor<EnterpriseEntity, EnterpriseImport> enterpriseExportProcessor,
            JsonFileItemWriter<EnterpriseImport> enterpriseJsonExportWriter
    ) {
        return new StepBuilder("enterpriseJsonExportStep", jobRepository)
                .<EnterpriseEntity, EnterpriseImport>chunk(20, transactionManager)
                .reader(enterpriseExportReader)
                .processor(enterpriseExportProcessor)
                .writer(enterpriseJsonExportWriter)
                .build();
    }

    @Bean
    public Job enterpriseCsvExportJob(JobRepository jobRepository,
                                      Step enterpriseCsvExportStep) {
        return new JobBuilder("enterpriseCsvExportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(enterpriseCsvExportStep)
                .build();
    }

    @Bean
    public Job enterpriseJsonExportJob(JobRepository jobRepository,
                                       Step enterpriseJsonExportStep) {
        return new JobBuilder("enterpriseJsonExportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(enterpriseJsonExportStep)
                .build();
    }
}
