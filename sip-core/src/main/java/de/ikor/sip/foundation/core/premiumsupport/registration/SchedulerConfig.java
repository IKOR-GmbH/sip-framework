package de.ikor.sip.foundation.core.premiumsupport.registration;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

//@Component
public class SchedulerConfig implements SchedulingConfigurer {
    public ScheduledTaskRegistrar scheduledTaskRegistrar;
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        this.scheduledTaskRegistrar = scheduledTaskRegistrar;
        //scheduledTaskRegistrar.getScheduledTasks().stream().findFirst().get().cancel();
    }
}
