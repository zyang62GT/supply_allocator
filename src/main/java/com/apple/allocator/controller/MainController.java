package com.apple.allocator.controller;

import com.apple.allocator.model.Plan;
import com.apple.allocator.model.SourcingRule;
import com.apple.allocator.repository.*;
import com.apple.allocator.service.DemandOrderService;
import com.apple.allocator.service.PlanService;
import com.apple.allocator.service.UnsatisfiedOrderService;
import com.apple.allocator.storage.StorageFileNotFoundException;
import com.apple.allocator.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired
    private SourcingRuleRepository sourcingRuleRepository;

    @Autowired
    private DemandOrderRepository demandOrderRepository;

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private DemandOrderService demandOrderService;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PlanService planService;

    @Autowired
    private UnsatisfiedOrderService unsatisfiedOrderService;

    @Autowired
    private UnsatisfiedOrderRepository unsatisfiedOrderRepository;

    private final StorageService storageService;



    @Autowired
    MainController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(path="/add")
    public @ResponseBody
    String addNewSourcingRule (@RequestParam String site
            , @RequestParam String customer, @RequestParam String product) {
        SourcingRule sourcingRule = new SourcingRule();
        sourcingRule.setSite(site);
        sourcingRule.setCustomer(customer);
        sourcingRule.setProduct(product);
        sourcingRuleRepository.save(sourcingRule);
        return "Saved";
    }

    @GetMapping(path="/test")
    public @ResponseBody
    List<Plan> getAllSourcingRules(){
        planService.allocate();
        return planService.getPlans();
    }

    @GetMapping(path="/")
    public String listUploadedFiles(Model model) throws IOException {
        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(MainController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping(path="/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping(path="/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/showplans", method = RequestMethod.GET)
    public String showPlans(Model md){
        if (planService.getPlans().isEmpty()) {
            planService.allocate();
        }
        md.addAttribute("plans", planService.getPlans());
        return "plans";
    }

    @RequestMapping(value = "/showrules", method = RequestMethod.GET)
    public String showRules(Model md){
        md.addAttribute("rules", sourcingRuleRepository.findAll());
        return "rules";
    }

    @PostMapping(value = "/deleterules")
    public String deleteRules () {
        sourcingRuleRepository.deleteAll();
        return "uploadForm";
    }

    @RequestMapping(value = "/showdemands", method = RequestMethod.GET)
    public String showDemands(Model md){
        md.addAttribute("demands", demandOrderRepository.findAll());
        return "demands";
    }

    @PostMapping(value = "/deletedemands")
    public String deleteDemands () {
        demandOrderRepository.deleteAll();
        return "uploadForm";
    }

    @PostMapping(value = "/deleteplans")
    public String deletePlans () {
        planService.setPlans(new ArrayList<>());
        planRepository.deleteAll();
        return "uploadForm";
    }

    @RequestMapping(value = "/showsupplies", method = RequestMethod.GET)
    public String showSupplies(Model md){
        md.addAttribute("supplies", supplyRepository.findAll());
        return "supplies";
    }

    @RequestMapping(value = "/showunsatisfied", method = RequestMethod.GET)
    public String showUnsatisfiedOrders(Model md){
        md.addAttribute("unsatisfied", unsatisfiedOrderRepository.findAll());
        return "unsatisfied";
    }

    @PostMapping(value = "/deletesupplies")
    public String deleteSupplies () {
        supplyRepository.deleteAll();
        return "uploadForm";
    }

    @PostMapping(value = "/deleteunsatisfied")
    public String deleteUnsatisfiedOrders () {
        unsatisfiedOrderRepository.deleteAll();
        return "uploadForm";
    }
}
