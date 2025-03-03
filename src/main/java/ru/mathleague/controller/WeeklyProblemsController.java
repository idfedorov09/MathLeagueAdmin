package ru.mathleague.controller;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
import ru.mathleague.util.ProblemSender;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/weekly-problems")
public class WeeklyProblemsController {

    private final String PROBLEMS_DIR = "./data/weekly-problem/";
    private final int DEFAULT_DPI = 750;

    @Autowired
    WeeklyTaskRepository weeklyTaskRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProblemSender problemSender;

    @GetMapping("list")
    public String problemsList(Model model){
        List<WeeklyTask> allProblems = weeklyTaskRepository.findAllByOrderByPriority();
        model.addAttribute("allProblems", allProblems);
        return "time_problems";
    }

    private void generateDirectory(long problemId){
        generateDirectory(PROBLEMS_DIR+problemId, PROBLEMS_DIR+"template");
    }

    @GetMapping("new-problem")
    public String createProblem(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User author = (User)authentication.getPrincipal();
        WeeklyTask weeklyTask = new WeeklyTask(weeklyTaskRepository.count(), author);
        weeklyTaskRepository.save(weeklyTask);

        long id = weeklyTask.getId();
        generateDirectory(id);

        return "redirect:/weekly-problems/"+id;
    }

    /*
    ALSO NEED TO ADD REMOVE DIRECTORY
     */
    private void generateDirectory(String dir, String originDir){
        try {
            FileUtils.copyDirectory(new File(originDir), new File(dir));
        } catch (java.nio.file.FileAlreadyExistsException e){
            System.out.println("Attempt to create used weekly problem dir.");
        }
        catch (Exception e) {
            System.out.println("Create directory (weekly problem) error:");
            e.printStackTrace();
        }
    }


    @GetMapping("image/{id}")
    @ResponseBody
    public byte[] getImage(@PathVariable Long id) throws IOException{
        String imagePath = PROBLEMS_DIR + id +"/image.jpg";
        Path image = Paths.get(imagePath);
        byte[] resultImage = null;
        try{
            resultImage = Files.readAllBytes(image);
        }catch (java.nio.file.NoSuchFileException e){
            generateDirectory(id);
            resultImage = Files.readAllBytes(image);
        }
        finally {
            return resultImage;
        }

    }


