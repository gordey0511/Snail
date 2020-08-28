package com.user.snail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TestForStudent extends AppCompatActivity {

    RecyclerView categoriesView;
    FirebaseFirestore firestore;
    DocumentReference db;
    static int questionsCount;
    static int categoriesCount;
    static TestForStudent.TestType id;
    String ans1,ans2;
    static ArrayList<ArrayList> questions = new ArrayList<>();
    static ArrayList<ArrayList> categories = new ArrayList<>();
    static ArrayList<Double> resList = new ArrayList<>();
    static ArrayList<Boolean> checked = new ArrayList<>();


    public static class MyViewModel extends ViewModel {

        MutableLiveData<List<Subcategory>> data;

        public LiveData<List<Subcategory> > getData() {
            if (data == null) {
                data = new MutableLiveData<>();
                loadData();
            }
            return data;
        }

        private void loadData() {
            data.postValue(TestForStudent.getRandomData());
        }

    }


    static List<Subcategory> getRandomData() {
        final Random random = new Random();
        final ArrayList<Subcategory> subcategoryList = new ArrayList<>();

        for(int i2=0;i2<questionsCount+1;i2++){
            final int i=i2;
            if(i == 0) {
                subcategoryList.add(new TestForStudent.Subcategory("",0,0));
            }else{
                subcategoryList.add(new TestForStudent.Subcategory(questions.get(i-1).get(0).toString(),Integer.valueOf(questions.get(i-1).get(1).toString()),Integer.valueOf(questions.get(i-1).get(2).toString())));
            }

        }
        return subcategoryList;
    }


    enum TestType {
        INTERESTS_MAP, TYPE_OF_THE_FUTURE_PROFESSION;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);

        setTitle("Ответьте на вопросы");
        questions.clear();
        categories.clear();
        resList.clear();
        checked.clear();

        Log.e("onCreate", "START");
        id = TestForStudent.TestType.valueOf(getIntent().getStringExtra("id"));

        Log.e("onCreate", "MIDDLE");
        firestore = FirebaseFirestore.getInstance();
        String doc;
        if (id == TestType.TYPE_OF_THE_FUTURE_PROFESSION){
            doc = "TypeOfTheFutureProfession";
        }else {
            doc = "InterestsMap";
            ans1 = getIntent().getStringExtra("ans1");
        }
        db = firestore.collection("test").document(doc);
        db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        final Map<String, Object> m = documentSnapshot.getData();
                        questionsCount = Integer.valueOf(m.get("questionsCount").toString());
                        categoriesCount = Integer.valueOf(m.get("categoriesCount").toString());
                        for(int i = 0;i < questionsCount;i++){
                            questions.add((ArrayList) m.get("question"+i));
                            checked.add(false);
                            resList.add(Double.valueOf(0));
                        }
                        for(int i = 0;i < categoriesCount;i++){
                            categories.add((ArrayList) m.get("category"+i));
                        }
                        FloatingActionButton fab=findViewById(R.id.goTest1);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean ch = true;
                                for(int i = 1;i < questionsCount;i++){
                                    if(!checked.get(i)){
                                        Log.e("onClickCH",String.valueOf(i));
                                        ch = false;
                                    }
                                }
                                if(ch) {
                                    double mx = -1e9;
                                    int ans = 0;
                                    for(int i = 0;i < questionsCount;i++){
                                        if(resList.get(i).doubleValue() > mx){
                                            mx = resList.get(i).doubleValue();
                                            ans = i;
                                        }
                                    }

                                    Intent intent = new Intent(TestForStudent.this, Ans1.class);

                                    if(id == TestType.TYPE_OF_THE_FUTURE_PROFESSION){
                                        intent.putExtra("ans1", categories.get(ans).get(2).toString());
                                    }else if(id == TestType.INTERESTS_MAP){
                                        intent.putExtra("ans1", ans1);
                                        intent.putExtra("ans2", categories.get(ans).get(0).toString());
                                    }

//                                    Toast.makeText(getApplicationContext(), categories.get(ans).get(1).toString(), Toast.LENGTH_LONG);
                                    intent.putExtra("id", id.toString());
                                    intent.putExtra("name", categories.get(ans).get(0).toString());
                                    intent.putExtra("text", categories.get(ans).get(1).toString());
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(getApplicationContext(),"Ответьте на все вопросы",Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        MyViewModel model = new ViewModelProvider(TestForStudent.this).get(MyViewModel.class);

                        LiveData<List<Subcategory>> data = model.getData();
                        data.observe(TestForStudent.this, new Observer<List<Subcategory>>() {
                            @Override
                            public void onChanged(@Nullable List<Subcategory> s) {
                            }
                        });

                        categoriesView=findViewById(R.id.categories_list_theme_test);

                        List<Subcategory> CircleCategories = TestForStudent.getRandomData();
                        categoriesView.setHasFixedSize(true);
                        categoriesView.setLayoutManager(new LinearLayoutManager(
                                getApplicationContext(),
                                RecyclerView.VERTICAL,
                                false
                        ));
                        categoriesView.setAdapter(new SubcategoriesAdapter(CircleCategories,id));
                    }
                }
            }
        });

    }


    static class Subcategory {
        String text;
        int category;
        int weight;
        int id = categoriesCount+1;
        int click_position;

        Subcategory(String text, int category, int weight) {
            this.text = text;
            this.category = category;
            this.weight = weight;
            this.click_position=-1;
        }
    }




    static class SubcategoriesAdapter extends RecyclerView.Adapter<SubcategoriesAdapter.ViewHolder> {

        List<Subcategory> data;
        TestForStudent.TestType type;

        SubcategoriesAdapter(List<Subcategory> data, TestForStudent.TestType type) {
            this.data = data;
            this.type=type;
        }

        @Override
        public SubcategoriesAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

            View view;
            if (viewType==1){
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.subcategory_list_item_test1_2, parent, false);
            }else{
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.subcategory_list_item_test1, parent, false);
            }
