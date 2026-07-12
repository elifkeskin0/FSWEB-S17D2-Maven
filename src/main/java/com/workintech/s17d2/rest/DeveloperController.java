package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.Experience;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {
    public Map<Integer, Developer> developers;
    private final Taxable developerTax;

    public DeveloperController(Taxable developerTax) {
        this.developerTax = developerTax;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer addDeveloper(@RequestBody Developer developer) {
        Developer taxedDeveloper = createDeveloperWithTax(developer);
        developers.put(taxedDeveloper.getId(), taxedDeveloper);
        return taxedDeveloper;
    }

    @PutMapping("/{id}")
    public Developer updateDeveloper(@PathVariable int id, @RequestBody Developer developer) {
        developer.setId(id);
        Developer taxedDeveloper = createDeveloperWithTax(developer);
        developers.put(id, taxedDeveloper);
        return taxedDeveloper;
    }

    @DeleteMapping("/{id}")
    public Developer deleteDeveloper(@PathVariable int id) {
        return developers.remove(id);
    }

    private Developer createDeveloperWithTax(Developer developer) {
        Experience experience = developer.getExperience();
        double salary = developer.getSalary();

        if (experience == Experience.JUNIOR) {
            return new JuniorDeveloper(developer.getId(), developer.getName(), salary - (salary * taxRate(developerTax.getSimpleTaxRate(), 15d)));
        }

        if (experience == Experience.MID) {
            return new MidDeveloper(developer.getId(), developer.getName(), salary - (salary * taxRate(developerTax.getMiddleTaxRate(), 25d)));
        }

        return new SeniorDeveloper(developer.getId(), developer.getName(), salary - (salary * taxRate(developerTax.getUpperTaxRate(), 35d)));
    }

    private double taxRate(Double rate, double defaultRate) {
        return rate == null ? defaultRate : rate;
    }
}
