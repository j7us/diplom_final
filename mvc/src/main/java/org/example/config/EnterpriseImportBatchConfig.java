package org.example.config;

import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import org.example.dto.EnterpriseRestDto;
import org.example.dto.enterprise.EnterpriseImport;
import org.example.entity.Enterprise;
import org.example.map.EnterpriseRestMapper;
import org.example.service.EnterpriseService;
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
    public ItemProcessor<EnterpriseImport, EnterpriseRestDto> enterpriseImportProcessor(
            EnterpriseRestMapper enterpriseRestMapper
    ) {
        return enterpriseRestMapper::toRestDto;
    }

    @Bean
    @StepScope
    public ItemWriter<EnterpriseRestDto> enterpriseImportWriter(
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
            ItemProcessor<EnterpriseImport, EnterpriseRestDto> enterpriseImportProcessor,
            ItemWriter<EnterpriseRestDto> enterpriseImportWriter
    ) {
        return new StepBuilder("enterpriseCsvImportStep", jobRepository)
                .<EnterpriseImport, EnterpriseRestDto>chunk(20, transactionManager)
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
            ItemProcessor<EnterpriseImport, EnterpriseRestDto> enterpriseImportProcessor,
            ItemWriter<EnterpriseRestDto> enterpriseImportWriter
    ) {
        return new StepBuilder("enterpriseJsonImportStep", jobRepository)
                .<EnterpriseImport, EnterpriseRestDto>chunk(20, transactionManager)
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
    public JpaPagingItemReader<Enterprise> enterpriseExportReader(
            EntityManagerFactory entityManagerFactory,
            @Value("#{jobParameters['username']}") String username
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);

        return new JpaPagingItemReaderBuilder<Enterprise>()
                .name("enterpriseExportReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select e from Enterprise e join e.managers m where m.username = :username")
                .parameterValues(params)
                .pageSize(20)
                .build();
    }

    @Bean
    public ItemProcessor<Enterprise, EnterpriseImport> enterpriseExportProcessor(
            EnterpriseRestMapper enterpriseRestMapper
    ) {
        return enterpriseRestMapper::toImport;
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
            JpaPagingItemReader<Enterprise> enterpriseExportReader,
            ItemProcessor<Enterprise, EnterpriseImport> enterpriseExportProcessor,
            FlatFileItemWriter<EnterpriseImport> enterpriseCsvExportWriter
    ) {
        return new StepBuilder("enterpriseCsvExportStep", jobRepository)
                .<Enterprise, EnterpriseImport>chunk(20, transactionManager)
                .reader(enterpriseExportReader)
                .processor(enterpriseExportProcessor)
                .writer(enterpriseCsvExportWriter)
                .build();
    }

    @Bean
    public Step enterpriseJsonExportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JpaPagingItemReader<Enterprise> enterpriseExportReader,
            ItemProcessor<Enterprise, EnterpriseImport> enterpriseExportProcessor,
            JsonFileItemWriter<EnterpriseImport> enterpriseJsonExportWriter
    ) {
        return new StepBuilder("enterpriseJsonExportStep", jobRepository)
                .<Enterprise, EnterpriseImport>chunk(20, transactionManager)
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
