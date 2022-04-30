package ru.otus.hw14;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class ShellCommands {
    private final JobLauncher jobLauncher;
    private final Job migrationToMongoDataJob;

    @ShellMethod(value = "Start migration", key = {"sm"})
    void migrateToMongo() throws Exception {
        JobExecution execution = jobLauncher.run(migrationToMongoDataJob,
                new JobParametersBuilder()
                        .addLong("unique", System.nanoTime())
                        .toJobParameters()
        );
        System.out.println(execution);
    }
}
