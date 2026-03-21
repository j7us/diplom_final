package org.example.config;

import org.example.dto.EnterpriseRestDto;
import org.example.dto.enterprise.EnterpriseImport;
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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

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
}
