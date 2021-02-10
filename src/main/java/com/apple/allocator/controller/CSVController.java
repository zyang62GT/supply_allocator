package com.apple.allocator.controller;

import java.util.List;

import com.apple.allocator.message.ResponseMessage;
import com.apple.allocator.model.SourcingRule;
import com.apple.allocator.service.DemandOrderService;
import com.apple.allocator.service.PlanService;
import com.apple.allocator.service.SourcingRuleService;
import com.apple.allocator.service.SupplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin("http://localhost:8081")
@Controller
@RequestMapping("/api/csv")
public class CSVController {

    @Autowired
    SourcingRuleService sourcingRuleService;

    @Autowired
    DemandOrderService demandOrderService;

    @Autowired
    SupplyService supplyService;

    @Autowired
    PlanService planService;

    @PostMapping("/uploadSourcingRule")
    public ResponseEntity<ResponseMessage> uploadSourcingRule(@RequestParam("file") MultipartFile file) {
        String message = "";
        //CSVHelper.hasCSVFormat(file)
        if (true) {
            try {
                sourcingRuleService.save(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        message = "Please upload an csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }

    @PostMapping("/uploadDemandOrder")
    public ResponseEntity<ResponseMessage> uploadDemandOrder(@RequestParam("file") MultipartFile file) {
        String message = "";
        //CSVHelper.hasCSVFormat(file)
        if (true) {
            try {
                demandOrderService.save(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        message = "Please upload an csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }

    @PostMapping("/uploadSupply")
    public ResponseEntity<ResponseMessage> uploadSupply(@RequestParam("file") MultipartFile file) {
        String message = "";
        //CSVHelper.hasCSVFormat(file)
        if (true) {
            try {
                supplyService.save(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        message = "Please upload an csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }

    @GetMapping("/sourcingrule")
    public ResponseEntity<List<SourcingRule>> getAllSourcingRules() {
        try {
            List<SourcingRule> SourcingRules = sourcingRuleService.getAllSourcingRules();

            if (SourcingRules.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(SourcingRules, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/downloadplan")
    public ResponseEntity<Resource> getFile() {
        String filename = "plan.csv";
        InputStreamResource file = new InputStreamResource(planService.load());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }


}

