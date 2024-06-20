package com.inhatc.android_final;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TodoDetailsActivity extends AppCompatActivity {

    EditText titleEditText; //제목
    EditText contentEditText; //글
    ImageButton saveToDoBtn; //저장버튼

    FirebaseDatabase myFirebase; //firebasedatabase의 진입점
    DatabaseReference myDB_Reference = null; //Realtime database의 특정 위치 참조
    String strHeader = "Todolist";

    TextView pageTitleTextView;
    String title, content, todoId;
    boolean isEditMode=false;
    TextView deleteTodoTextViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_todo_details);

        titleEditText = findViewById(R.id.Todo_title_text);
        contentEditText = findViewById(R.id.Todo_content_text);
        saveToDoBtn = findViewById(R.id.save_todo_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteTodoTextViewBtn = findViewById(R.id.delete_todo_text_view_btn);

        //전달받은 데이터
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        todoId = getIntent().getStringExtra("todoId");

        if(todoId!=null && !todoId.isEmpty())
            isEditMode = true;

        myFirebase = FirebaseDatabase.getInstance();
        myDB_Reference = myFirebase.getReference();

        titleEditText.setText(title);
        contentEditText.setText(content);

        if(isEditMode){
            pageTitleTextView.setText("todo리스트 상세");
            deleteTodoTextViewBtn.setVisibility(View.VISIBLE);
        }


        // 상단 저장 버튼 눌렀을 시
        saveToDoBtn.setOnClickListener((v)->saveTodo());

        deleteTodoTextViewBtn.setOnClickListener((v)-> deletetodoFromFirebase());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //todo 저장 메서드
    void saveTodo(){
        String todoTitle = titleEditText.getText().toString();
        String todoContent = contentEditText.getText().toString();
        if(todoTitle == null || todoContent.isEmpty()){
            titleEditText.setError("제목이나 내용을 입력하지 않으셨습니다");
            return;
        }
        Todo todo = new Todo();
        todo.setTitle(todoTitle);
        todo.setContent(todoContent);

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        todo.setTimestamp(timestamp);

        saveTodoToFirebase(todo);
    }

    void saveTodoToFirebase(Todo todo){

        myDB_Reference.child(strHeader).orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if(isEditMode){
                   //수정모드
                   todo.setTodoId(Integer.parseInt(todoId));
                   myDB_Reference.child(strHeader).child(Integer.toString(todo.getTodoId())).setValue(todo)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()) {
                                       // 데이터 설정 성공
                                       Toast.makeText(TodoDetailsActivity.this, "Todo를 수정하였습니다.", Toast.LENGTH_SHORT).show();
                                       finish();
                                   } else {
                                       // 데이터 설정 실패
                                       Toast.makeText(TodoDetailsActivity.this, "Todo 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   // 데이터 저장 중 오류 발생
                                   Toast.makeText(TodoDetailsActivity.this, "Todo 수정에 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                               }
                           });
               }else {
                   //입력모드
                   //DataSnapshot Firebase Realtime Database의 데이터를 읽을 때 사용되는 클래스입니다.
                   int newId = 1; // db가 비어 있다면 1
                   if (dataSnapshot.exists()) {
                       //for each로 todo list를 가져와 1개씩 검사
                       for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                           Todo lastTodo = snapshot.getValue(Todo.class);
                           if (lastTodo != null) {
                               //마지막 todoid에서 + 1
                               newId = lastTodo.getTodoId() + 1;
                           }
                       }
                   }
                   todo.setTodoId(newId);
                   //데이터 저장
                   myDB_Reference.child(strHeader).child(String.valueOf(newId)).setValue(todo)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()) {
                                       // 데이터 설정 성공
                                       Toast.makeText(TodoDetailsActivity.this, "Todo를 저장하였습니다.", Toast.LENGTH_SHORT).show();
                                       finish();
                                   } else {
                                       // 데이터 설정 실패
                                       Toast.makeText(TodoDetailsActivity.this, "Todo 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   // 데이터 저장 중 오류 발생
                                   Toast.makeText(TodoDetailsActivity.this, "Todo 저장에 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                               }
                           });
               }
           }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터베이스 오류 처리
                Toast.makeText(TodoDetailsActivity.this, "데이터베이스 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void deletetodoFromFirebase(){
        myDB_Reference.child(strHeader).child(todoId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(TodoDetailsActivity.this, "Todo를 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(TodoDetailsActivity.this, "Todo 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TodoDetailsActivity.this, "Todo 삭제에 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void getTodoToFirebase(){

    }
}