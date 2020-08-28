package com.user.snail;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainResult extends AppCompatActivity {

    RecyclerView categoriesView;
    ArrayList<ArrayList> results = new ArrayList();
    ArrayList fac = new ArrayList();
    static ArrayList<String> arrayList = new ArrayList<>();
    static String ans1, ans2, profession;
    FirebaseFirestore db;
    Context context;
    View view;
    int cnt2;

    public static class MyViewModel extends ViewModel {
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

    static List<Subcategory> getRandomData() {
        final ArrayList<Subcategory> subcategoryList = new ArrayList<>();

        for(int i2=0;i2< arrayList.size();i2++){
            final int i=i2;
            subcategoryList.add(new Subcategory(arrayList.get(i), profession));
        }
        return subcategoryList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test5);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Результат");
        context=this;

        results = ResultEGE.results;
        ans1 = getIntent().getStringExtra("ans1");
        ans2 = getIntent().getStringExtra("ans2");
        profession = getIntent().getStringExtra("profession");
        Log.e("result", String.valueOf(results.size()));

        db = FirebaseFirestore.getInstance();
        db.collection("institutes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                final int len = task2.getResult().size();
                cnt2 = 0;
                for(final QueryDocumentSnapshot documentTask : task2.getResult()){
                    final DocumentReference dr = db.collection("institutes").document(documentTask.getId());
                    dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists()){
                                    Map<String,Object> m = documentSnapshot.getData();
                                    int cnt = 0;
                                    int sum = 0;
                                    Log.e("LOG", profession);
//                                    if(m.get(profession) != null){
//                                        fac = (ArrayList) m.get(profession);
//                                        for(int i = 0;i < fac.size()-1;i++){
//                                            for (int j = 0;j < results.size();j++){
//                                                if(results.get(j).get(0).toString().equals(fac.get(i).toString())){
//                                                    sum += Integer.valueOf(results.get(j).get(1).toString());
//                                                    cnt++;
//                                                }
//                                            }
//                                        }
//                                        if(cnt >= fac.size()-1 && sum >= Integer.valueOf(fac.get(fac.size()-1).toString())){
//                                            arrayList.add(documentSnapshot.getId());
//                                        }
//                                    }
//                                    else{
                                        ArrayList prof = new ArrayList();
                                        prof.add("Русский язык");
                                        prof.add("Математика");
                                        prof.add("Физика");
                                        prof.add((int)Math.random()*150+150);
                                        m.put(profession, prof);
                                    ArrayList prof1 = new ArrayList();
                                    prof1.add("Русский язык");
                                    prof1.add("Математика");
                                    prof1.add("Физика");
                                    prof1.add((int)Math.random()*150+150);
                                    m.put("Информационная безопасность", prof);
                                    dr.set(m, SetOptions.merge());
                                        arrayList.add(documentSnapshot.getId());
//                                    }
//                                    Log.e("LOG", "cnt " + cnt);
//                                    Log.e("LOG", "sum " + sum);
//                                    Log.e("LOG", fac.get(0).toString());
                                    if (len - 1 == cnt2) {
                                        categoriesView=findViewById(R.id.categories_list_theme_test5);

                                        List<Subcategory> CircleCategories = MainResult.getRandomData();
                                        categoriesView.setHasFixedSize(true);
                                        categoriesView.setLayoutManager(new LinearLayoutManager(
                                                context,
                                                RecyclerView.VERTICAL,
                                                false
                                        ));
                                        categoriesView.setAdapter(new SubcategoriesAdapter(CircleCategories));
                                    }
                                    cnt2++;
                                }
                            }
                        }
                    });
                }
            }
        });
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

    static class Subcategory {
        String name, fac;
        int all_taskTheme;

        Subcategory(String name, String fac)
        {
            this.name = name;
            this.fac = fac;
        }
    }




    static class SubcategoriesAdapter extends RecyclerView.Adapter<SubcategoriesAdapter.ViewHolder> {

        List<Subcategory> data;

        SubcategoriesAdapter(List<Subcategory> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subcategory_list_item_test5, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Subcategory subcategory = data.get(position);

            holder.subcategoryName.setText(subcategory.name);
            holder.subcategoryText.setText("Ваше направление: " + subcategory.fac);

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView subcategoryName,subcategoryText;

            ViewHolder(View itemView) {
                super(itemView);
                subcategoryName = itemView.findViewById(R.id.university);
                subcategoryText = itemView.findViewById(R.id.faculty);
            }
        }
    }

}

/*
    Join us now and share the software;
    You’ll be free, hackers, you’ll be free.
    Hoarders can get piles of money,
    That is true, hackers, that is true.
    But they cannot help their neighbors;
    That’s not good, hackers, that’s not good.
 */