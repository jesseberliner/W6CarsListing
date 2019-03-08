package com.example.demo.Controllers;

import com.cloudinary.utils.ObjectUtils;
import com.example.demo.Models.Car;
import com.example.demo.Models.Category;
import com.example.demo.Repositories.CarRepository;
import com.example.demo.Repositories.CategoryRepository;
import com.example.demo.Services.CloudinaryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    CarRepository carRepo;

    @Autowired
    CategoryRepository catRepo;

    @Autowired
    CloudinaryConfig cloudc;


    @RequestMapping("/")
    public String home(Model model)
    {
        model.addAttribute("cars", carRepo.findAllByDeletedIsFalse());
        model.addAttribute("category", catRepo.findAll());
        return "index";
    }

//Car CRUD
    @GetMapping("/add")
    public String add(Model model){
        model.addAttribute("car", new Car());
        model.addAttribute("category", catRepo.findAll());
        return "add";
    }

    @PostMapping("/add")
    public String processForm(@Valid Car car, BindingResult result, @RequestParam("file") MultipartFile file, Model model){


        if(file.isEmpty())
        {
            model.addAttribute("car", car);
            return "add";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            car.setImage(uploadResult.get("url").toString());
            carRepo.save(car);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }
        if(result.hasErrors()){
            model.addAttribute(car);
            return "add";
        }
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showCar(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("car", carRepo.findById(id));
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateCar(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("car", carRepo.findById(id));
        model.addAttribute("cat", catRepo.findAll());
        return "add";
    }

    @RequestMapping("/delete/{id}")
    public String delCar(@PathVariable("id") long id)
    {
        Car toDelete = carRepo.findById(id);
        toDelete.setDeleted(true);
        return "redirect:/";
    }


//Category CRUD
    @GetMapping("/addCat")
    public String addCat(Model model){
        model.addAttribute("category", new Category());
        return "addCat";
    }

    @PostMapping("/addCat")
    public String processForm(@Valid Category category, BindingResult result, Model model){


        if(result.hasErrors()){
            model.addAttribute(category);
            return "add";
        }
        catRepo.save(category);

        return "redirect:/";
    }


//Other
    @GetMapping("/seeByCat/{id}")
    public String seeByCategory(Model model, @PathVariable("id") long id)
    {
        model.addAttribute("cars", carRepo.findByCategoryId(id));
        return "index";
    }
}
