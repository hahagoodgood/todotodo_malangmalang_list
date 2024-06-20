package com.inhatc.android_final;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

// TodoAdapter 클래스는 FirebaseRecyclerAdapter를 확장하여 Todo 항목을 RecyclerView에 표시합니다.
public class TodoAdapter extends FirebaseRecyclerAdapter<Todo, TodoAdapter.TodoViewHolder> {

    // Context는 어댑터가 실행되는 컨텍스트를 나타내며, 레이아웃 인플레이터 등 다양한 작업에 사용됩니다.
    Context context;

    // 생성자: FirebaseRecyclerOptions와 Context를 받아 초기화합니다.
    public TodoAdapter(@NonNull FirebaseRecyclerOptions<Todo> options, Context context){
        super(options);
        this.context = context;
    }

    // onBindViewHolder 메서드는 각 항목에 데이터를 바인딩합니다.
    @Override
    protected void onBindViewHolder(@NonNull TodoViewHolder holder, int position, @NonNull Todo todo) {
        holder.titleTextView.setText(todo.getTitle());
        holder.dateTextView.setText(todo.getTimestamp());

        holder.itemView.setOnClickListener((v)->{
            Intent intent = new Intent(context, TodoDetailsActivity.class);
            intent.putExtra("title",todo.title);
            intent.putExtra("content",todo.content);
            intent.putExtra("todoId",Integer.toString(todo.todoId));
            context.startActivity(intent);
        });
    }

    // onCreateViewHolder 메서드는 새로운 ViewHolder를 생성합니다.
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_todo_item, parent, false);
        return new TodoViewHolder(view);
    }

    // TodoViewHolder 클래스는 RecyclerView의 각 항목 뷰를 보유합니다.
    class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView; //제목
        TextView dateTextView;

        // 생성자: 아이템 뷰를 받아 내부의 텍스트 뷰를 초기화합니다.
        public TodoViewHolder(@NonNull View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.todo_title_text_View);
            dateTextView = itemView.findViewById(R.id.todo_date_text_view);
        }
    }
}
