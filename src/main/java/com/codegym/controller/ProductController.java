package com.codegym.controller;

import com.codegym.model.Product;
import com.codegym.model.ProductForm;
import com.codegym.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller

public class ProductController {
    @Autowired
    private IProductService productService;

    @Value("${file-upload}")
    private String uploadPath;

    @GetMapping("/products/list")
    public ModelAndView showListProduct(@RequestParam (name = "q", required = false) String q) {
        ModelAndView modelAndView = new ModelAndView("/product/list");
        List<Product> products = productService.findAll();
        if (q != null) {
            products = productService.findByName(q);
        }
        modelAndView.addObject("products", products);
        return modelAndView;
    }

    @GetMapping("/products/create")
    public ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("/product/create");
        ProductForm productForm = new ProductForm();
        modelAndView.addObject("productForm", productForm);
        return modelAndView;
    }

    @PostMapping("/products/create")
    public ModelAndView createProduct(@ModelAttribute ProductForm productForm) {
        ModelAndView modelAndView = new ModelAndView("redirect:/products/list");
        MultipartFile multipartFile = productForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        long currentTime = System.currentTimeMillis();
        fileName = currentTime + fileName;
        try {
            FileCopyUtils.copy(multipartFile.getBytes(), new File(uploadPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Product product = new Product(productForm.getId(), productForm.getName(), productForm.getPrice(), productForm.getDescription(), fileName);
        productService.save(product);
        return modelAndView;
    }

    @GetMapping("/products/delete/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("/product/delete");
        Product product = productService.findById(id);
        modelAndView.addObject("product", product);
        return modelAndView;
    }

    @PostMapping("/products/delete/{id}")
    public ModelAndView deleteProduct(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("redirect:/products/list");
        Product product = productService.findById(id);
        File file = new File(uploadPath + product.getImage());
        if (file.exists()) {
            file.delete();
        }
        productService.removeById(id);
        return modelAndView;
    }

    @GetMapping("/products/edit/{id}")
    public ModelAndView showFormEdit(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("/product/edit");
        Product product = productService.findById(id);
        modelAndView.addObject("product", product);
        return modelAndView;
    }

    @PostMapping("/products/edit/{id}")
    public ModelAndView editProduct(@PathVariable Long id, @ModelAttribute ProductForm productForm) {
        Product oldProduct = productService.findById(id);
        MultipartFile multipartFile = productForm.getImage();
        if (multipartFile.getSize() != 0) {
            String fileName = multipartFile.getOriginalFilename();
            long currentTime = System.currentTimeMillis();
            fileName = currentTime + fileName;
            oldProduct.setImage(fileName);
            try {
                FileCopyUtils.copy(multipartFile.getBytes(), new File(uploadPath + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        oldProduct.setName(productForm.getName());
        oldProduct.setPrice(productForm.getPrice());
        oldProduct.setDescription(productForm.getDescription());
        productService.save(oldProduct);
        return new ModelAndView("redirect:/products/list");
    }

    @GetMapping("/products/{id}")
    public ModelAndView showViewDetailProduct(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("/product/view");
        Product product = productService.findById(id);
        modelAndView.addObject("product", product);
        return modelAndView;
    }
}