//            SubcategoriesAdapter.ViewHolder viewHolder;

            return new SubcategoriesAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final SubcategoriesAdapter.ViewHolder holder, final int position) {
            final Subcategory subcategory = data.get(position);


            if(position==0) {
                if(type!= TestType.TYPE_OF_THE_FUTURE_PROFESSION) {
                    holder.subcategoryText.setText("На данном экране представлены вопросы для определения наиболее подходящей специальности. Выбирете наиболее подожящий для вас вариант. Только отвечайте честно");
                }else {
                    holder.subcategoryText.setText("На данном экране представлены вопросы для определения вашего типа личности. Выбирете наиболее подожящий для вас вариант. Только отвечайте честно");
                }
            }else{
                if(subcategory.click_position==1){
                    holder.radioGroup.check(R.id.c0);
                }else if(subcategory.click_position==2){
                    holder.radioGroup.check(R.id.c1);
                }else if(subcategory.click_position==3){
                    holder.radioGroup.check(R.id.c2);
                }else if(subcategory.click_position==4){
                    holder.radioGroup.check(R.id.c3);
                }else if(subcategory.click_position==5){
                    holder.radioGroup.check(R.id.c4);
                }else{
                    holder.radioGroup.clearCheck();
                }
                holder.subcategoryText.setText(subcategory.text);
                holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(subcategory.id == categoriesCount+1){
                            double cof;
                            RadioButton rb = group.findViewById(checkedId);
                            if(rb!=null) {
                                String id = rb.getText().toString();
                                if (id.equals("Точно да")) {
                                    cof = 2;
                                    subcategory.click_position=1;
                                } else if (id.equals("Скорее да")) {
                                    cof = 1;
                                    subcategory.click_position=2;
                                } else if (id.equals("Не знаю")) {
                                    cof = 0;
                                    subcategory.click_position=3;
                                } else if (id.equals("Скорее нет")) {
                                    cof = -1;
                                    subcategory.click_position=4;
                                } else {
                                    cof = -2;
                                    subcategory.click_position=5;
                                }

                                checked.set(position - 1, true);
                                resList.set(subcategory.category, resList.get(subcategory.category) + (subcategory.weight * cof));
                            }
                        }
                    }
                });
            }

        }


        @Override
        public int getItemViewType(int position) {
            int res=0;
            if(position==0){
                res=1;
            }
            return res;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView subcategoryText;
            RadioGroup radioGroup;
            LinearLayout linearLayout;
            Button buttonChoose;

            ViewHolder(View itemView) {
                super(itemView);
                linearLayout=itemView.findViewById(R.id.linearLayout);
                radioGroup=itemView.findViewById(R.id.radioGroup);
                subcategoryText=itemView.findViewById(R.id.textViewTextSection);
                buttonChoose=itemView.findViewById(R.id.buttonChoose);
            }
        }
    }
}