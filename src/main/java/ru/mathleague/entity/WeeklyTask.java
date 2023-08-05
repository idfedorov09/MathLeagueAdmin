package ru.mathleague.entity;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mathleague.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name="t_weekly_task")
public class WeeklyTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long priority;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private User author;

    private String title;

    @Column(columnDefinition="text")
    private String latexCode;

    public WeeklyTask(){}
    public WeeklyTask(Long priority, User author){
        this.priority = priority;
        this.author = author;

        this.title = "Введите название задачи..";
        this.latexCode = "Известно, что попарные углы между векторами  $\\vec{e}_1, \\vec{e}_2, \\ldots, \\vec{e}_n \\in \\mathbb{R}^N$ больше  $90^{\\circ} +\\alpha$, где $\\alpha>0$. Докажите, что тогда\n" +
                "\n" +
                "\\begin{equation*}\n" +
                "n < \\frac{1}{\\sin\\alpha}+1.\n" +
                "\\end{equation*}";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getLatexCode() {
        return latexCode;
    }

    public void setLatexCode(String latexCode) {
        this.latexCode = latexCode;
    }

    public int[] postDate(long count, boolean isRevertTime) {
        ZoneId moscowZone = ZoneId.of("Europe/Moscow");
        LocalDateTime moscowNow = LocalDateTime.now(moscowZone);

        //добавить настройку времени?? тут уже удобно начать переписывать на котлин, но зачем, если все равно всю матлигу переписывать..
        if(isRevertTime){
            moscowNow = moscowNow.minusMinutes(30);
        }

        LocalDate today = moscowNow.toLocalDate();

        DayOfWeek currentDayOfWeek = today.getDayOfWeek();
        int daysUntilNextSunday = DayOfWeek.SUNDAY.getValue() - currentDayOfWeek.getValue();

        if(daysUntilNextSunday==0 && moscowNow.getHour()>=9){
            daysUntilNextSunday += 7;
        }

        long totalDaysToAdd = daysUntilNextSunday + count * 7;
        LocalDate nextSunday = today.plusDays(totalDaysToAdd);

        int[] res = new int[3];
        res[0] = nextSunday.getDayOfMonth();
        res[1] = nextSunday.getMonthValue();
        res[2] = nextSunday.getYear();
        return res;
    }

    public int[] postDate(boolean isRevertTime) {
        return this.postDate(this.priority, isRevertTime);
    }

    public String postDateStr(boolean isRevertTime) {
        int[] dateArray = this.postDate(isRevertTime);
        return String.format("%02d.%02d.%04d", dateArray[0], dateArray[1], dateArray[2]);
    }

    public String postDateStr() {
        return postDateStr(false);
    }


}
