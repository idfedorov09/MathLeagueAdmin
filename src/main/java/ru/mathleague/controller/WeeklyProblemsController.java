package ru.mathleague.controller;

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

import java.util.List;

@Controller
@RequestMapping("/weekly-problems")
public class WeeklyProblemsController {

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

        return "redirect:/weekly-problems/"+weeklyTask.getId();
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
