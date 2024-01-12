package com.henrymeds.codedemo.api;

import com.henrymeds.codedemo.dto.AppointmentSlot;
import com.henrymeds.codedemo.dto.ProviderAvailability;
import com.henrymeds.codedemo.scheduler.ProviderScheduler;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
public class Provider {

    private final ProviderScheduler providerScheduler;

    @Autowired
    public Provider(final ProviderScheduler providerScheduler) {
        this.providerScheduler = providerScheduler;
    }

    @GetMapping("/availability")
    public List<AppointmentSlot> getProviderAvailability(@RequestParam final UUID providerId, @RequestParam final LocalDate requestedDate) {
        return providerScheduler.getAvailability(providerId, requestedDate);
    }

    @PutMapping("/availability")
    public Integer updateProviderAvailability(@RequestBody @NonNull ProviderAvailability providerAvailability) {
        return providerScheduler.updateAvailability(providerAvailability);
    }
}
