package com.example.todoapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.todoapp.entity.Task;
import com.example.todoapp.repository.TaskRepository;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    /** 一覧表示（sortOrder 順） */
    @GetMapping
    public String index(Model model) {
        List<Task> tasks = taskRepository.findAllByOrderBySortOrderAsc();
        model.addAttribute("tasks", tasks);
        model.addAttribute("newTask", new Task());
        return "tasks";
    }

    /** 新規追加 */
    @PostMapping
    public String create(@ModelAttribute Task task) {
        // 新規タスクに最大 sortOrder + 1 を設定
        Integer maxSort = taskRepository.findAll().stream()
                .map(Task::getSortOrder)
                .max(Integer::compareTo)
                .orElse(0);

        task.setSortOrder(maxSort + 1);
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    /** 完了 */
    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setCompleted(true);
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    /** 未完了に戻す */
    @PostMapping("/{id}/uncomplete")
    public String uncomplete(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setCompleted(false);
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    /** 削除 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/tasks";
    }

    /** 編集画面表示 */
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Task task = taskRepository.findById(id).orElseThrow();
        model.addAttribute("task", task);
        return "edit_task";
    }

    /** 編集保存 */
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute Task formTask) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setTitle(formTask.getTitle());
        task.setDescription(formTask.getDescription());
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    /** ★ 並び替え結果を受け取る（超重要） */
    @PostMapping("/reorder")
    @ResponseBody
    public void reorder(@RequestBody List<Long> sortedIds) {
        int order = 1;
        for (Long id : sortedIds) {
            Task task = taskRepository.findById(id).orElseThrow();
            task.setSortOrder(order++);
            taskRepository.save(task);
        }
    }
}
