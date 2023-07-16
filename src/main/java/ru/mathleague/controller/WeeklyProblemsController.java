package ru.mathleague.controller;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mathleague.entity.User;
import ru.mathleague.entity.WeeklyTask;
import ru.mathleague.repository.UserRepository;
import ru.mathleague.repository.WeeklyTaskRepository;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/weekly-problems")
public class WeeklyProblemsController {

    private final String PROBLEMS_DIR = "./data/weekly-problem/";
    private final int DEFAULT_DPI = 750;

    @Autowired
    WeeklyTaskRepository weeklyTaskRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("list")
    public String problemsList(Model model){
        List<WeeklyTask> allProblems = weeklyTaskRepository.findAllByOrderByPriority();
        model.addAttribute("allProblems", allProblems);
        return "time_problems";
    }

    @GetMapping("new-problem")
    public String createProblem(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User author = (User)authentication.getPrincipal();
        WeeklyTask weeklyTask = new WeeklyTask(weeklyTaskRepository.count(), author);
        weeklyTaskRepository.save(weeklyTask);
        long id = weeklyTask.getId();

        generateDirectory(PROBLEMS_DIR+id, PROBLEMS_DIR+"template");

        return "redirect:/weekly-problems/"+id;
    }

    /*
    ALSO NEED TO ADD REMOVE DIRECTORY
     */
    private boolean generateDirectory(String dir, String originDir){
        try {
            FileUtils.copyDirectory(new File(originDir), new File(dir));
            return true;
        } catch (IOException e) {
            System.out.println("Create directory (weekly problem) error:");
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("image/{id}")
    @ResponseBody
    public byte[] getImage(@PathVariable Long id) throws IOException {
        String imagePath = PROBLEMS_DIR + id +"/image.jpg"; // Предполагаем, что у вас используется расширение .jpg
        Path image = Paths.get(imagePath);
        return Files.readAllBytes(image);
    }

    @PostMapping("refresh")
    public ResponseEntity<String> refreshProblem(@RequestParam("id") Long problemId)  {

        String currentDir = PROBLEMS_DIR+problemId;
        WeeklyTask curTask = weeklyTaskRepository.findById(problemId);

        if(curTask==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Problem not found");
        }

        String problemCode = curTask.getLatexCode();
        try {
            FileUtils.writeStringToFile(new File(currentDir+"/Problems/1.tex"), problemCode, "UTF-8");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error with writing problem to file.");
        }

        System.out.println("Compile daily task with id="+problemId);

        ProcessBuilder processBuilder = new ProcessBuilder("xelatex", "main.tex");
        processBuilder.directory(new File(currentDir));
        int exitCode = 0;

        try {
            Process process = processBuilder.start();
            exitCode = process.waitFor();
        }catch (IOException | InterruptedException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error on waiting process builder!");
        }

        System.out.println(problemId+" compiled with code "+exitCode);

        if (exitCode != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error on compiling!");
        }

        try {
            generateImageFromPdf(currentDir+"/main.pdf", currentDir+"/image.jpg", DEFAULT_DPI);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error on create img from pdf!");
        }

        return ResponseEntity.ok("successful compiled");
    }

    private static void generateImageFromPdf(String in, String out, int dpi) throws IOException {
        PDDocument document = PDDocument.load(new File(in));
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(
                    page, dpi, ImageType.RGB);
            ImageIOUtil.writeImage(
                    bim, out, dpi);
        }
        document.close();
    }


    @GetMapping("{problem-id}")
    public String editProblem(@PathVariable("problem-id") Long problemId, Model model){

        WeeklyTask weeklyTask = weeklyTaskRepository.findById(problemId);
        model.addAttribute("problem", weeklyTask);

        return "edit_problem";
    }

    @PostMapping("delete/{problem-id}")
    @Transactional
    public ResponseEntity<String> deleteProblem(@PathVariable("problem-id") Long problemId){

        WeeklyTask taskToRemove = weeklyTaskRepository.findById(problemId);

        if(taskToRemove!=null) {
            weeklyTaskRepository.delete(taskToRemove);
        }
        return ResponseEntity.ok("Problem deleted successfully");
    }

    @PostMapping("save-title")
    public ResponseEntity<String> saveProblemTitle(@RequestParam Long id, @RequestBody String newTitle){

        WeeklyTask task = weeklyTaskRepository.findById(id);

        if(task==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Problem not found");
        }

        task.setTitle(newTitle);
        weeklyTaskRepository.save(task);

        return ResponseEntity.ok("Changed problem title with id="+id);
    }

    @PostMapping("save-texcode")
    public ResponseEntity<String> saveProblemTexcode(@RequestParam Long id, @RequestBody String newCode){

        WeeklyTask task = weeklyTaskRepository.findById(id);

        if(task==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Problem not found");
        }

        task.setLatexCode(newCode);
        weeklyTaskRepository.save(task);

        return ResponseEntity.ok("Changed problem tex code with id="+id);
    }

}
