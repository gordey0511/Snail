package com.user.snail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultEGE extends AppCompatActivity {

    RecyclerView categoriesView;
    static Context context;
    String profession, professionFile;
    FirebaseFirestore db;
    static ArrayList<String> exams = new ArrayList<>();
    static ArrayList<ArrayList> results = new ArrayList<>();

    public static class MyViewModel extends ViewModel{
        MutableLiveData<List<ResultEGE.Subcategory>> data;

        public LiveData<List<ResultEGE.Subcategory>> getData(){
            if(data == null){
                data = new MutableLiveData<>();
                loadData();
            }
            return data;
        }

        private void loadData(){
            data.postValue(ResultEGE.getRandomData());
        }
    }

    static List<ResultEGE.Subcategory> getRandomData() {
        final ArrayList<ResultEGE.Subcategory> subcategoryList = new ArrayList<>();

        results.clear();
        for(int i2=0;i2<exams.size();i2++){
            final int i=i2;
            subcategoryList.add(new ResultEGE.Subcategory(exams.get(i)));
            ArrayList result = new ArrayList();
            result.add(exams.get(i));
            result.add(0);
            result.add(false);
            results.add(result);
        }
        return subcategoryList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        context=this;

        profession = getIntent().getStringExtra("profession");
        professionFile = getIntent().getStringExtra("professionFIle");

        setTitle("Предметы для подготовки");

        db = FirebaseFirestore.getInstance();
        DocumentReference fb = db.collection("test").document("professions");
        fb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        final Map<String, Object> m = documentSnapshot.getData();
                        if(m.get(profession)==null){
                            exams=new ArrayList<>();
                            exams.add("Математика");
                            exams.add("Русский язык");
                        }else {
                            exams = (ArrayList) m.get(profession);
                        }
                    }else{
                        exams=new ArrayList<>();
                        exams.add("Математика");
                        exams.add("Русский язык");
                    }
                    createRecycleView();
                }
            }
        });
    }

    private void createRecycleView() {
        MyViewModel model = new ViewModelProvider(ResultEGE.this).get(MyViewModel.class);

        LiveData<List<Subcategory>> data = model.getData();

        data.observe(ResultEGE.this, new Observer<List<Subcategory>>() {
            @Override
            public void onChanged(List<Subcategory> subcategories) {
            }
        });
        FloatingActionButton fab=findViewById(R.id.go);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCan = true;
                for(int i = 0;i < results.size();i++){
                    if(!((Boolean) results.get(i).get(2))){
                        Log.e("result",String.valueOf(i));
                        isCan = false;
                    }
                }
                if(isCan){
                    Intent starter = new Intent(ResultEGE.this, MainResult.class);
                    starter.putExtra("value", results);
                    starter.putExtra("profession", profession);
                    startActivity(starter);
                }
                else{
                    Toast.makeText(context, "Введите все значения баллов", Toast.LENGTH_SHORT).show();
                }
            }
        });

        categoriesView=findViewById(R.id.categories_list_theme_test3);

        List<Subcategory> CircleCategories = ResultEGE.getRandomData();
        categoriesView.setHasFixedSize(true);
        categoriesView.setLayoutManager(new LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
        ));
        categoriesView.setAdapter(new SubcategoriesAdapter(CircleCategories));
    }


    static class Subcategory {
        String name;
        int all_taskTheme;

        Subcategory(String name)
        {
            this.name = name;
        }
    }




    static class SubcategoriesAdapter extends RecyclerView.Adapter<ResultEGE.SubcategoriesAdapter.ViewHolder> {

        List<ResultEGE.Subcategory> data;

        SubcategoriesAdapter(List<ResultEGE.Subcategory> data) {
            this.data = data;
        }

        @Override
        public ResultEGE.SubcategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subcategory_list_item_test3, parent, false);
            return new ResultEGE.SubcategoriesAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ResultEGE.SubcategoriesAdapter.ViewHolder holder, final int position) {
            final ResultEGE.Subcategory subcategory = data.get(position);

            holder.subcategoryName.setText(subcategory.name);

            holder.subcategoryText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(!holder.subcategoryText.getText().toString().equals("")) {
//                        Toast.makeText(holder.itemView.getContext(),"Text",Toast.LENGTH_LONG).show();
                        results.get(position).set(1, holder.subcategoryText.getText().toString());
                        results.get(position).set(2,true);
                    }

                    return false;
                }
            });

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView subcategoryName;
            TextInputEditText subcategoryText;

            ViewHolder(View itemView) {
                super(itemView);
                subcategoryName = itemView.findViewById(R.id.textViewName);
                subcategoryText = itemView.findViewById(R.id.textViewNameOlymp);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}