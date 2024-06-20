package com.inhatc.android_final;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity{
    FirebaseDatabase myFirebase;
    DatabaseReference myDB_Reference = null;
    FloatingActionButton addToDoBtn; //todolist 추가 버튼 변수
    RecyclerView recyclerView; //대량의 데이터 또는 동적으로 생성된 아이템을 효율적으로 표시하기 위해 설계된 뷰 그룹
    ImageButton menuBtn;
    TodoAdapter todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //유튜브 동작 부분
        YouTubePlayerView youTubePlayerView1 = findViewById(R.id.youtube_player_view1);
        YouTubePlayerView youTubePlayerView2 = findViewById(R.id.youtube_player_view2);
        getLifecycle().addObserver(youTubePlayerView1);
        getLifecycle().addObserver(youTubePlayerView2);

        youTubePlayerView1.addYouTubePlayerListener(new
            AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    super.onReady(youTubePlayer);
                    String videoId = "pIQmxUk_FdI";
                    youTubePlayer.loadVideo(videoId, 0);
                }
            });

        youTubePlayerView2.addYouTubePlayerListener(new
            AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    super.onReady(youTubePlayer);
                    String videoId = "pIQmxUk_FdI";
                    youTubePlayer.loadVideo(videoId, 0);
                }
            });


        // =======================변수 선언 부=======================
        addToDoBtn = findViewById(R.id.add_todo_btn); //버튼 부분
        recyclerView = findViewById(R.id.recyler_view); //todo list 보관용 recyclerview
        menuBtn = findViewById(R.id.menu_btn); //메뉴 버튼


        //todo list 추가 버튼 클릭 이벤트
        addToDoBtn.setOnClickListener((v)-> startActivity(new Intent(MainActivity.this,TodoDetailsActivity.class)));

        //메뉴 버튼 클릭 이벤트
//        menuBtn.setOnClickListener((v)->startActivity(new Intent(MainActivity.this, DiaryActivity.this)));

        //recyclerView 설정 파일
        setupRecyclerView();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    void showMenu(){
        //메뉴 부분
        PopupMenu popupMenu = new PopupMenu(MainActivity.this,menuBtn);
        popupMenu.getMenu().add("일기");
        popupMenu.show();
    }
    
    void setupRecyclerView(){

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myDB_Reference = FirebaseDatabase.getInstance().getReference().child("Todolist");

        FirebaseRecyclerOptions<Todo> options = new FirebaseRecyclerOptions.Builder<Todo>()
                .setQuery(myDB_Reference,Todo.class).build();

        todoAdapter = new TodoAdapter(options, this);
        recyclerView.setAdapter(todoAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 액티비티가 시작되면 FirebaseRecyclerAdapter가 데이터 변경을 수신하도록 리스닝을 시작합니다.
        todoAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 액티비티가 중지되면 FirebaseRecyclerAdapter가 데이터 변경 수신을 중지하도록 리스닝을 중지합니다.
        todoAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 액티비티가 다시 활성화되면 어댑터에 데이터가 변경되었음을 알리고 RecyclerView를 갱신합니다.
        todoAdapter.notifyDataSetChanged();
    }
}