    private ResponseEntity<Map<String, Object>> regenLatex(long problemId){
        String currentDir = PROBLEMS_DIR+problemId;
        ProcessBuilder processBuilder = new ProcessBuilder("xelatex", "main.tex");
        processBuilder.directory(new File(currentDir));

        String errorMessage = "not defined";
        int errorLine;

        try {
            Process process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                if(line.startsWith("! ")){
                    errorMessage = line;
                }
                if(line.startsWith("l.")){
                    errorLine = extractNumber(line);
                    process.destroy();

                    Map<String, Object> response = new HashMap<>();
                    response.put("errorLine", errorLine);
                    response.put("message", errorMessage);

                    return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
                }
            }

        }catch (IOException e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> regenLatexAndDate(WeeklyTask task, boolean isRevertTime){
        setDate(10, task, isRevertTime);
        return regenLatex(task.getId());
    }

    @PostMapping("refresh")
    public ResponseEntity<Map<String, Object>> refreshProblem(@RequestParam("id") Long problemId)  {
        return refreshProblem(problemId, false);
    }

    public ResponseEntity<Map<String, Object>> refreshProblem(Long problemId, boolean isRevertTime)  {
        String currentDir = PROBLEMS_DIR+problemId;
        WeeklyTask curTask = weeklyTaskRepository.findById(problemId);

        if(curTask==null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        generateDirectory(problemId);

        String problemCode = curTask.getLatexCode();
        try {
            FileUtils.writeStringToFile(new File(currentDir+"/Problems/1.tex"), problemCode, "UTF-8");
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<Map<String, Object>> regenLatexResp = regenLatexAndDate(curTask, isRevertTime);

        if (regenLatexResp.getStatusCode() != HttpStatus.OK) {
            return regenLatexResp;
        }

        try {
            generateImageFromPdf(currentDir+"/main.pdf", currentDir+"/image.jpg", DEFAULT_DPI);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void setDate(int lineNumber, WeeklyTask weeklyTask, boolean isRevertTime){
        File file = new File(PROBLEMS_DIR+weeklyTask.getId()+"/main.tex");
        var dateArray = weeklyTask.postDate(isRevertTime);
        String date = "\\task[\\day = %d \\month = %d \\year = %d]{1}".formatted(dateArray[0], dateArray[1], dateArray[2]);
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            lines.set(lineNumber - 1, date);
            FileUtils.writeLines(file, "UTF-8", lines);
        } catch (java.nio.file.NoSuchFileException e) {
            generateDirectory(weeklyTask.getId());
        }catch (IOException e){
            System.out.println("Writing latex date (weekly task) error: " + e);
        }
    }

    private static int extractNumber(String inputString) {
        Pattern pattern = Pattern.compile("l\\.(\\d+)");
        Matcher matcher = pattern.matcher(inputString);

        if (matcher.find()) {
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        } else {
            throw new IllegalArgumentException("parsing error");
        }
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

    private void removeProblem(long problemId){
        String path = PROBLEMS_DIR + problemId;
        try {
            FileUtils.deleteDirectory(new File(path));
        }
        catch (IOException e){
            System.out.println("Can't remove directory "+path);
            System.out.println("Maybe, directory doesn't exist. Error: "+e);
        }
    }

    @PostMapping("delete/{problem-id}")
    @Transactional
    public ResponseEntity<String> deleteProblem(@PathVariable("problem-id") Long problemId){

        WeeklyTask taskToRemove = weeklyTaskRepository.findById(problemId);

        if(taskToRemove!=null) {
            weeklyTaskRepository.delete(taskToRemove);
            weeklyTaskRepository.decreasePriorityAfter(taskToRemove.getPriority());
        }

        removeProblem(problemId);

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

    private void sendProblemToChat(long problemId){
        problemSender.sendPhotoToTelegram(PROBLEMS_DIR+problemId+"/image.jpg", "#ЗадачаНедели");
    }

    @Scheduled(cron = "0 0 9 ? * SUN", zone = "Europe/Moscow")  //every sunday 9am (on Moscow)
    //@Scheduled(cron = "0 */2 * * * *", zone = "Europe/Moscow") //every 2 minutes
    public void sendTask() {
        WeeklyTask problemToSend = weeklyTaskRepository.findByPriority(0L);

        if(problemToSend==null){
            System.out.println("нет задач.");
            // реализовать отправку сообщения админам о том что нужно выложить задачу
            return;
        }

        long taskId = problemToSend.getId();

        refreshProblem(taskId, true); /// здесь можно отправить сообщение админам о неудачной обработке задачи

        sendProblemToChat(taskId);
        weeklyTaskRepository.delete(problemToSend);
        weeklyTaskRepository.decreasePriorityForAllTasks();

        removeProblem(taskId);
    }

    @PostMapping("updatePriority")
    public ResponseEntity<String>  updatePriority(@RequestParam("oldPriority") Long oldPriority,
                                                  @RequestParam("newPriority") Long newPriority){

        long targetId = weeklyTaskRepository.findIdByPriority(oldPriority);

        if(oldPriority==newPriority){
            return ResponseEntity.ok("OK!! NIGGER OK!!!");
        }

        if (Math.max(oldPriority, newPriority)>=weeklyTaskRepository.count()){
            //обновить страницу, данные устарели
            return ResponseEntity.status(HttpStatus.UPGRADE_REQUIRED).body("Update page!");
        }

        if(oldPriority<newPriority){
            weeklyTaskRepository.decreasePriorityBetween(oldPriority, newPriority);
        }
        else{
            weeklyTaskRepository.increasePriorityBetween(newPriority, oldPriority);
        }

        weeklyTaskRepository.changePriorityById(targetId, newPriority);

        return ResponseEntity.ok("updated.");
    }

}
