package com.example.demo;

import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Klass { 
    
    List<Student> students;

    public void dong(){
        System.out.println(this.getStudents());
    }
    
}
